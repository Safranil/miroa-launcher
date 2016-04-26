package safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;

class InstallProgressMonitor implements IProgressMonitor {
    private double progress;
    private double max;
    private MainController controller;

    InstallProgressMonitor(MainController controller) {
        this.controller = controller;
    }


    @Override
    public void setProgress(int i) {
        progress = i;
        if (max > 0)
            PlatformImpl.runLater(() -> {
                controller.progress.setProgress(progress / max);
            });
    }

    @Override
    public void setMax(int i) {
        max = i;
    }

    @Override
    public void incrementProgress(int i) {
        progress++;
        if (max > 0)
            PlatformImpl.runLater(() -> {
                controller.progress.setProgress(progress / max);
            });
    }

    @Override
    public void setStatus(String s) {
        PlatformImpl.runLater(() -> {
            controller.subInfoLabel.setText(s);
        });
    }
}

