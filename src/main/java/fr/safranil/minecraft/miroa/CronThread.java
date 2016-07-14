package fr.safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;

/**
 * Automatically execute actions repeatedly
 */
class CronThread extends Thread {
    @Override
    public void run() {
        MiroaLauncher launcher = MiroaLauncher.getInstance();
        ServerQuery serverQuery = new ServerQuery();

        try {
            while (true) {
                serverQuery.getPlayerCount();
                PlatformImpl.runLater(() -> launcher.mainController.playerCount.setText(serverQuery.getCount() + "/" + serverQuery.getMax()));

                Thread.sleep(60000);
            }
        } catch (InterruptedException ignored) {
        }
    }
}
