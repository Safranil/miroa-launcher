package safranil.minecraft.miroa;

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
        MiroaLauncher launcher = MiroaLauncher.getInstance();

        if (launcher.checkJavaBin(javaField.getText())) {
            launcher.setJavaBin(javaField.getText());
            launcher.setMemory(memoryChoice.getValue().getJavaOption());

            closeAction();
        }
        else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erreur chemin vers Java");
            error.setHeaderText("L'exécutable Java spécifié est introuvable ou n'est pas exécutable.");
            error.setContentText("Vérifiez que vous avez spécifier le bon chemin de Java, par exemple :\n" +
                    "C:\\Program Files\\Java\\jre1.8.0_92\\bin\\java.exe");
            error.showAndWait();
        }
    }

    @FXML
    public void logoutAction() {
        MiroaLauncher launcher = MiroaLauncher.getInstance();
        try {
            launcher.logout();
        } catch (Exception e) {
            e.printStackTrace();
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
