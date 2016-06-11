<?php
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

$CONFIG = [
    "folder_scan" => __DIR__ . "/files",
    "password" => "Meowww",
    "json_file" => __DIR__ . "/update.json",
    "url" => "http://static.safranil.fr/minecraft/files%s"
];

$json = [
    "last_update" => time(),
    "files" => [],
];

function logger($message, $level = "INFO")
{
    printf("[%' 7s] %s" . PHP_EOL, $level, $message);
}

function parseFiles($baseDir, $dir, $eDir = null)
{
    global $json, $CONFIG;

    if ($eDir === null) $eDir = $dir;

    $handle = opendir($baseDir . $dir);
    if ($handle === false) {
        logger("Unable to open $dir", "ERROR");
        return;
    }

    logger("Opened dir $dir");

    while (($name = readdir($handle)) && $name !== false) {
        if ($name == ".." || $name == ".")
            continue;

        $fullDir = $dir . "/" . $name;
        $fullEDir = $eDir . "/" . rawurlencode($name);

        if (is_dir($baseDir . $fullDir)) {
            parseFiles($baseDir, $fullDir, $fullEDir);
        } elseif (is_file($baseDir . $fullDir)) {
            logger("Parsing $fullDir");

            $json["files"][] = [
                "name" => $fullDir,
                "url" => sprintf($CONFIG["url"], $fullEDir),
                "size" => filesize($baseDir . $fullDir),
                "checksum" => sha1_file($baseDir . $fullDir)
            ];
        }

    }

    closedir($handle);
}

header('Content-Type: text/plain');
if (PHP_SAPI != "cli" && isset($_GET["password"]) && $_GET["password"] != $CONFIG["password"]) {
    logger("Wrong password.", "ERROR");
    exit();
} else {
    logger("Password accepted.");
}

logger("Starting update at " . date("d/m/Y H:i:s") . "...");

parseFiles($CONFIG["folder_scan"], "");

file_put_contents($CONFIG["json_file"], json_encode($json, JSON_PRETTY_PRINT), LOCK_EX);
logger("Update ended.");