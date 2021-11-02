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
            if (!Storage.isPlayerUUIDWhitelisted(player.getUniqueId().toString())) {
                if (!Storage.isPlayerIGNWhitelisted(player.getName())){
                    player.disconnect(new TextComponent("You are not whitelisted!"));
                } else {
                    Storage.setUUIDFromIGN(
                            player.getName(),
                            player.getUniqueId().toString(),
                            player.getName().startsWith(".")
                    );
                }
            }
        } catch (SQLException e) {
            player.disconnect(new TextComponent("There was an error while checking the whitelist. Please contact staff."));
            e.printStackTrace();
        }

    }
}
