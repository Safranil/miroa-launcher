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
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sk.tomsik68.mclauncher.api.common.mc.MinecraftInstance;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.versions.IVersion;
import sk.tomsik68.mclauncher.api.versions.IVersionInstaller;
import sk.tomsik68.mclauncher.impl.login.legacy.LegacyProfile;
import sk.tomsik68.mclauncher.impl.versions.mcdownload.MCDownloadVersionList;

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
    public void playAction(ActionEvent event) {
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
                        PlatformImpl.runAndWait(() -> {
                            setToPlay();
                        });
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

                    MCDownloadVersionList mcd = new MCDownloadVersionList();
                    try {
                        mcd.startDownload();
                        IVersion version = mcd.retrieveVersionInfo("1.7.10");
                        version.getInstaller().install(version, launcher.mc, new InstallProgressMonitor(_this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PlatformImpl.runAndWait(() -> {
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
