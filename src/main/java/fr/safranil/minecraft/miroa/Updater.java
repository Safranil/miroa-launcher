/**
 * This file is part of Miroa Launcher.
 * Copyright (C) 2016 David Cachau <dev@safranil.fr>
 * <p>
 * Miroa Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * <p>
 * Miroa Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Miroa Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.safranil.minecraft.miroa;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.codec.digest.DigestUtils;
import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;
import sk.tomsik68.mclauncher.util.HttpUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.logging.Logger;

/**
 * Custom updater that connect to a custom server for :
 *  - Download missing lib for forge
 *  - Download mods & configs
 */
class Updater {
    private static Logger log = Logger.getLogger(Updater.class.getName());

    /**
     * Update custom files without checking localfile
     *
     * @param minecraft Launcher working directory
     * @param progress Progress monitor
     * @throws Exception
     */
    static void update(File minecraft, IProgressMonitor progress) throws Exception {
        update(minecraft, progress, false);
    }

    /**
     * Update custom files
     *
     * @param minecraft Launcher working directory
     * @param progress Progress monitor
     * @throws Exception
     */
    static void update(File minecraft, IProgressMonitor progress, boolean checkLocal) throws Exception {
        String serverUrl = "http://static.safranil.fr/minecraft/update.json";

        // Download package information from the server
        log.info("Downloading package informations");
        String jsonString = HttpUtils.httpGet(serverUrl);
        JSONObject updateJson = (JSONObject) JSONValue.parse(jsonString);

        if (updateJson == null) {
            throw new Exception("Unable to get the update file.");
        }

        // Load the local package infos
        JSONObject localJson = null;
        File localFile = new File(minecraft, "package.json");
        if (localFile.exists() && localFile.canRead()) {
            log.info("No package information stored locally");
            FileReader fileReader = new FileReader(localFile);
            localJson = (JSONObject) JSONValue.parse(fileReader);
            fileReader.close();
        }

        JSONArray fileToDelete = new JSONArray();
        JSONArray fileToDownload = new JSONArray();

        if (!updateJson.containsKey("files") && updateJson.get("files") instanceof JSONArray) {
            throw new Exception("Update is invalid !");
        }

        if (localJson == null) {
            log.info("Rule : Downloading all file");
            fileToDownload = (JSONArray) updateJson.get("files");
        } else {
            log.info("Rule : Delta calculation");
            calculateDelta((JSONArray) localJson.get("files"), (JSONArray) updateJson.get("files"), fileToDelete, fileToDownload, minecraft, checkLocal);
        }

        File tempDir = new File(minecraft, "tmpdl");
        if (!tempDir.exists()) {
            FileUtils.forceMkdir(tempDir);
        }

        if (fileToDownload.size() > 0) log.info("Downloading files...");
        for (Object aFileToDownload : fileToDownload) {
            JSONObject json = (JSONObject) aFileToDownload;
            String file = (String) json.get("name");
            String url = (String) json.get("url");
            String hash = (String) json.get("checksum");
            Integer size = (Integer) json.get("size");
            File destFile = new File(tempDir, file);

            // Check if file destination is outside the Miroa working directory
            if (!destFile.getAbsolutePath().startsWith(minecraft.getAbsolutePath())) {
                log.severe("Unsafe modification detected, the directory is outside of the launcher for ".concat(file));
                throw new Exception("Update file contain unsafe modification, aborting process !");
            }

            // If the file exist, check if the downloaded version is correct
            // Useful when the launcher has been closed when updating content
            if (destFile.exists()) {
                log.fine("File ".concat(file).concat(" exist, checking content"));
                FileInputStream fis = new FileInputStream(destFile);
                if (size.floatValue() != destFile.length()
                        || !DigestUtils.sha1Hex(fis).equals(hash)) {
                    log.info("Deleting errored file ".concat(file));
                    fis.close();
                    FileUtils.forceDelete(destFile);
                } else {
                    continue;
                }
            }

            // Download all missing file in the temporary download directory
            log.info("Downloading file at ".concat(url));
            progress.setStatus("Downloading ".concat(file));
            sk.tomsik68.mclauncher.util.FileUtils.downloadFileWithProgress(url, destFile, progress);
        }

        // Delete all unwanted files
        deleteFiles(fileToDelete, minecraft);

        // Write the updated package data
        sk.tomsik68.mclauncher.util.FileUtils.writeFile(localFile, jsonString);

        // Move all downloaded files
        moveFiles(fileToDownload, tempDir, minecraft);

        // Delete the tmp dir when the launcher exit
        FileUtils.forceDeleteOnExit(tempDir);
    }

    /**
     * Move all downloaded file in there final destination
     * @param fileToMove All file to move
     * @param tempDir Download working directory
     * @param ws Miroa working directory
     * @throws Exception
     */
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

            if (dst.exists()) {
                FileUtils.forceDelete(dst);
            }
            FileUtils.moveFileToDirectory(src, dst.getParentFile(), true);
        }

    }

    /**
     * Delete each file in the array
     * @param fileToDelete All file to delete
     * @param ws The Miroa working directory
     * @throws IOException
     */
    private static void deleteFiles(JSONArray fileToDelete, File ws) throws IOException {
        for (Object aFileToDelete : fileToDelete) {
            JSONObject json = (JSONObject) aFileToDelete;
            String fileStr = (String) json.get("name");
            File file = new File(ws, fileStr);
            //File parent = file.getParentFile();

            log.info("deleting file ".concat(fileStr));
            if (file.exists())
                FileUtils.forceDelete(file);

            // @TODO Remove empty directory
        }
    }

    /**
     * Compare the local and remote package data
     * @param local local json
     * @param update remote json
     * @param fileToDelete all file to delete array
     * @param fileToDownload all file to download array
     */
    private static void calculateDelta(JSONArray local, JSONArray update, JSONArray fileToDelete, JSONArray fileToDownload, File ws, boolean checkLocalFiles) {
        JSONObject lJson, uJson;
        String lStr, uStr, fStr;
        FileInputStream fis;
        int lSize, uSize;

        // Compare local and update array
        lSize = local.size();
        for (int i = 0; i < lSize; i++) {
            uSize = update.size();
            for (int j = 0; j < uSize; j++) {
                uJson = (JSONObject) local.get(i);
                lJson = (JSONObject) update.get(j);
                lStr = (String) lJson.get("name");
                uStr = (String) uJson.get("name");

                // If the same file name is found
                if (lStr.equals(uStr)) {
                    fis = null;
                    fStr = "";
                    if (checkLocalFiles) {
                        // Get the local file hash
                        try {
                            fis = new FileInputStream(new File(ws, lStr));
                            fStr = DigestUtils.sha1Hex(fis);
                        } catch (FileNotFoundException ignored) {
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fis != null)
                                try {
                                    fis.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }

                    lStr = (String) ((JSONObject) local.get(i)).get("checksum");
                    uStr = (String) ((JSONObject) update.get(j)).get("checksum");

                    // If hashes are incorrect, download the file and remove the local file
                    if (!lStr.equals(uStr) || (checkLocalFiles && !lStr.equals(fStr))) {
                        fileToDelete.add(lJson);
                        fileToDownload.add(uJson);
                    }

                    // Remove found file from arrays
                    local.remove(i);
                    update.remove(j);
                    i--;
                    lSize = local.size();
                    break;
                }
            }
        }

        // For each local file remaining delete them
        lSize = local.size();
        for (int i = 0; i < lSize; i++) {
            fileToDelete.add(local.get(i));
        }

        // For each file remaining in the download list, set to download them
        uSize = update.size();
        for (int i = 0; i < uSize; i++) {
            fileToDownload.add(update.get(i));
        }
    }

}
