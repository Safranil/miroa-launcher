package safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
