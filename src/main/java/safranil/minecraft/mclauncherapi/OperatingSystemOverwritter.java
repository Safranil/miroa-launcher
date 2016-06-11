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

import sk.tomsik68.mclauncher.api.common.IOperatingSystem;
import sk.tomsik68.mclauncher.api.common.MCLauncherAPI;
import sk.tomsik68.mclauncher.impl.common.Platform;

import java.io.File;
import java.io.IOException;

public class OperatingSystemOverwritter implements IOperatingSystem {
    private final IOperatingSystem os;
    private File workDir;

    public OperatingSystemOverwritter(IOperatingSystem os) {
        this.os = os;
    }

    @Override
    public String getDisplayName() {
        return os.getDisplayName();
    }

    @Override
    public String getMinecraftName() {
        return os.getMinecraftName();
    }

    @Override
    public boolean isCurrent() {
        return os.isCurrent();
    }

    @Override
    public File getWorkingDirectory() {
        if(this.workDir == null) {
            String path;
            try {
                path = (new File(Platform.getCurrentPlatform().getWorkingDirectory() + "/..")).getCanonicalPath();
            } catch (IOException e) {
                path = (new File(Platform.getCurrentPlatform().getWorkingDirectory() + "/..")).getAbsolutePath();
            }
            workDir = new File(path + "/.miroa");
            MCLauncherAPI.log.info("Miroa working directory: ".concat(this.workDir.getAbsolutePath()));
        }
        return this.workDir;
    }

    @Override
    public String getArchitecture() {
        return os.getArchitecture();
    }
}
