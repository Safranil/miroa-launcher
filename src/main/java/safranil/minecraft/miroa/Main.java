package safranil.minecraft.miroa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("main.fxml"));
        GridPane root = loader.load();
        primaryStage.setTitle("Miroa");
        primaryStage.setScene(new Scene(root, 720, 440));
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());

        MainController controller = loader.getController();
        controller.webPreview.getEngine().load("http://minecraft.safranil.fr/");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
