package safranil.minecraft.miroa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@SuppressWarnings("WeakerAccess")
public class Main extends Application {
    static Scene mainScene;
    static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("main.fxml"));
        AnchorPane root = loader.load();
        primaryStage.setTitle("Miroa");
        //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.setScene(new Scene(root, 750, 450));
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(primaryStage.getWidth());

        Main.mainScene = primaryStage.getScene();
        Main.mainStage = primaryStage;

        MainController controller = loader.getController();
        MiroaLauncher launcher = MiroaLauncher.getInstance();
        launcher.setMainController(controller);

        Thread t = new Starter();
        t.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
