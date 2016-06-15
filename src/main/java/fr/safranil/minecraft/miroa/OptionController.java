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

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class OptionController {
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


    @SuppressWarnings("WeakerAccess")
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
        }
        else {
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

    void prepareOptions() {
        MiroaLauncher launcher = MiroaLauncher.getInstance();

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
}
