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
package de.troido.resigner.main;

import de.troido.resigner.controll.ResignerLogic;
import de.troido.resigner.ui.MainWindow;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length == 0)
			new MainWindow();

		if (args.length == 2) {
			try {
				ResignerLogic.checkEnvironment();
			} catch (RuntimeException exc) {
				System.err.println(exc.getMessage());
				System.exit(0);
			}
			String[] result = ResignerLogic.resign(args[0], args[1]);
			if (result != null) {
				System.out
						.println("apk successfully re-signed\n\nPackage name: "
								+ result[0] + "\nMain activity:" + result[1]);
			}
		}
	}
}
