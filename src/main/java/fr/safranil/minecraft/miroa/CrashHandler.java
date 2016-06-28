package fr.safranil.minecraft.miroa;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handler when Minecraft crash
 */
public class CrashHandler {
    private File[] filesToSave;
    private boolean saveLog = false;
    private String saveFileName;
    private File selectedFile;

    public CrashHandler(String saveFileName, File... fileToSave) {
        this.saveFileName = saveFileName;
        this.filesToSave = fileToSave;
    }

    public CrashHandler(File... fileToSave) {
        this.saveFileName = "miroa_crash.zip";
        this.filesToSave = fileToSave;
    }

    /**
     * Display a question to create or not the zip with all log file for this game
     */
    public void displayMessage() {
        PlatformImpl.runAndWait(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Minecraft a quitté prématurément");
            alert.setHeaderText("Voulais-vous sauvegarder les logs dans un fichier zip pour reporter l'erreur ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK)
                saveLog = true;
        });

        if (saveLog) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chemin vers l'exécutable Java");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setInitialFileName(saveFileName);
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ZIP", "*.zip")
            );
            PlatformImpl.runAndWait(() -> selectedFile = fileChooser.showSaveDialog(Main.mainScene.getWindow()));
            if (selectedFile != null) {
                try {
                    zipLog(selectedFile);

                    Main.hostServices.showDocument("http://minecraft.safranil.fr/rapport-de-bug");
                    Main.hostServices.showDocument(selectedFile.getParent());

                    PlatformImpl.runAndWait(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Logs sauvegardés");
                        alert.setHeaderText("Les fichiers de logs ont été sauvegardé dans le fichier ".concat(selectedFile.getAbsolutePath()));
                        alert.setContentText("La page de signalement d'erreur viens d'être ouverte automatiquement (http://minecraft.safranil.fr/rapport-de-bug)");
                        alert.showAndWait();
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Save all file in the dest zip
     * @param dest ZIP file
     */
    private void zipLog(File dest) throws IOException {
        if (dest.exists())
            FileUtils.forceDelete(dest);

        if(!dest.createNewFile())
            throw new IOException("File not created");

        FileOutputStream fos = new FileOutputStream(dest);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (File fileToSave : filesToSave) {
            if (fileToSave.exists() && fileToSave.canRead()) {
                MiroaLauncher.LOGGER.warning("Adding file ".concat(fileToSave.getAbsolutePath()));
                FileInputStream fis = new FileInputStream(fileToSave);
                ZipEntry zipEntry = new ZipEntry(fileToSave.getName());
                zos.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }

                zos.closeEntry();
                fis.close();
            }
            else {
                MiroaLauncher.LOGGER.warning("Ignoring file ".concat(fileToSave.getAbsolutePath()));
            }
        }

        zos.close();
        fos.close();
    }
}
