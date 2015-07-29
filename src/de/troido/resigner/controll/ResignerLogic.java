/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.troido.resigner.controll;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;
import de.troido.resigner.utils.PropertiesUtil;

public class ResignerLogic {

	private static String jarsignerpath;
	private static String zipalignpath;

	public static void checkEnvironment() {
		String javahome = System.getenv("JAVA_HOME");
		String androidhome = System.getenv("ANDROID_HOME");
		if (javahome == null)
			throw new RuntimeException(
					"JAVA_HOME not set. Please install Java SDK and set the JAVA_HOME environment variable");
		if (androidhome == null)
			throw new RuntimeException(
					"ANDROID_HOME not set. Please install Android SDK and set the ANDROID_HOME environment variable");
//		jarsignerpath = new File(javahome).getAbsolutePath() + "/bin/jarsigner";
		jarsignerpath = "jarsigner";

		zipalignpath = new File(androidhome).getAbsolutePath()
				+ "/tools/zipalign";
		// if (!new File(jarsignerpath).exists()) throw new
		// RuntimeException("Could not find jarsigner in JAVA_HOME/bin, please check if the path is correct");

	}

	public static String join(String cmd[]) {
		StringBuilder sb = new StringBuilder();
		for (String s : cmd)
			sb.append(s + " ");
		return sb.toString();
	}

	public static void zipAlign(String inputFile, String outputFile)
			throws Exception {
		if (!new File(zipalignpath+".exe").exists()) throw new
				RuntimeException("找不到zipalign文件，请从sdk/build-tools/任意版本复制一个zipalign.exe到tools文件夹下");
		String cmdLine[] = { zipalignpath, "-f", "4", inputFile, outputFile };
		System.out.println("Running zipalign\nCommand line: " + join(cmdLine));
		Process proc = Runtime.getRuntime().exec(cmdLine);
		proc.waitFor();
		InputStream err = proc.getErrorStream();
		InputStream in = proc.getInputStream();
		System.out.println("zipalign finished with following output:");
		while (err.available() > 0)
			System.out.print((char) err.read());
		while (in.available() > 0)
			System.out.print((char) in.read());
	}

	public static void signWithDebugKey(String inputFile) throws Exception {
		String userDir = System.getProperty("user.home");
		String userDebugKeyStore = userDir + "/.android/debug.keystore";
		String debugKeyStore = System.getenv("ANDROID_HOME")+"/.android/debug.keystore";
		if(!new File(debugKeyStore).exists()){
			if(new File(userDebugKeyStore).exists()){
				debugKeyStore = userDebugKeyStore;
			}else{
				if(new File(PropertiesUtil.get("debug.keystore")).exists()){
					debugKeyStore = PropertiesUtil.get("debug.keystore");
				}else{
					throw new RuntimeException(
							"please set the debug.keystore path.");
				}
			}
		}
		PropertiesUtil.put("debug.keystore", debugKeyStore);
		String cmdLine[] = { jarsignerpath, "-digestalg", "SHA1", "-sigalg", "MD5withRSA",
				 "-keystore", debugKeyStore, "-storepass",
				"android", "-keypass", "android", inputFile, "androiddebugkey" };
		System.out.println("Running jarsigner\nCommand line: " + join(cmdLine));
		Process proc = Runtime.getRuntime().exec(cmdLine);
		proc.waitFor();
		InputStream err = proc.getErrorStream();
		InputStream in = proc.getInputStream();
		System.out.println("jarsigner finished with following output:");
		while (err.available() > 0)
			System.out.print((char) err.read());
		while (in.available() > 0)
			System.out.print((char) in.read());
	}

	public static String[] stripSigning(String inputFile, String outputFile)
			throws Exception {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile));
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
				outputFile));
		ZipEntry entry = null;
		String resultString[] = null;
		while ((entry = zis.getNextEntry()) != null) {
			if (entry.getName().contains("META-INF"))
				continue;
			zos.putNextEntry(new ZipEntry(entry.getName()));
			int size;
			ByteBuffer bb = ByteBuffer.allocate(500000);
			byte[] buffer = new byte[2048];
			while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
				zos.write(buffer, 0, size);
				if (entry.getName().endsWith("AndroidManifest.xml")) {
					bb.put(buffer, 0, size);
				}
			}
			zos.flush();
			zos.closeEntry();
			if (bb.position() > 0) {

				buffer = new byte[bb.position()];
				bb.rewind();
				bb.get(buffer);
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				docFactory.setNamespaceAware(true); // never forget this!
				docFactory.setIgnoringComments(true);
				docFactory.setIgnoringElementContentWhitespace(true);

				DocumentBuilder builder = docFactory.newDocumentBuilder();
				String packageName = "";
				String mainActivity = "";
				String docStr = AXMLToXML(buffer).replaceAll("\n", "");
				Pattern pattern = Pattern
						.compile("<activity.*?android:name=\"(.*?)\".*?</activity>");
				Matcher m = pattern.matcher(docStr);
				while (m.find()) {
					String result = m.group();
					if (result.contains("android.intent.action.MAIN")
							&& result
									.contains("android.intent.category.LAUNCHER")) {
						mainActivity = (m.group(1));

					}
				}

				pattern = Pattern.compile("<manifest.*?package=\"(.*?)\"");
				m = pattern.matcher(docStr);
				if (m.find()) {
					packageName = m.group(1);
				}
				mainActivity = mainActivity.replaceAll(packageName, "");
				resultString = new String[] { packageName,
						packageName + mainActivity };
			}
		}

		zis.close();
		zos.close();
		return resultString;
	}

	public static String[] resign(String in, String out) throws Exception {

		ResignerLogic rl = new ResignerLogic();
		File f = File.createTempFile("resigner", ".apk");
		String[] result = stripSigning(in, f.getAbsolutePath());
		signWithDebugKey(f.getAbsolutePath());
		zipAlign(f.getAbsolutePath(), out);
		return result;
	}

	private static String getNamespacePrefix(String prefix) {
		if (prefix == null || prefix.length() == 0) {
			return "";
		}
		return prefix + ":";
	}

	private static String getAttributeValue(AXmlResourceParser parser, int index) {
		int type = parser.getAttributeValueType(index);
		int data = parser.getAttributeValueData(index);
		if (type == TypedValue.TYPE_STRING) {
			return parser.getAttributeValue(index);
		}
		if (type == TypedValue.TYPE_ATTRIBUTE) {
			return String.format("?%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_REFERENCE) {
			return String.format("@%s%08X", getPackage(data), data);
		}
		if (type == TypedValue.TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (type == TypedValue.TYPE_INT_HEX) {
			return String.format("0x%08X", data);
		}
		if (type == TypedValue.TYPE_INT_BOOLEAN) {
			return data != 0 ? "true" : "false";
		}
		if (type == TypedValue.TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data))
					+ DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type == TypedValue.TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))
					+ FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type >= TypedValue.TYPE_FIRST_COLOR_INT
				&& type <= TypedValue.TYPE_LAST_COLOR_INT) {
			return String.format("#%08X", data);
		}
		if (type >= TypedValue.TYPE_FIRST_INT
				&& type <= TypedValue.TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>", data, type);
	}

	private static String getPackage(int id) {
		if (id >>> 24 == 1) {
			return "android:";
		}
		return "";
	}

	public static String AXMLToXML(byte[] axml) throws Exception {
		AXmlResourceParser parser = new AXmlResourceParser();
		ByteArrayInputStream bais = new ByteArrayInputStream(axml);
		parser.open(bais);
		StringBuilder indent = new StringBuilder(10);
		StringBuilder output = new StringBuilder(axml.length * 2);
		final String indentStep = "   ";

		while (true) {
			int type = parser.next();
			if (type == XmlPullParser.END_DOCUMENT) {
				break;
			}
			switch (type) {
			case XmlPullParser.START_DOCUMENT: {
				output.append(String
						.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
				output.append("\n");
				break;
			}
			case XmlPullParser.START_TAG: {
				output.append(String.format("%s<%s%s", indent,
						getNamespacePrefix(parser.getPrefix()),
						parser.getName()));
				output.append("\n");
				indent.append(indentStep);

				int namespaceCountBefore = parser.getNamespaceCount(parser
						.getDepth() - 1);
				int namespaceCount = parser
						.getNamespaceCount(parser.getDepth());
				for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
					output.append(String.format("%sxmlns:%s=\"%s\"", indent,
							parser.getNamespacePrefix(i),
							parser.getNamespaceUri(i)));
					output.append("\n");
				}

				for (int i = 0; i != parser.getAttributeCount(); ++i) {
					output.append(String.format("%s%s%s=\"%s\"", indent,
							getNamespacePrefix(parser.getAttributePrefix(i)),
							parser.getAttributeName(i),
							getAttributeValue(parser, i)));
					output.append("\n");
				}
				output.append(String.format("%s>", indent));
				output.append("\n");
				break;
			}
			case XmlPullParser.END_TAG: {
				indent.setLength(indent.length() - indentStep.length());
				output.append(String.format("%s</%s%s>", indent,
						getNamespacePrefix(parser.getPrefix()),
						parser.getName()));
				output.append("\n");
				break;
			}
			case XmlPullParser.TEXT: {
				output.append(String.format("%s%s", indent, parser.getText()));
				output.append("\n");
				break;
			}
			}
		}
		return output.toString();
	}

	public static float complexToFloat(int complex) {
		return (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
	}

	private static final float RADIX_MULTS[] = { 0.00390625F, 3.051758E-005F,
			1.192093E-007F, 4.656613E-010F };
	private static final String DIMENSION_UNITS[] = { "px", "dip", "sp", "pt",
			"in", "mm", "", "" };
	private static final String FRACTION_UNITS[] = { "%", "%p", "", "", "", "",
			"", "" };
}
