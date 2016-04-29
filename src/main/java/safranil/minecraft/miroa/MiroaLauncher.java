package safranil.minecraft.miroa;

import safranil.minecraft.mclauncherapi.OperatingSystemOverwritter;
import sk.tomsik68.mclauncher.api.common.IOperatingSystem;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.login.ISession;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.login.legacy.LegacyProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDAuthProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDLoginService;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDProfileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class MiroaLauncher {
    static final IOperatingSystem OS = new OperatingSystemOverwritter(Platform.getCurrentPlatform());

    private static final String DEFAULT_MEMORY = "2048M";
    static final int DEFAULT_MEMORY_ID = 4;
    static final String MC_VERSION = "1.7.10";

    private static final String DEFAULT_JAVA = getDefaultJava();

    MainController mainController;

    static final ArrayList<MemoryOption> memoryOptions = new ArrayList<>();

    private final OptionSaver optionSaver = new OptionSaver(new File(OS.getWorkingDirectory() + "launcher.properties"));
    private IProfile[] profiles = {};
    ISession session;
    private boolean loggedIn = false;

    private static final MiroaLauncher self = new MiroaLauncher();

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

    void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    boolean login() {
        try {
            YDLoginService loginService = new YDLoginService();
            YDProfileIO io = new YDProfileIO(Platform.getCurrentPlatform().getWorkingDirectory());
            profiles = io.read();

            if (profiles.length > 0) {
                loginService.load(Platform.getCurrentPlatform().getWorkingDirectory());
                session = loginService.login(profiles[0]);
            }
            else {
                return false;
            }

            io.write(profiles);

            loggedIn = true;
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Profile file does not exist.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @SuppressWarnings("SameReturnValue")
    boolean login(String username, String password) throws Exception {
        YDLoginService loginService = new YDLoginService();
        session = loginService.login(new LegacyProfile(username, password));
        YDProfileIO io = new YDProfileIO(Platform.getCurrentPlatform().getWorkingDirectory());

        profiles = new IProfile[]{new YDAuthProfile(username, session.getUsername(), session.getSessionID(), session.getUUID(), session.getUUID())};

        io.write(profiles);
        loginService.save(Platform.getCurrentPlatform().getWorkingDirectory());
        io.write(profiles);

        loggedIn = true;
        return true;
    }

    void logout() throws Exception {
        YDLoginService loginService = new YDLoginService();
        YDProfileIO io = new YDProfileIO(Platform.getCurrentPlatform().getWorkingDirectory());
        try {
            loginService.logout(session);
        } catch (ClassCastException ignored) {
            /*
             See the issue here : https://github.com/tomsik68/mclauncher-api/issues/23
             This is a temporary workaround
             TODO: Remove this try catch
             */
        }
        io.write(new IProfile[]{});

        loggedIn = false;
    }

    boolean isLoggedIn() {
        return loggedIn;
    }

    String getUsername() {
        if (profiles.length > 0) {
            return profiles[0].getName();
        }
        return "";
    }


    String getMemory() {
        String memory = optionSaver.get("memory");
        if (checkMemory(memory)) {
            return memory;
        } else {
            return DEFAULT_MEMORY;
        }
    }

    boolean setMemory(String memory) {
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

    private boolean checkMemory(String memory) {
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

    String getJavaBin() {
        String java = optionSaver.get("java");
        if (java != null && !"".equals(java) && checkJavaBin(java)) {
            return java;
        } else {
            return DEFAULT_JAVA;
        }
    }

    boolean setJavaBin(String java) {
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

    boolean checkJavaBin(String java) {
        File file = new File(java);
        return file.exists() && file.canExecute();
    }
}
