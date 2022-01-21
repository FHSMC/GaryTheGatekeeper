package fhsmc.minecraft.gary;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;

public class Listeners implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event){
        ProxiedPlayer player = event.getPlayer();

        try {
            if (!Storage.isPlayerUUIDWhitelisted(player.getUniqueId().toString(), player.getName().startsWith("."))) {
                if (!Storage.isPlayerIGNWhitelisted(player.getName(), player.getName().startsWith("."))) {
                    player.disconnect(new TextComponent(Config.getString("messages.not-whitelisted")));
                } else {
                    Storage.setUUIDFromIGN(
                            player.getName(),
                            player.getUniqueId().toString(),
                            player.getName().startsWith(".")
                    );
                }
            } else {
                if (!Storage.isPlayerIGNWhitelisted(player.getName(), player.getName().startsWith("."))) {
                    Storage.setIGNFromUUID(
                            player.getUniqueId().toString(),
                            player.getName(),
                            player.getName().startsWith(".")
                    );
                }

                if (Storage.isDiscordIdDisabled(
                        Storage.getDiscordFromIGN(player.getName(), player.getName().startsWith("."))
                    )) {
                        player.disconnect(new TextComponent(Config.getString("messages.discord-disabled")));
                }
            }
        } catch (SQLException e) {
            player.disconnect(new TextComponent(Config.getString("messages.whitelist-check-error")));
            e.printStackTrace();
        }

    }
}
