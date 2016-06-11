package safranil.minecraft.mclauncherapi;

import sk.tomsik68.mclauncher.api.mods.IModdingProfile;

import java.io.File;
import java.util.List;

public class ModdingProfile implements IModdingProfile {
    @Override
    public File[] injectBeforeLibs(String s) {
        return new File[0];
    }

    @Override
    public File[] injectAfterLibs(String s) {
        return new File[0];
    }

    @Override
    public boolean isLibraryAllowed(String s) {
        return false;
    }

    @Override
    public File getCustomGameJar() {
        return null;
    }

    @Override
    public String getMainClass() {
        return null;
    }

    @Override
    public String[] changeMinecraftArguments(String[] strings) {
        return new String[0];
    }

    @Override
    public List<String> getLastParameters() {
        return null;
    }
}
