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

