/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.updater;

import cope.saturn.core.Saturn;
import cope.saturn.util.internal.Wrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.NarratorManager;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VersionChecker {
    public static final Logger LOGGER = LoggerFactory.getLogger("VersionChecker");

    /**
     * Represents the latest URL in raw text
     *
     * This can really be any URL, as long as it returns text upon a request
     */
    public static final String LATEST_URL = "https://raw.githubusercontent.com/Sxmurai/saturn-client/version/version.txt";

    /**
     * Handles updating the client
     */
    public static void handleUpdates() {
        if (Saturn.VERSION.endsWith("-beta")) {
            LOGGER.info("Client version is in not in release channel, will not run updates.");
            return;
        }

        Path modsFolder = getMinecraftModsFolder();
        if (modsFolder == null) {
            LOGGER.error("Minecraft mods folder not found?");
            return;
        }

        // this needs to be set to false otherwise upon initializing JFrame it will crash
        System.setProperty("java.awt.headless", "false");

        LOGGER.info("Fetching latest version...");
        String latest = fetchVersion();

        if (Saturn.VERSION.equalsIgnoreCase(latest)) {
            LOGGER.info("On latest version {}!", Saturn.VERSION);
            return;
        }

        try {
            if (Float.parseFloat(Saturn.VERSION) > Float.parseFloat(latest)) {
                LOGGER.warn("Client version {} is greater than server version {}? Possible development environment?", Saturn.VERSION, latest);
                return;
            }
        } catch (NumberFormatException ignored) {

        }

        LOGGER.info("Version mismatch, client: {}, server: {}", Saturn.VERSION, latest);

        int response = JOptionPane.showConfirmDialog(
                null,
                "Version outdated! Client version is on v" + Saturn.VERSION + ", and we're on v" + latest + "\nWould you like to update?",
                "Saturn Updater",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            LOGGER.info("Updating client version to {} from {}", latest, Saturn.VERSION);

            new Thread(() -> {
                try {
                    FileUtils.copyURLToFile(
                            new URL("https://github.com/Sxmurai/saturn-client/blob/releases/saturn-" + latest + ".jar?raw=true"),
                            modsFolder.resolve("saturn-" + latest + ".jar").toFile());

                    // stop minecraft instance
                    // we cannot replace the jar while the client is running, so we'll need to do this
                    // we also cannot call .stop() because it calls System.exit before we'll be able to do anything

                    try {
                        NarratorManager.INSTANCE.destroy();

                        if (Wrapper.mc.world != null) {
                            Wrapper.mc.world.disconnect();
                        }

                        if (Wrapper.mc.currentScreen != null) {
                            Wrapper.mc.currentScreen.removed();
                        }

                        Wrapper.mc.close();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    Files.delete(modsFolder.resolve("saturn-" + Saturn.VERSION + ".jar"));
                } catch (IOException e) {
                    Saturn.LOGGER.error("Could not download JAR into mods folder or could not delete current mod, stacktrace follows:\n{}", e.toString());
                }

                JOptionPane.showMessageDialog(
                        null,
                        "Downloaded and replaced mod jar! Please restart your minecraft instance!",
                        "Saturn Updater",
                        JOptionPane.INFORMATION_MESSAGE);
            }).start();
        }
    }

    /**
     * Fetches the latest version of the client
     * @return The latest client version, or defaulting to Saturn.VERSION if request fails.
     */
    public static String fetchVersion() {
        HttpClient client = HttpClients.createDefault();
        try {
            HttpGet request = new HttpGet(LATEST_URL);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:97.0) Gecko/20100101 Firefox/97.0");
            request.setHeader("Content-Type", "text/plain; charset=utf-8");

            HttpResponse response = client.execute(request);

            InputStream stream = response.getEntity().getContent();
            String text = new String(stream.readAllBytes());
            stream.close();

            return text;
        } catch (IOException e) {
            Saturn.LOGGER.error("Could not fetch latest client version, stacktrace follows:\n{}", e.toString());
            return Saturn.VERSION;
        }
    }

    /**
     * Finds minecraft's mods folder
     * @return The file path to the mods folder
     */
    private static Path getMinecraftModsFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        Path home = Paths.get(System.getProperty("user.home"));

        if (os.contains("win")) {
            return home.resolve("/AppData/Roaming/.minecraft/mods/");
        } else if (os.contains("mac")) {
            return home.resolve("/Library/Application Support/minecraft/mods/");
        } else if (os.contains("nix") || os.contains("linux")) {
            return home.resolve(".minecraft/mods/");
        } else {
            return null;
        }
    }
}
