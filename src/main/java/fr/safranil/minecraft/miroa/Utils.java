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

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Some utils to display messages
 */
class Utils {
    /**
     * Show a dialog with an exception inside
     *
     * @param title     title of the dialog
     * @param header    Header part
     * @param content   content part
     * @param throwable exception to display
     */
    static void displayException(String title, String header, String content, Throwable throwable) {
        throwable.printStackTrace();

        PlatformImpl.runAndWait(() -> {
            // Create the alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content +
                    "\nVous pouvez reporter cette erreur par mail à dev@safranil.fr avec le contenu de la boite ci-dessous (Afficher les détails pour voir la boite).");

            // Create the exception area
            TextArea textArea = new TextArea(ExceptionUtils.getStackTrace(throwable));
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            textArea.setStyle("-fx-font-size: 11px");
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            // Add the exception area to the alert
            GridPane pane = new GridPane();
            pane.setMaxWidth(Double.MAX_VALUE);
            pane.add(textArea, 0, 0);
            alert.getDialogPane().setExpandableContent(pane);
            alert.getDialogPane().setMinWidth(650);

            // And finally, show the alert
            alert.showAndWait();
        });
    }

    /**
     * Show a dialog with an exception inside
     *
     * @param title     title of the dialog
     * @param header    Header part
     * @param throwable exception to display
     */
    static void displayException(String title, String header, Throwable throwable) {
        displayException(title, header, "", throwable);
    }

    static void displayError(String title, String header, String content) {
        PlatformImpl.runAndWait(() -> {
            // Create the alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            // And finally, show the alert
            alert.showAndWait();
        });
    }

    static void displayError(String title, String header) {
        displayError(title, header, "");
    }
}
