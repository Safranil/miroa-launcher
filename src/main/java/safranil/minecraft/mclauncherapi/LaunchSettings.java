package safranil.minecraft.mclauncherapi;

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
