package fhsmc.minecraft.gary;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;


public final class GaryTheGatekeeper extends Plugin {

    @Override
    public void onEnable() {
        File dataFolder = this.getDataFolder();
        dataFolder.mkdirs();
        Storage.open( dataFolder + "/whitelist.db");

        getProxy().getPluginManager().registerListener(this, new Listeners());
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
