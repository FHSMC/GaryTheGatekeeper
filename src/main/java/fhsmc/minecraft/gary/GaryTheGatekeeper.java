package fhsmc.minecraft.gary;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


public final class GaryTheGatekeeper extends Plugin {

    private Configuration config;

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

            System.out.println(config.getString("test_value"));

            Storage.open(dataFolder + "/whitelist.db");

            getProxy().getPluginManager().registerListener(this, new Listeners());
            getProxy().getPluginManager().registerCommand(this, new MainCommand());
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

        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterCommands(this);
        getProxy().getPluginManager().unregisterListeners(this);
    }
}
