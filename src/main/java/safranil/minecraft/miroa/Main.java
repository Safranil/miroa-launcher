package safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    static Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("main.fxml"));
        GridPane root = loader.load();
        primaryStage.setTitle("Miroa");
        //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.setScene(new Scene(root, 720, 440));
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(primaryStage.getWidth());

        mainScene = primaryStage.getScene();

        MainController controller = loader.getController();
        MiroaLauncher launcher = MiroaLauncher.getInstance();

        // Enable logging from API
        /*MCLauncherAPI.log.setLevel(Level.ALL);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        MCLauncherAPI.log.addHandler(consoleHandler);*/

        launcher.setMainController(controller);

        if (MiroaLauncher.LAUNCHER_FOLDER.isFile()) {
            MiroaLauncher.LAUNCHER_FOLDER.delete();
        }

        if (!MiroaLauncher.LAUNCHER_FOLDER.exists()) {
            MiroaLauncher.LAUNCHER_FOLDER.mkdir();
        }

        if (!MiroaLauncher.LAUNCHER_FOLDER.canRead() || !MiroaLauncher.LAUNCHER_FOLDER.canWrite() || !MiroaLauncher.LAUNCHER_FOLDER.isDirectory()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Dossier du jeu en erreur");
            error.setHeaderText("Le dossier contenant le jeu n'est pas accessible.");
            error.setContentText(String.format(
                    "canRead : %s\ncanWrite : %s\nisDirectory : %s",
                    MiroaLauncher.LAUNCHER_FOLDER.canRead(),
                    MiroaLauncher.LAUNCHER_FOLDER.canWrite(),
                    MiroaLauncher.LAUNCHER_FOLDER.isDirectory()
            ));
            error.showAndWait();
            PlatformImpl.exit();
        }

        controller.webPreview.getEngine().load("http://minecraft.safranil.fr/");
        controller.setToLogin();

        if (launcher.login()) {
            controller.setToPlay();
        }

        String username = launcher.getUsername();
        if (username != null) {
            controller.loginField.setText(username);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
