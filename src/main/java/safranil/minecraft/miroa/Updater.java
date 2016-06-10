package safranil.minecraft.miroa;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.codec.digest.DigestUtils;
import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;
import sk.tomsik68.mclauncher.util.FileUtils;
import sk.tomsik68.mclauncher.util.HttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

class Updater {
    private static Logger log = Logger.getLogger(Updater.class.getName());
    private static String serverUrl = "http://static.safranil.fr/minecraft/update.json";

    public static void update(File ws, IProgressMonitor progress) throws Exception {
        log.info("Downloading package informations");
        String jsonString = HttpUtils.httpGet(serverUrl);
        JSONObject updateJson = (JSONObject) JSONValue.parse(jsonString);

        if (updateJson == null) {
            throw new Exception("Unable to get the update file.");
        }

        JSONObject localJson = null;
        File localFile = new File(ws, "package.json");
        if (localFile.exists() && localFile.canRead()) {
            log.info("No package information stored locally");
            localJson = (JSONObject) JSONValue.parse(new FileReader(localFile));
        }

        JSONArray fileToDelete = new JSONArray();
        JSONArray fileToDownload = new JSONArray();

        if (!updateJson.containsKey("files") && updateJson.get("files") instanceof JSONArray) {
            throw new Exception("Update is invalid !");
        }

        if (localJson == null) {
            log.info("Rule : Downloading all file");
            fileToDownload = (JSONArray) updateJson.get("files");
        }
        else {
            log.info("Rule : Delta calculation");
            calculateDelta((JSONArray) localJson.get("files"), (JSONArray) updateJson.get("files"), fileToDelete, fileToDownload);
        }

        File tempDir = new File(ws, "tmpdl");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        log.info("Downloading files...");
        for (Object aFileToDownload : fileToDownload) {
            JSONObject json = (JSONObject) aFileToDownload;
            String file = (String) json.get("name");
            String url = (String) json.get("url");
            String hash = (String) json.get("checksum");
            Integer size = (Integer) json.get("size");
            File destFile = new File(tempDir, file);

            if (!destFile.getAbsolutePath().startsWith(ws.getAbsolutePath())) {
                log.severe("Unsafe modification detected, the directory is outside of the launcher for ".concat(file));
                throw new Exception("Update file contain unsafe modification, aborting process !");
            }

            if (destFile.exists()) {
                log.fine("File ".concat(file).concat(" exist, checking content"));
                FileInputStream fis = new FileInputStream(destFile);
                if (size.floatValue() != destFile.length()
                        || !DigestUtils.sha1Hex(fis).equals(hash)) {
                    log.info("Deleting errored file ".concat(file));
                    fis.close();
                    destFile.delete();
                } else {
                    continue;
                }
            }

            log.info("Downloading file at ".concat(url));
            progress.setStatus("Downloading ".concat(file));
            FileUtils.downloadFileWithProgress(url, destFile, progress);
        }

        deleteFiles(fileToDelete, ws);
        moveFiles(fileToDownload, tempDir, ws);

        org.apache.commons.io.FileUtils.forceDeleteOnExit(tempDir);

        FileUtils.writeFile(localFile, jsonString);
    }

    private static void moveFiles(JSONArray fileToMove, File tempDir, File ws) throws Exception {
        File src, dst;
        for (Object aFileToMove : fileToMove) {
            JSONObject json = (JSONObject) aFileToMove;
            String file = (String) json.get("name");
            src = new File(tempDir, file);
            dst = new File(ws, file);

            if (!src.getAbsolutePath().startsWith(ws.getAbsolutePath()) || !dst.getAbsolutePath().startsWith(ws.getAbsolutePath())) {
                log.severe("Unsafe modification detected, the directory is outside of the launcher for ".concat(file));
                throw new Exception("Update file contain unsafe modification, aborting process !");
            }

            dst.delete();
            org.apache.commons.io.FileUtils.moveFileToDirectory(src, dst.getParentFile(), true);
        }

    }

    private static void deleteFiles(JSONArray fileToDelete, File ws) {
        for (Object aFileToDelete : fileToDelete) {
            JSONObject json = (JSONObject) aFileToDelete;
            String fileStr = (String) json.get("name");
            File file = new File(ws, fileStr);
            //File parent = file.getParentFile();

            log.info("deleting file ".concat(fileStr));
            file.delete();

            // @TODO Remove empty directory
        }
    }

    private static void calculateDelta(JSONArray local, JSONArray update, JSONArray fileToDelete, JSONArray fileToDownload) {
        JSONObject lJson, uJson = null;
        String lStr, uStr = null;
        int lSize, uSize;

        lSize = local.size();
        for (int i = 0; i < lSize; i++) {
            uSize = update.size();
            for (int j = 0; j < uSize; j++) {
                uJson = (JSONObject) local.get(i);
                lJson = (JSONObject) update.get(j);
                lStr = (String) uJson.get("name");
                uStr = (String) lJson.get("name");
                if (lStr.equals(uStr)) {
                    lStr = (String) ((JSONObject) update.get(i)).get("checksum");
                    uStr = (String) ((JSONObject) update.get(j)).get("checksum");

                    if (!lStr.equals(uStr)) {
                        fileToDelete.add(uJson);
                        fileToDownload.add(lJson);
                    }

                    local.remove(i);
                    update.remove(j);
                    i--;
                    lSize = local.size();
                    break;
                }
            }
        }

        lSize = local.size();
        for (int i = 0; i < lSize; i++) {
            lJson = (JSONObject) local.get(i);
            fileToDelete.add(lJson);
        }

        uSize = update.size();
        for (int i = 0; i < uSize; i++) {
            uJson = (JSONObject) update.get(i);
            fileToDelete.add(uJson);
        }
    }

}
