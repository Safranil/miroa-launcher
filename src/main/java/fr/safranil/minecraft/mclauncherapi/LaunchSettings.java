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
package fr.safranil.minecraft.mclauncherapi;

import sk.tomsik68.mclauncher.api.common.ILaunchSettings;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LaunchSettings implements ILaunchSettings {
    private final String maxHeap;
    private final File javaLocation;

    public LaunchSettings(String maxHeap, File javaLocation) {
        this.maxHeap = maxHeap;
        this.javaLocation = javaLocation;
    }

    @Override
    public String getInitHeap() {
        return "512M";
    }

    @Override
    public String getHeap() {
        return maxHeap;
    }

    @Override
    public Map<String, String> getCustomParameters() {
        return null;
    }

    @Override
    public List<String> getCommandPrefix() {
        return null;
    }

    @Override
    public boolean isModifyAppletOptions() {
        return false;
    }

    @Override
    public File getJavaLocation() {
        return javaLocation;
    }

    @Override
    public List<String> getJavaArguments() {
        return Arrays
                .asList("-XX:+UseConcMarkSweepGC",
                        "-XX:+CMSIncrementalMode",
                        "-XX:-UseAdaptiveSizePolicy",
                        "-Xmn128M");
    }
}
