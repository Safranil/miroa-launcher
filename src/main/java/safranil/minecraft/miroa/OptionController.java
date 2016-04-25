package safranil.minecraft.miroa;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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


    @FXML
    public void closeAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveAction(ActionEvent event) {
        boolean error = false;
        MiroaLauncher launcher = MiroaLauncher.getInstance();

        launcher.setMemory(memoryChoice.getValue().getJavaOption());

        closeAction(event);
    }

    @FXML
    public void logoutAction(ActionEvent event) {
        MiroaLauncher launcher = MiroaLauncher.getInstance();
        launcher.logout();
        launcher.mainController.setToLogin();
        logoutButton.setDisable(true);
    }

    @FXML
    public void selectJavaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chemin vers l'exécutable Java");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Exécutable Java", "java.exe", "java"),
                new FileChooser.ExtensionFilter("Tout les fichiers", "*")
        );
        File selectedFile = fileChooser.showOpenDialog(javaButton.getScene().getWindow());
        if (selectedFile != null) {
            javaField.setText(selectedFile.getAbsolutePath());
        }
    }

    public void prepareOptions() {
        MiroaLauncher launcher = MiroaLauncher.getInstance();

        memoryChoice.setItems(FXCollections.observableArrayList(MiroaLauncher.memoryOptions));

        for (MemoryOption memory : launcher.memoryOptions) {
            if (memory.getJavaOption().equals(launcher.getMemory())) {
                memoryChoice.setValue(memory);
                break;
            }
        }

        if (memoryChoice.getValue() == null) {
            memoryChoice.setValue(launcher.memoryOptions.get(launcher.DEFAULT_MEMORY_ID));
        }

        if (launcher.isLoggedIn()) {
            logoutButton.setVisible(true);
        } else {
            logoutButton.setVisible(false);
        }
    }
}
