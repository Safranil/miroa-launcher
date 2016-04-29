package safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sk.tomsik68.mclauncher.backend.MinecraftLauncherBackend;
import sk.tomsik68.mclauncher.impl.common.Platform;

import java.io.IOException;

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
    public void playAction() {
        MainController _this = this;
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
                        loginPane.setOpacity(0.25);
                        infoPane.setVisible(true);
                        progress.setStyle(" -fx-progress-color: limegreen;");
                        infoLabel.setText("Connexion en cours...");
                    });

                    MiroaLauncher launcher = MiroaLauncher.getInstance();

                    try {
                        launcher.login(loginField.getText(), passwordField.getText());
                        PlatformImpl.runAndWait(() -> setToPlay());
                    } catch (Exception e) {
                        e.printStackTrace();

                        PlatformImpl.runAndWait(() -> {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Erreur lors de l'authentification");
                            error.setHeaderText("Erreur lors de l'authentification.");
                            error.setContentText(String.format("ErrorMessage : %s", e.getMessage()));
                            error.showAndWait();
                            setToLogin();
                        });
                    }

                    PlatformImpl.runAndWait(() -> {
                        playButton.setDisable(false);
                        optionsButton.setDisable(false);
                        progress.setVisible(false);
                        infoLabel.setText("");
                        loginPane.setOpacity(1);
                        infoPane.setVisible(false);
                    });
                } else {
                    PlatformImpl.runAndWait(() -> {
                        playButton.setDisable(true);
                        optionsButton.setDisable(true);
                        loginField.setDisable(true);
                        passwordField.setDisable(true);
                        progress.setVisible(true);
                        progress.setStyle(" -fx-progress-color: limegreen;");
                        progress.setProgress(-1);
                        infoLabel.setText("Installation de Minecraft...");
                        loginPane.setOpacity(0.25);
                        infoPane.setVisible(true);
                    });

                    MiroaLauncher launcher = MiroaLauncher.getInstance();
                    MinecraftLauncherBackend launcherBackend = new MinecraftLauncherBackend(Platform.getCurrentPlatform().getWorkingDirectory());

                    try {
                        launcherBackend.updateMinecraft(MiroaLauncher.MC_VERSION, new InstallProgressMonitor(_this));
                        ProcessBuilder pb = launcherBackend.launchMinecraft(launcher.session, MiroaLauncher.MC_VERSION);

                        PlatformImpl.runLater(() -> Main.mainStage.hide());

                        Process p = pb.start();

                        int returnValue = p.waitFor();
                        if (returnValue == 0) {
                            PlatformImpl.exit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PlatformImpl.runAndWait(() -> {
                        Main.mainStage.show();
                        playButton.setDisable(false);
                        optionsButton.setDisable(false);
                        progress.setVisible(false);
                        infoLabel.setText("");
                        subInfoLabel.setText("");
                        loginPane.setOpacity(1);
                        infoPane.setVisible(false);
                    });
                }

                return null;
            }
        });

        t.start();
    }

    @FXML
    public void optionAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("option.fxml"));
            Parent root = loader.load();
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
