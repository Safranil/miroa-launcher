package safranil.minecraft.miroa;

import fr.theshark34.openauth.AuthenticationException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    static Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("main.fxml"));
        GridPane root = loader.load();
        primaryStage.setTitle("Miroa");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.setScene(new Scene(root, 720, 440));
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());

        mainScene = primaryStage.getScene();

        MainController controller = loader.getController();
        MiroaLauncher launcher = MiroaLauncher.getInstance();
        launcher.setMainController(controller);

        controller.webPreview.getEngine().load("http://minecraft.safranil.fr/");

        String username = launcher.getUsername();
        controller.setToLogin();

        try {
            if (launcher.refreshToken()) {
                controller.setToPlay();
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        if (username != null) {
            controller.loginField.setText(username);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
