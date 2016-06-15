/**
 * This file is part of Miroa Launcher.
 * Copyright (C) 2016 David Cachau <dev@safranil.fr>
 *
 * Miroa Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Miroa Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Miroa Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;

import java.net.InetAddress;

/**
 * Thread to do some actions at the startup (Mojang is online, Mojang token verification, ...)
 */
class Starter extends Thread {
    Starter() {

    }

    @Override
    public void run() {
        try {
            /*InetAddress inetAddress = InetAddress.getByName("authserver.mojang.com");
            System.out.println("Mojang authserver at " + inetAddress.getHostAddress());
            if (!inetAddress.isReachable(10000)) {
                Utils.displayError("Serveur indisponible", "Le serveur de connexion de Mojang est indisponible.", "La connexion au serveur de connexion est impossible. Verifiez votre connexion à Internet et réessayez.");
                PlatformImpl.exit();
            }*/

            MiroaLauncher launcher = MiroaLauncher.getInstance();

            if (MiroaLauncher.OS.getWorkingDirectory().isFile()) {
                MiroaLauncher.OS.getWorkingDirectory().delete();
            }

            if (!MiroaLauncher.OS.getWorkingDirectory().exists()) {
                MiroaLauncher.OS.getWorkingDirectory().mkdir();
            }

            if (!MiroaLauncher.OS.getWorkingDirectory().canRead() || !MiroaLauncher.OS.getWorkingDirectory().canWrite() || !MiroaLauncher.OS.getWorkingDirectory().isDirectory()) {
                MiroaLauncher.LOGGER.severe("Unable to read/write on game directory");
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
            MiroaLauncher.LOGGER.info("Unable to start the launcher");
            e.printStackTrace();
            Utils.displayException("Impossible de lancer le launcher",
                    "Une erreur est survenue lors du lancement du launcher.", e);
            PlatformImpl.exit();
        }
    }
}
