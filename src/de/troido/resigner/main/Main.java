package de.troido.resigner.main;

import de.troido.resigner.controll.ReSignerLogic;
import de.troido.resigner.ui.MainWindow;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            new MainWindow();
        if (args.length == 2) {
            try {
                ReSignerLogic.checkEnvironment();
            } catch (RuntimeException exc) {
                System.err.println(exc.getMessage());
                System.exit(0);
            }
            String[] result = ReSignerLogic.resign(args[0], args[1]);
            if (result != null) {
                System.out
                        .println("apk successfully re-signed\n\nPackage name: "
                                + result[0] + "\nMain activity:" + result[1]);
            }
        }
    }
}