package safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import fr.theshark34.openauth.AuthenticationException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainController {
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button optionsButton;
    @FXML
    Button playButton;
    @FXML
    WebView webPreview;
    @FXML
    ProgressIndicator progress;

    public void setToPlay() {
        playButton.setText("Jouer");
        loginField.setDisable(true);
        passwordField.setDisable(true);
    }

    public void setToLogin() {
        playButton.setText("Connexion");
        loginField.setDisable(false);
        passwordField.setDisable(false);
    }

    @FXML
    public void playAction(ActionEvent event) {
        Thread t = new Thread(new Task<Void>() {
            @Override
            public Void call() {
                // Display login form
                if (!MiroaLauncher.getInstance().isLoggedIn()) {
                    PlatformImpl.runAndWait(() -> {
                        playButton.setDisable(true);
                        optionsButton.setDisable(true);
                        loginField.setDisable(true);
                        passwordField.setDisable(true);
                        progress.setVisible(true);
                    });

                    MiroaLauncher launcher = MiroaLauncher.getInstance();

                    try {
                        launcher.auth(loginField.getText(), passwordField.getText());
                        PlatformImpl.runAndWait(() -> {
                            setToPlay();
                        });
                    } catch (AuthenticationException e) {
                        e.printStackTrace();

                        PlatformImpl.runAndWait(() -> {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Erreur lors de l'authentification");
                            error.setHeaderText("Erreur lors de l'authentification.");
                            error.setContentText(String.format("ErrorMessage : %s\nError : %s", e.getErrorModel().getErrorMessage(), e.getErrorModel().getError()));
                            error.showAndWait();
                            setToLogin();
                        });
                    }

                    PlatformImpl.runAndWait(() -> {
                        playButton.setDisable(false);
                        optionsButton.setDisable(false);
                        progress.setVisible(false);
                    });
                } else {

                }

                return null;
            }
        });

        t.start();
    }

    @FXML
    public void optionAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("option.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(Main.mainScene.getWindow());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Options");
            stage.setScene(new Scene(root));
            stage.show();

            OptionController controller = loader.getController();
            controller.prepareOptions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
