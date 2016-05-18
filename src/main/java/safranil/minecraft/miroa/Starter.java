package safranil.minecraft.miroa;

import com.sun.java.swing.plaf.motif.resources.motif_ja;
import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.InetAddress;

class Starter extends Thread {
    Starter() {

    }

    @Override
    public void run() {
        try {
            InetAddress inetAddress = InetAddress.getByName("authserver.mojang.com");
            if (!inetAddress.isReachable(10000)) {
                Utils.displayError("Serveur indisponible", "Le serveur de connexion de Mojang est indisponible.", "La connexion au serveur de connexion est impossible. Verifiez votre connexion à Internet et réessayez.");
                PlatformImpl.exit();
            }

            MiroaLauncher launcher = MiroaLauncher.getInstance();

            if (MiroaLauncher.OS.getWorkingDirectory().isFile()) {
                MiroaLauncher.OS.getWorkingDirectory().delete();
            }

            if (!MiroaLauncher.OS.getWorkingDirectory().exists()) {
                MiroaLauncher.OS.getWorkingDirectory().mkdir();
            }

            if (!MiroaLauncher.OS.getWorkingDirectory().canRead() || !MiroaLauncher.OS.getWorkingDirectory().canWrite() || !MiroaLauncher.OS.getWorkingDirectory().isDirectory()) {
                Utils.displayError("Erreur dossier de jeu", "Le dossier de stockage n'est pas accessible.", String.format(
                        "canRead : %s\ncanWrite : %s\nisDirectory : %s",
                        MiroaLauncher.OS.getWorkingDirectory().canRead(),
                        MiroaLauncher.OS.getWorkingDirectory().canWrite(),
                        MiroaLauncher.OS.getWorkingDirectory().isDirectory()
                ));
                PlatformImpl.exit();
            }

            PlatformImpl.runLater(() -> launcher.mainController.webPreview.getEngine().load("http://minecraft.safranil.fr/"));


            if (launcher.login()) {
                PlatformImpl.runLater(() -> launcher.mainController.setToPlay());
            } else {
                PlatformImpl.runLater(() -> launcher.mainController.setToLogin());
            }

            String username = launcher.getUsername();
            if (username != null) {
                PlatformImpl.runLater(() -> launcher.mainController.loginField.setText(username));
            }

            PlatformImpl.runLater(() -> launcher.mainController.loading.setVisible(false));
        } catch (Exception e) {
            Utils.displayException("Impossible de lancer le launcher",
                    "Une erreur est survenue lors du lancement du launcher.", e);
            PlatformImpl.exit();
        }
    }
}
