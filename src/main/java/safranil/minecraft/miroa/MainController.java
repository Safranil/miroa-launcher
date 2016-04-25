package safranil.minecraft.miroa;

import fr.theshark34.openauth.AuthenticationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainController {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button optionsButton;
    @FXML
    public Button playButton;
    @FXML
    public WebView webPreview;
    @FXML
    public ProgressIndicator progress;

    @FXML
    public void playAction(ActionEvent event) {
        Thread t = new Thread(new Task<Void>() {
            int i;

            @Override
            public Void call() {
                Platform.runLater(() -> {
                    playButton.setDisable(true);
                    optionsButton.setDisable(true);
                    loginField.setDisable(true);
                    passwordField.setDisable(true);
                    progress.setVisible(true);
                });

                MiroaLauncher launcher = MiroaLauncher.getInstance();

                try {
                    launcher.auth(loginField.getText(), passwordField.getText());
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    playButton.setDisable(false);
                    optionsButton.setDisable(false);
                    loginField.setDisable(true);
                    passwordField.setDisable(true);
                    progress.setVisible(false);
                });

                /*Platform.runLater(() -> {
                    playButton.setDisable(true);
                    playButton.setText("Mise à jour...");
                    progress.setVisible(true);
                });

                for (i = 0; i <= 100; i++) {
                    Platform.runLater(() -> progress.setProgress(i / 100.0));

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Platform.runLater(() -> {
                    playButton.setText("Jouer");
                    playButton.setDisable(false);
                });*/


                return null;
            }
        });

        t.start();
    }

    @FXML
    public void optionAction(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("option.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Options");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
