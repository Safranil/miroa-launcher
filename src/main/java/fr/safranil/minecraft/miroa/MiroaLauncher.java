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
package fr.safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import fr.safranil.minecraft.mclauncherapi.OperatingSystemOverwritter;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.codec.binary.Base64;
import sk.tomsik68.mclauncher.api.common.IOperatingSystem;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.login.ISession;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.login.legacy.LegacyProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDAuthProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDLoginService;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDProfileIO;
import sk.tomsik68.mclauncher.util.HttpUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Launcher class that do some work and store some info for the launcher
 */
class MiroaLauncher {
    static final IOperatingSystem OS = new OperatingSystemOverwritter(Platform.getCurrentPlatform());

    private static final String DEFAULT_MEMORY = "2048M";
    static final int DEFAULT_MEMORY_ID = 4;
    static final String MC_VERSION = "1.7.10";
    static final String FORGE_VERSION = "1.7.10-Forge10.13.4.1614-1.7.10";

    private static final String DEFAULT_JAVA = getDefaultJava();
    public static final String SERVER_IP = "mc.safranil.fr";
    public static final int SERVER_PORT = 25565;

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

    /**
     * Do the login work
     * @return authenticated via the Mojang service
     */
    boolean login() {
        try {
            YDLoginService loginService = new YDLoginService();
            YDProfileIO io = new YDProfileIO(Platform.getCurrentPlatform().getWorkingDirectory());
            profiles = io.read();

            // Check if a profile exist and take the first item (the launcher use and store only one profile)
            if (profiles.length > 0) {
                loginService.load(Platform.getCurrentPlatform().getWorkingDirectory());
                session = loginService.login(profiles[0]);
            }
            else {
                return false;
            }

            io.write(profiles);

            loggedIn = true;
            displayFace();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Profile file does not exist.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     *
     * @param username username to send
     * @param password password to send
     * @return Always return true
     * @throws Exception
     */
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
        displayFace();
        return true;
    }

    private void displayFace() {
        try {
            String sessionUrl = "https://sessionserver.mojang.com/session/minecraft/profile/"+session.getUUID();
            String skinURL = null;
            String sessionString = HttpUtils.httpGet(sessionUrl);
            JSONObject sessionJSON = (JSONObject) JSONValue.parse(sessionString);
            JSONArray properties = (JSONArray) sessionJSON.get("properties");

            String propertyString = null;

            for (Object rawProperty : properties) {
                JSONObject property = (JSONObject) rawProperty;
                if (property.containsKey("name") && "textures".equals(property.get("name"))) {
                    if (property.containsKey("value")) {
                        propertyString = new String(Base64.decodeBase64((String) property.get("value")));
                    }
                    break;
                }
            }

            JSONObject tmpJSON = (JSONObject) JSONValue.parse(propertyString);
            if (tmpJSON.containsKey("textures")) {
                tmpJSON = (JSONObject) tmpJSON.get("textures");
                if (tmpJSON.containsKey("SKIN")) {
                    tmpJSON = (JSONObject) tmpJSON.get("SKIN");
                    if (tmpJSON.containsKey("url")) {
                        skinURL = (String) tmpJSON.get("url");
                    }
                }
            }

            if (skinURL != null) {
                System.out.println("Found skin URL : ".concat(skinURL));
            }
            else {
                System.out.println("No skin URL found !");
                return;
            }

            Image skin = new Image(skinURL, 64*6, 32*6, false, false);
            Image face = new WritableImage(skin.getPixelReader(), 8*6, 8*6, 8*6, 8*6);


            PlatformImpl.runLater(() -> {
                mainController.face.setVisible(true);
                mainController.face.setImage(face);
            });

            /*InputStream is = new ByteArrayInputStream(imageString.getBytes());
            Image image = new Image(is);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect and revoke the client token
     * @throws Exception
     */
    void logout() throws Exception {
        YDLoginService loginService = new YDLoginService();
        YDProfileIO io = new YDProfileIO(Platform.getCurrentPlatform().getWorkingDirectory());
        loginService.logout(session);
        io.write(new IProfile[]{});

        loggedIn = false;

        PlatformImpl.runLater(() -> mainController.face.setVisible(false));
    }

    /**
     * Check if user is logged in
     * @return true if logged
     */
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
