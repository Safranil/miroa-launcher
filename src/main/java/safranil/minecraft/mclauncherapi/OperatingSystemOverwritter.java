package safranil.minecraft.mclauncherapi;

import sk.tomsik68.mclauncher.api.common.IOperatingSystem;
import sk.tomsik68.mclauncher.api.common.MCLauncherAPI;
import sk.tomsik68.mclauncher.impl.common.Platform;

import java.io.File;
import java.io.IOException;

public class OperatingSystemOverwritter implements IOperatingSystem {
    private IOperatingSystem os;
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
            String path = null;
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
