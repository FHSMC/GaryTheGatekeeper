package fhsmc.minecraft.gary.Bot;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuthFlow {

    public AuthFlow(ButtonClickEvent event) {
        JSONObject data = null;
        try {
            data = GoogeOAuth.startAuthFlow();

            String deviceCode = data.getString("device_code");
            String userCode = data.getString("user_code");
            int expires = data.getInt("expires_in");
            int interval = data.getInt("interval");

            String url = data.getString("verification_url");

            ScheduledExecutorService exec = Executors.newScheduledThreadPool(10);
            ScheduledFuture<?> task = exec.scheduleAtFixedRate(() -> {}, 0, interval, TimeUnit.SECONDS);
            exec.scheduleWithFixedDelay(() -> task.cancel(true), 0, expires, TimeUnit.SECONDS);

            event.reply("To get whitelisted, we need to make sure you have a valid school email."
                    + "Follow the instructions below:\n"
                    + "*Go to "
                    + url
                    + " and enter the following code: "
                    + "**`" + userCode + "`**"
                    + "\nWhen you have complete the steps there, you will be able to whitelist your Minecraft account.").setEphemeral(true).queue();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

}
