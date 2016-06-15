/**
 * This file is part of Miroa Launcher.
 * Copyright (C) 2016 David Cachau <dev@safranil.fr>
 *
 * Miroa Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Miroa Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Miroa Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.safranil.minecraft.miroa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressWarnings("WeakerAccess")
public class Main extends Application {
    static Scene mainScene;
    static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        MiroaLauncher.LOGGER.info("Starting Miroa launcher...");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        MiroaLauncher.LOGGER.info("Local time is ".concat(sdf.format(Calendar.getInstance().getTime())));

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("main.fxml"));
        AnchorPane root = loader.load();
        primaryStage.setTitle("Miroa");

        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("minecraft16.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("minecraft32.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("minecraft48.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("minecraft64.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("minecraft128.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("minecraft256.png")));

        MiroaLauncher.LOGGER.info("Showing the scene...");
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

        controller.loading.setVisible(true);

        // Do some work in a separate thread
        MiroaLauncher.LOGGER.info("Running Starter thread...");
        Thread t = new Starter();
        t.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
