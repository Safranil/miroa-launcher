package safranil.minecraft.miroa;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openauth.model.response.RefreshResponse;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class MiroaLauncher {
    private static final String CLIENT_TOKEN = "MiroaLauncher";
    public static final String DEFAULT_MEMORY = "2048M";
    public static final int DEFAULT_MEMORY_ID = 4;
    public static String DEFAULT_JAVA = getDefaultJava();

    static MiroaLauncher self;
    MainController mainController;

    static ArrayList<MemoryOption> memoryOptions = new ArrayList<>();

    private Saver saver = new Saver(new File("launcher.properties"));
    Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);

    private String accessToken;

    /**
     * Make class as Singleton
     */
    private MiroaLauncher() {
        // Set up memory options
        memoryOptions.add(0, new MemoryOption("1024M", "1 Gio"));
        memoryOptions.add(1, new MemoryOption("1280M", "1.25 Gio"));
        memoryOptions.add(2, new MemoryOption("1526M", "1.5 Gio"));
        memoryOptions.add(3, new MemoryOption("1792M", "1.75 Gio"));
        memoryOptions.add(DEFAULT_MEMORY_ID, new MemoryOption(DEFAULT_MEMORY, "2 Gio"));
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
    public static MiroaLauncher getInstance() {
        if (self == null) {
            self = new MiroaLauncher();
        }
        return self;
    }

    public void auth(String username, String password) throws AuthenticationException {
        saver.set("username", username);

        AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, CLIENT_TOKEN);
        accessToken = response.getAccessToken();
        saver.set("accessToken", accessToken);
    }

    public boolean refreshToken() throws AuthenticationException {
        String token = saver.get("accessToken");

        if (token == null || "".equals(token)) {
            return false;
        }

        RefreshResponse response = authenticator.refresh(token, CLIENT_TOKEN);
        accessToken = response.getAccessToken();
        saver.set("accessToken", accessToken);

        return true;
    }

    public String getMemory() {
        String memory = saver.get("memory");
        if (checkMemory(memory)) {
            return memory;
        } else {
            return DEFAULT_MEMORY;
        }
    }

    public boolean setMemory(String memory) {
        if (checkMemory(memory)) {
            saver.set("memory", memory);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkMemory(String memory) {
        for(MemoryOption memOpt:memoryOptions) {
            if (memOpt.getJavaOption().equals(memory)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoggedIn() {
        return accessToken != null;
    }

    public void logout() {
        try {
            authenticator.invalidate(accessToken, CLIENT_TOKEN);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        accessToken = null;
        saver.set("accessToken", "");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public String getUsername() {
        return saver.get("username");
    }

    public String getJavaBin() {
        String java = saver.get("java");
        if (java != null && !"".equals(java) && checkJavaBin(java)) {
            return java;
        } else {
            return DEFAULT_JAVA;
        }
    }

    public boolean setJavaBin(String java) {
        if (checkJavaBin(java)) {
            saver.set("java", java);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkJavaBin(String java) {
        File file = new File(java);
        return file.exists() && file.canExecute();
    }

    private static String getDefaultJava() {
        String java = JavaUtil.getJavaCommand();
        if (java.startsWith("\"")) {
            java = java.substring(1);
        }
        if (java.endsWith("\"")) {
            java = java.substring(0, java.length()-1);
        }
        return java;
    }
}
