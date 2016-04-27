package safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;

class InstallProgressMonitor implements IProgressMonitor {
    private double progress;
    private double max;
    private MainController controller;

    private boolean canUpdateGUI = true;

    InstallProgressMonitor(MainController controller) {
        this.controller = controller;
    }


    @Override
    public void setProgress(int i) {
        progress = i;
        updateGUI();
    }

    @Override
    public void setMax(int i) {
        max = i;
    }

    @Override
    public void incrementProgress(int i) {
        progress++;
        updateGUI();
    }

    @Override
    public void setStatus(String s) {
        PlatformImpl.runLater(() -> {
            controller.subInfoLabel.setText(s);
        });
    }

    private void updateGUI() {
        if (max > 0 && (canUpdateGUI || progress == max)) {
            canUpdateGUI = false;
            PlatformImpl.runLater(() -> {
                controller.progress.setProgress(progress / max);
                canUpdateGUI = true;
            });
        }
    }
}

