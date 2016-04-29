package safranil.minecraft.mclauncherapi;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;

public class InstallProgressMonitor implements IProgressMonitor {
    private double progress;
    private double max;
    private final ProgressIndicator progressIndicator;
    private final Label label;

    private boolean canUpdateGUI = true;

    public InstallProgressMonitor(ProgressIndicator progressIndicator, Label label) {
        this.progressIndicator = progressIndicator;
        this.label = label;
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
        PlatformImpl.runLater(() -> label.setText(s));
    }

    private void updateGUI() {
        if (max > 0 && (canUpdateGUI || progress == max)) {
            canUpdateGUI = false;
            PlatformImpl.runLater(() -> {
                progressIndicator.setProgress(progress / max);
                canUpdateGUI = true;
            });
        }
    }
}

