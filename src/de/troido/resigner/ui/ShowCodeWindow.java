package de.troido.resigner.ui;

import java.awt.Container;
import java.awt.HeadlessException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Created by pengwei08 on 2015/7/30.
 */
public class ShowCodeWindow extends BaseJFrame {

    private JTextArea jTextArea;
    private String packageName;
    private String SplashActivity;
    private String appName;

    public ShowCodeWindow(String packageName, String splashActivity) throws HeadlessException {
        this.packageName = packageName;
        SplashActivity = splashActivity;
        appName = packageName.substring(packageName.lastIndexOf(".")+1, packageName.length());
        appName = firsUpperCase(appName)+"Test";
        init();
    }

    /**
     * 首字母大写
     * @param s
     * @return
     */
    private String firsUpperCase(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public void init(){
        setTitle("基础代码生成");
        Container container = getContentPane();
        jTextArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(jTextArea);
        jTextArea.setText("import com.robotium.solo.Solo;\n"
                + "import android.test.ActivityInstrumentationTestCase2;\n"
                + "import android.test.AndroidTestCase;\n"
                + "\n"
                + "/**\n"
                + " * Created by pengwei08 on 2015/7/5.\n"
                + " */\n"
                + "public class "+appName+" extends ActivityInstrumentationTestCase2 {\n"
                + "    private static String mainActivity = \""+SplashActivity+"\";\n"
                + "    private Solo solo;\n"
                + "    private static Class<?> launchActivityClass;\n"
                + "    static {\n"
                + "        try {\n"
                + "            launchActivityClass = Class.forName(mainActivity);\n"
                + "        } catch (ClassNotFoundException e) {\n"
                + "            e.printStackTrace();\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public "+appName+"() {\n"
                + "        super(launchActivityClass);\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public void setUp() throws Exception {\n"
                + "        super.setUp();\n"
                + "        solo = new Solo(getInstrumentation(), getActivity());\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public void tearDown() throws Exception {\n"
                + "        super.tearDown();\n"
                + "        solo.finishOpenedActivities();\n"
                + "    }\n"
                + "}\n");
        container.add(scroll);
        setSize(800,500);
        setLocationRelativeTo(getRootPane());
    }
}
