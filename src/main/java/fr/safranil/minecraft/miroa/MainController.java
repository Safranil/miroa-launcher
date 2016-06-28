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
import fr.safranil.minecraft.mclauncherapi.InstallProgressMonitor;
import fr.safranil.minecraft.mclauncherapi.LaunchSettings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sk.tomsik68.mclauncher.api.common.mc.MinecraftInstance;
import sk.tomsik68.mclauncher.api.servers.ServerInfo;
import sk.tomsik68.mclauncher.backend.MinecraftLauncherBackend;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.common.mc.VanillaServerStorage;
import sk.tomsik68.mclauncher.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class MainController {
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label infoLabel;
    @FXML
    Label subInfoLabel;
    @FXML
    Button optionsButton;
    @FXML
    Button playButton;
    @FXML
    WebView webPreview;
    @FXML
    ProgressIndicator progress;

    @FXML
    GridPane loginPane;
    @FXML
    GridPane infoPane;

    @FXML
    Pane loading;

    @FXML
    ImageView face;

    public void onEnter() {
        playButton.fire();
    }

    public void setToPlay() {
        MiroaLauncher.LOGGER.info("Gui setting up to play");
        playButton.setText("Jouer");
        playButton.requestFocus();
        loginField.setDisable(true);
        passwordField.setDisable(true);
        playButton.setDisable(false);
        optionsButton.setDisable(false);
    }

    public void setToLogin() {
        MiroaLauncher.LOGGER.info("Gui setting up to login");
        playButton.setText("Connexion");
        loginField.setDisable(false);
        loginField.requestFocus();
        passwordField.setDisable(false);
        playButton.setDisable(false);
        optionsButton.setDisable(false);
    }

    @FXML
    public void playAction() {
        MiroaLauncher.LOGGER.info("Play button pressed, starting Task thread");
        Thread t = new Thread(new Task<Void>() {
            @Override
            public Void call() {
                // Display login form
                if (!MiroaLauncher.getInstance().isLoggedIn()) {
                    MiroaLauncher.LOGGER.info("User not logged");
                    PlatformImpl.runAndWait(() -> {
                        playButton.setDisable(true);
                        optionsButton.setDisable(true);
                        loginField.setDisable(true);
                        passwordField.setDisable(true);
                        progress.setVisible(true);
                        loginPane.setOpacity(0.25);
                        infoPane.setVisible(true);
                        progress.setStyle("-fx-progress-color: limegreen;");
                        infoLabel.setText("Connexion en cours...");
                    });

                    MiroaLauncher launcher = MiroaLauncher.getInstance();

                    MiroaLauncher.LOGGER.info("Authenticate user through Mojang server");
                    try {
                        launcher.login(loginField.getText(), passwordField.getText());
                        PlatformImpl.runAndWait(() -> setToPlay());
                        MiroaLauncher.LOGGER.info("User ".concat(launcher.getUsername()).concat(" is now authenticated"));
                    } catch (Exception e) {
                        MiroaLauncher.LOGGER.warning("Exception thrown when authenticating : ".concat(e.getMessage()));
                        e.printStackTrace();
                        Utils.displayError("Erreur lors de l'authentification", "ErrorMessage : ".concat(e.getMessage()));
                    }

                    PlatformImpl.runAndWait(() -> {
                        playButton.setDisable(false);
                        optionsButton.setDisable(false);
                        progress.setVisible(false);
                        infoLabel.setText("");
                        loginPane.setOpacity(1);
                        face.setOpacity(1);
                        infoPane.setVisible(false);
                    });
                } else {
                    MiroaLauncher.LOGGER.info("User logged in");
                    PlatformImpl.runAndWait(() -> {
                        playButton.setDisable(true);
                        optionsButton.setDisable(true);
                        loginField.setDisable(true);
                        passwordField.setDisable(true);
                        progress.setVisible(true);
                        progress.setStyle("-fx-progress-color: limegreen;");
                        progress.setProgress(-1);
                        infoLabel.setText("Veuillez patienter...");
                        loginPane.setOpacity(0.25);
                        face.setOpacity(0.25);
                        infoPane.setVisible(true);
                    });

                    createForgeVersionFile();

                    MiroaLauncher launcher = MiroaLauncher.getInstance();
                    MinecraftLauncherBackend launcherBackend = new MinecraftLauncherBackend(Platform.getCurrentPlatform().getWorkingDirectory());

                    boolean canLaunch = false;
                    try {
                        MiroaLauncher.LOGGER.info("Installing Minecraft");
                        PlatformImpl.runAndWait(() -> infoLabel.setText("Installation de Minecraft..."));
                        launcherBackend.updateMinecraft(MiroaLauncher.MC_VERSION, new InstallProgressMonitor(progress, subInfoLabel));

                        PlatformImpl.runAndWait(() -> {
                            infoLabel.setText("Installation de Forge...");
                            subInfoLabel.setText("");
                            progress.setStyle("-fx-progress-color: #cb3d35;");
                            progress.setProgress(-1);
                        });

                        MiroaLauncher.LOGGER.info("Installing Forge and mods");
                        Updater.update(MiroaLauncher.OS.getWorkingDirectory(), new InstallProgressMonitor(progress, subInfoLabel));
                        MiroaLauncher.LOGGER.info("Installing Forge missing libraries");
                        launcherBackend.updateMinecraft(MiroaLauncher.FORGE_VERSION, new InstallProgressMonitor(progress, subInfoLabel));

                        updateServers();

                        canLaunch = true;
                    } catch (Exception e) {
                        MiroaLauncher.LOGGER.severe("Error when downloading updates");
                        e.printStackTrace();
                        Utils.displayException("Erreur lors du téléchargement", "Une erreur c'est produite lors du téléchargement.", e);
                    }

                    if (canLaunch) {
                        MiroaLauncher.LOGGER.info("Starting Minecraft");
                        try {
                            PlatformImpl.runAndWait(() -> {
                                infoLabel.setText("Lancement du jeu");
                                subInfoLabel.setText("");
                                progress.setStyle(" -fx-progress-color: royalblue;");
                                progress.setProgress(-1);
                            });

                            MiroaLauncher.LOGGER.info("Memory setting : "+launcher.getMemory());
                            MiroaLauncher.LOGGER.info("Java setting : "+launcher.getJavaBin());

                            ProcessBuilder pb = launcherBackend.launchMinecraft(
                                    launcher.session,
                                    null,
                                    MiroaLauncher.FORGE_VERSION,
                                    new LaunchSettings(launcher.getMemory(), new File(launcher.getJavaBin())),
                                    null
                            );

                            pb.directory(MiroaLauncher.OS.getWorkingDirectory());

                            File logDir = new File(MiroaLauncher.OS.getWorkingDirectory(), "logs");
                            org.apache.commons.io.FileUtils.forceMkdir(logDir);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                            String date = sdf.format(Calendar.getInstance().getTime());

                            File errLog = new File(logDir, "minecraft_"+date+".err.log");
                            File outLog = new File(logDir, "minecraft_"+date+".out.log");

                            pb.redirectError(errLog);
                            pb.redirectOutput(outLog);

                            MiroaLauncher.LOGGER.info("Minecraft log can be found in logs/minecraft_"+date+".*.log");

                            Process p = pb.start();
                            MiroaLauncher.LOGGER.info("Process started");
                            PlatformImpl.runAndWait(() -> {
                                infoLabel.setText("Jeu lancé");
                                progress.setProgress(1);
                            });

                            Thread.sleep(3000);
                            PlatformImpl.runAndWait(() -> {
                                Main.mainStage.hide();
                            });

                            int returnCode = p.waitFor();
                            if (returnCode != 0 || true) {
                                MiroaLauncher.LOGGER.severe("Minecraft return code is "+returnCode+", starting crash handler");
                                PlatformImpl.runAndWait(() -> Main.mainStage.show());
                                CrashHandler crashHandler = new CrashHandler(
                                        "miroa_crash_".concat(date).concat(".zip"),
                                        outLog,
                                        errLog,
                                        new File(MiroaLauncher.OS.getWorkingDirectory(), "launcher.log"),
                                        new File(logDir, "latest.log"),
                                        new File(logDir, "fml-client-latest.log")
                                );
                                crashHandler.displayMessage();
                            }
                            else {
                                MiroaLauncher.LOGGER.info("Minecraft return code is 0, exiting launcher");
                                PlatformImpl.exit();
                            }
                        } catch (InterruptedException ignored) {
                        } catch (Exception e) {
                            MiroaLauncher.LOGGER.severe("Error when launching Minecraft");
                            e.printStackTrace();
                            Utils.displayException("Erreur lors du lancement", "Une erreur c'est produite lors du lancement du jeu.", e);
                        }
                    }

                    PlatformImpl.runAndWait(() -> {
                        Main.mainStage.show();
                        playButton.setDisable(false);
                        optionsButton.setDisable(false);
                        progress.setVisible(false);
                        infoLabel.setText("");
                        subInfoLabel.setText("");
                        loginPane.setOpacity(1);
                        face.setOpacity(1);
                        infoPane.setVisible(false);
                    });
                }

                return null;
            }
        });

        t.start();
    }

    private void updateServers() {
        MiroaLauncher.LOGGER.info("Updating server list");
        MinecraftInstance mc = new MinecraftInstance(MiroaLauncher.OS.getWorkingDirectory());
        VanillaServerStorage serverStorage = new VanillaServerStorage(mc);
        ServerInfo[] savedServers = new ServerInfo[0];

        try {
            savedServers = serverStorage.loadServers();
        } catch (Exception ignored) {
        }

        try {
            ServerInfo[] servers;

            for (ServerInfo server : savedServers) {
                if (Objects.equals(server.getIP(), MiroaLauncher.SERVER_IP) && server.getPort() == MiroaLauncher.SERVER_PORT) {
                    MiroaLauncher.LOGGER.info("Server already exist");
                    return;
                }
            }

            MiroaLauncher.LOGGER.info("Adding Miroa server");
            servers = new ServerInfo[savedServers.length + 1];
            servers[0] = new ServerInfo(MiroaLauncher.SERVER_IP, "Miroa", null, MiroaLauncher.SERVER_PORT);
            System.arraycopy(savedServers, 0, servers, 1, savedServers.length);
            serverStorage.saveServers(servers);
        } catch (Exception e) {
            MiroaLauncher.LOGGER.info("Unable to update server list");
            e.printStackTrace();
        }
    }

    private void createForgeVersionFile() {
        File forgeVersion = new File(MiroaLauncher.OS.getWorkingDirectory(), "versions/" + MiroaLauncher.FORGE_VERSION + "/" + MiroaLauncher.FORGE_VERSION + ".json");
        if (!forgeVersion.exists()) {
            MiroaLauncher.LOGGER.info("Creating missing forge version file");
            try {
                FileUtils.createFileSafely(forgeVersion);
                org.apache.commons.io.FileUtils.copyURLToFile(getClass().getResource("forgeVersion.json"), forgeVersion);
            } catch (Exception e) {
                MiroaLauncher.LOGGER.severe("Unable to add Forge version file");
                e.printStackTrace();
                Utils.displayException("Installation impossible", "Impossible de créer le fichier de version de Forge.", e);
                PlatformImpl.exit();
            }
        }
    }

    @FXML
    public void optionAction() {
        MiroaLauncher.LOGGER.info("Opening options GUI");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("option.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(Main.mainScene.getWindow());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Options");
            stage.setScene(new Scene(root));
            MiroaLauncher.LOGGER.info("Showing options GUI");
            stage.show();
        } catch (IOException e) {
            MiroaLauncher.LOGGER.severe("Unable to show Option GUI");
            e.printStackTrace();
            Utils.displayException("Erreur interface", "Impossible d'afficher les options.", e);
        }
    }
}
