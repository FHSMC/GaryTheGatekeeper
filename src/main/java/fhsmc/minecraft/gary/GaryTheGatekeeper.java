package fhsmc.minecraft.gary;

import fhsmc.minecraft.gary.Bot.GaryBot;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


public final class GaryTheGatekeeper extends Plugin {

    @Override
    public void onEnable() {
        try {
            File dataFolder = this.getDataFolder();

            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            try {
                loadConfig();
            } catch (IOException e) {
                getProxy().getLogger().warning("Failed to load configuration file.");
                throw e;
            }

            Storage.open(dataFolder + "/whitelist.db");

            getProxy().getPluginManager().registerListener(this, new Listeners());
            getProxy().getPluginManager().registerCommand(this, new MainCommand());

            GaryBot.run();

        } catch (Exception e) {
            getProxy().getLogger().warning("Gary experienced an error while starting, so he's disabling himself.");
            onDisable();
            e.printStackTrace();
        }
    }

    public void loadConfig() throws IOException {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Config.loadConfig(configFile);
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterCommands(this);
        getProxy().getPluginManager().unregisterListeners(this);
    }

}
