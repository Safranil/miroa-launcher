package safranil.minecraft.miroa;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openauth.model.response.RefreshResponse;
import safranil.minecraft.mclauncherapi.OperatingSystemOverwritter;
import sk.tomsik68.mclauncher.api.common.IOperatingSystem;
import sk.tomsik68.mclauncher.api.common.mc.MinecraftInstance;
import sk.tomsik68.mclauncher.api.login.ISession;
import sk.tomsik68.mclauncher.backend.GlobalAuthenticationSystem;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.login.legacy.LegacyLoginService;
import sk.tomsik68.mclauncher.impl.login.legacy.LegacyProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDLoginService;

import java.io.File;
import java.util.ArrayList;

public class MiroaLauncher {
    private static IOperatingSystem OS = new OperatingSystemOverwritter(Platform.getCurrentPlatform());

    public static final String DEFAULT_MEMORY = "2048M";
    public static final int DEFAULT_MEMORY_ID = 4;
    private static final String CLIENT_TOKEN = "MiroaLauncher";

    public static final String DEFAULT_JAVA = getDefaultJava();
    public static final File LAUNCHER_FOLDER = OS.getWorkingDirectory();

    MainController mainController;

    static ArrayList<MemoryOption> memoryOptions = new ArrayList<>();

    private OptionSaver optionSaver = new OptionSaver(new File(LAUNCHER_FOLDER.getPath() + "/launcher.properties"));
    Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);

    private String accessToken;

    MinecraftInstance mc = new MinecraftInstance(MiroaLauncher.LAUNCHER_FOLDER);

    private static MiroaLauncher self = new MiroaLauncher();

    /**
     * Make class as Singleton
     */
    private MiroaLauncher() {
        Platform.forcePlatform(OS);

        // Set up memory options
        memoryOptions.add(0, new MemoryOption("1024M", "1 Gio"));
        memoryOptions.add(1, new MemoryOption("1280M", "1.25 Gio"));
        memoryOptions.add(2, new MemoryOption("1526M", "1.5 Gio"));
        memoryOptions.add(3, new MemoryOption("1792M", "1.75 Gio"));
        memoryOptions.add(4, new MemoryOption("2048M", "2 Gio"));
        memoryOptions.add(5, new MemoryOption("2560M", "2.5 Gio"));
        memoryOptions.add(6, new MemoryOption("3072M", "3 Gio"));
        memoryOptions.add(7, new MemoryOption("3584M", "3.5 Gio"));
        memoryOptions.add(8, new MemoryOption("4096M", "4 Gio"));
    }

    /**
     * Get or create the unique Miroa launcher instance
     *
     * @return singleton instance
     */
    static MiroaLauncher getInstance() {
        return self;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void auth(String username, String password) throws AuthenticationException {
        optionSaver.set("username", username);

        AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, CLIENT_TOKEN);
        accessToken = response.getAccessToken();
        optionSaver.set("accessToken", accessToken);
    }

    public boolean refreshToken() throws AuthenticationException {
        String token = optionSaver.get("accessToken");

        if (token == null || "".equals(token)) {
            return false;
        }

        RefreshResponse response = authenticator.refresh(token, CLIENT_TOKEN);
        accessToken = response.getAccessToken();
        optionSaver.set("accessToken", accessToken);

        return true;
    }

    public boolean login() {
        try {
            String[] authProfileNames = GlobalAuthenticationSystem.getProfileNames();
            if (authProfileNames.length == 0) {
                return false;
            }
            ISession loginSession = GlobalAuthenticationSystem.login(authProfileNames[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean login(String username, String password) throws Exception {
        //LegacyLoginService loginService = new LegacyLoginService();
        YDLoginService loginService = new YDLoginService();
        ISession session = loginService.login(new LegacyProfile(username, password));

        return true;
    }

    public void logout() {
        try {
            authenticator.invalidate(accessToken, CLIENT_TOKEN);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        accessToken = null;
        optionSaver.remove("accessToken");
    }

    public boolean isLoggedIn() {
        return accessToken != null;
    }

    public String getUsername() {
        return optionSaver.get("username");
    }


    public String getMemory() {
        String memory = optionSaver.get("memory");
        if (checkMemory(memory)) {
            return memory;
        } else {
            return DEFAULT_MEMORY;
        }
    }

    public boolean setMemory(String memory) {
        if (checkMemory(memory)) {
            if (DEFAULT_MEMORY.equals(memory))
                optionSaver.remove("memory");
            else
                optionSaver.set("memory", memory);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkMemory(String memory) {
        for (MemoryOption memOpt : memoryOptions) {
            if (memOpt.getJavaOption().equals(memory)) {
                return true;
            }
        }
        return false;
    }

    private static String getDefaultJava() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return System.getProperty("java.home") + "\\bin\\java.exe";
        } else {
            return System.getProperty("java.home") + "/bin/java";
        }
    }

    public String getJavaBin() {
        String java = optionSaver.get("java");
        if (java != null && !"".equals(java) && checkJavaBin(java)) {
            return java;
        } else {
            return DEFAULT_JAVA;
        }
    }

    public boolean setJavaBin(String java) {
        if (checkJavaBin(java)) {
            if (DEFAULT_JAVA.equals(java))
                optionSaver.remove("java");
            else
                optionSaver.set("java", java);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkJavaBin(String java) {
        File file = new File(java);
        return file.exists() && file.canExecute();
    }
}
