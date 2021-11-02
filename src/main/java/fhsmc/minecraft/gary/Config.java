package fhsmc.minecraft.gary;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private static Configuration config;

    public static void loadConfig(File configPath) throws IOException {
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configPath);
    }

    public static String getString(String key) {
        return config.getString(key);
    }

    public static int getInt(String key) {
        return config.getInt(key);
    }

}
