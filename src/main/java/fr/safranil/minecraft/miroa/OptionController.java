/**
 * This file is part of Miroa Launcher.
 * Copyright (C) 2016 David Cachau <dev@safranil.fr>
 * <p>
 * Miroa Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * <p>
 * Miroa Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Miroa Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import fr.safranil.minecraft.mclauncherapi.InstallProgressMonitor;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class OptionController {
    @FXML
    Pane mainPane;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;
    @FXML
    ChoiceBox<MemoryOption> memoryChoice;
    @FXML
    Button javaButton;
    @FXML
    TextField javaField;
    @FXML
    Button logoutButton;
    @FXML
    Label version;

    @FXML
    public void initialize() {
        MiroaLauncher.LOGGER.info("Initializing option controller");
        MiroaLauncher launcher = MiroaLauncher.getInstance();

        version.setText(GitInfo.getInstance().getDescribe());

        memoryChoice.setItems(FXCollections.observableArrayList(MiroaLauncher.memoryOptions));

        for (MemoryOption memory : MiroaLauncher.memoryOptions) {
            if (memory.getJavaOption().equals(launcher.getMemory())) {
                memoryChoice.setValue(memory);
                break;
            }
        }

        if (memoryChoice.getValue() == null) {
            memoryChoice.setValue(MiroaLauncher.memoryOptions.get(MiroaLauncher.DEFAULT_MEMORY_ID));
        }

        if (launcher.isLoggedIn()) {
            logoutButton.setVisible(true);
        } else {
            logoutButton.setVisible(false);
        }

        javaField.setText(launcher.getJavaBin());
    }

    @FXML
    public void openWebsite() {
        Main.hostServices.showDocument("http://minecraft.safranil.fr/");
    }

    @FXML
    public void openLog() {
        Main.hostServices.showDocument((new File(MiroaLauncher.OS.getWorkingDirectory(), "logs")).getAbsolutePath());
    }

    @FXML
    public void openLauncherDir() {
        Main.hostServices.showDocument(MiroaLauncher.OS.getWorkingDirectory().getAbsolutePath());
    }

    @FXML
    public void checkLocalFiles() {
        MiroaLauncher.LOGGER.info("Checking local files");
        //closeAction();
        Thread t = new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                MiroaLauncher launcher = MiroaLauncher.getInstance();
                try {
                    PlatformImpl.runAndWait(() -> {
                        mainPane.setDisable(true);
                        launcher.mainController.playButton.setDisable(true);
                        launcher.mainController.optionsButton.setDisable(true);
                        launcher.mainController.loginPane.setOpacity(0.25);
                        launcher.mainController.face.setOpacity(0.25);
                        launcher.mainController.infoPane.setVisible(true);
                        launcher.mainController.infoLabel.setText("Vérification des fichiers...");
                        launcher.mainController.subInfoLabel.setText("");
                        launcher.mainController.progress.setStyle("-fx-progress-color: #ffd700;");
                        launcher.mainController.progress.setProgress(-1);
                    });
                    Updater.update(MiroaLauncher.OS.getWorkingDirectory(), new InstallProgressMonitor(launcher.mainController.progress, launcher.mainController.subInfoLabel), true);
                    PlatformImpl.runAndWait(() -> {
                        mainPane.setDisable(false);
                        launcher.mainController.infoPane.setVisible(false);
                        launcher.mainController.playButton.setDisable(false);
                        launcher.mainController.optionsButton.setDisable(false);
                        launcher.mainController.progress.setVisible(false);
                        launcher.mainController.infoLabel.setText("");
                        launcher.mainController.subInfoLabel.setText("");
                        launcher.mainController.loginPane.setOpacity(1);
                        launcher.mainController.face.setOpacity(1);
                    });
                } catch (Exception e) {
                    MiroaLauncher.LOGGER.severe("Error when downloading updates");
                    e.printStackTrace();
                    Utils.displayException("Erreur lors de la vérification", "Une erreur c'est produite lors de la vérification des fichiers du jeu.", e);
                }
                return null;
            }
        });
        t.start();
    }

    @FXML
    public void removeLog() {
        try {
            FileUtils.deleteDirectory(new File(MiroaLauncher.OS.getWorkingDirectory(), "logs"));
        } catch (IOException e) {
            MiroaLauncher.LOGGER.info("Error when removing log files");
            e.printStackTrace();
            Utils.displayException("Suppression impossible", "Une erreur est survenue lors de la suppression.", e);
        }
    }

    @FXML
    public void removeLauncher() {
        for (File file : MiroaLauncher.OS.getWorkingDirectory().listFiles()) {
            String name = file.getName();
            if (!name.equals("saves") &&
                    !name.equals("launcher_profiles.json") &&
                    !name.equals("server.dat") &&
                    !name.equals("launcher.jar")) {
                FileUtils.deleteQuietly(file);
            }
        }
    }

    @FXML
    public void closeAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveAction() {
        MiroaLauncher.LOGGER.info("Saving options");
        MiroaLauncher launcher = MiroaLauncher.getInstance();

        if (launcher.checkJavaBin(javaField.getText())) {
            launcher.setJavaBin(javaField.getText());
            launcher.setMemory(memoryChoice.getValue().getJavaOption());

            MiroaLauncher.LOGGER.info("Options saved");
            closeAction();
        } else {
            MiroaLauncher.LOGGER.info("Invalid options, not saved");
            Utils.displayError("Erreur chemin vers Java",
                    "L'exécutable Java spécifié est introuvable ou n'est pas exécutable.",
                    "Vérifiez que vous avez spécifier le bon chemin de Java, par exemple :\n" +
                            "C:\\Program Files\\Java\\jre1.8.0_92\\bin\\java.exe");
        }
    }

    @FXML
    public void logoutAction() {
        MiroaLauncher launcher = MiroaLauncher.getInstance();
        try {
            launcher.logout();
        } catch (Exception e) {
            MiroaLauncher.LOGGER.info("Error when log out");
            e.printStackTrace();
            Utils.displayException("Deconnexion impossible", "Une erreur est survenue lors de la déconnexion.", e);
        }
        launcher.mainController.setToLogin();
        logoutButton.setDisable(true);
    }

    @FXML
    public void selectJavaAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chemin vers l'exécutable Java");

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            fileChooser.setInitialDirectory(new File("C:\\Program Files"));
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Exécutable Java", "java.exe", "java"),
                new FileChooser.ExtensionFilter("Tout les fichiers", "*")
        );
        File selectedFile = fileChooser.showOpenDialog(javaButton.getScene().getWindow());
        if (selectedFile != null) {
            javaField.setText(selectedFile.getAbsolutePath());
        }
    }

}
