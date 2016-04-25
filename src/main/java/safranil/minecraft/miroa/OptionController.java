package safranil.minecraft.miroa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OptionController {
    @FXML
    public Button saveButton;
    @FXML
    public Button cancelButton;
    @FXML
    public TextField memoryField;
    @FXML
    public Button javaButton;
    @FXML
    public TextField javaField;


    @FXML
    public void cancelAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
