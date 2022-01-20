package fhsmc.minecraft.gary.Bot;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import fhsmc.minecraft.gary.Config;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AuthFlow {

    private final SlashCommandEvent event;

    private String deviceCode;

    private JSONObject data;

    private Timer exec;

    public AuthFlow(SlashCommandEvent event) {
        this.event = event;
        try {
            data = GoogleOAuth.startAuthFlow();

            deviceCode = data.getString("device_code");
            int expires = data.getInt("expires_in");
            int interval = data.getInt("interval");

            exec = new Timer();
            exec.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    pollTask();
                }
            }, interval * 1000L, interval * 1000L);

            exec.schedule(new TimerTask() {
                @Override
                public void run() {
                    GaryBot.authFlowTimeout(event);
                    exec.cancel();
                    exec.purge();
                }
            }, expires * 1000L);

            sendFirstEmbed(this.event);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFirstEmbed(SlashCommandEvent event) {
        event.getHook().editOriginalEmbeds(InfoEmbed.fromString("To get whitelisted, we need to make sure you have a valid school email. "
                + "Follow the instructions below:\n\n"
                + "- Go to "
                + this.data.getString("verification_url")
                + " and enter the following code: "
                + "**`" + this.data.getString("user_code") + "`**\n\n"
                + "- Log in with your school google account\n\nAfterword you will be able to whitelist your Minecraft account.\n"
                + "*We do not store your email or anything else about your Google account. "
                + "This only needs to be done once to confirm you have a valid school email.*\n\n").build()
        ).queue();
    }

    private void pollTask() {
        try {
            JSONObject token_data = GoogleOAuth.pollGoogleAuth(deviceCode);
            if (token_data.has("error") && !token_data.get("error").equals("authorization_pending")) {
                throw new IOException("Error when polling Google servers: " + token_data.get("error"));
            } else {
                if (token_data.has("id_token")) {
                    postReceiveToken(token_data.getString("id_token"));
                    exec.cancel();
                    exec.purge();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.event.getHook().editOriginalEmbeds(
                    InfoEmbed.fromString(":warning: Whoops, looks like we got an error from Google servers! Contact staff for help.")
                            .build()
            ).queue();
        }
    }

    private void postReceiveToken(String token) {
        String email;
        try {
            DecodedJWT jwt = JWT.decode(token);
            email = jwt.getClaim("email").toString().replace("\"", "");
        } catch (JWTDecodeException exception){
            exception.printStackTrace();
            this.event.getHook().editOriginalEmbeds(
                    InfoEmbed.fromString(":warning: Whoops, looks like there was a problem with the data we got from Google servers. Contact staff for help.")
                            .build()
            ).queue();
            return;
        }

        GaryBot.authFlowComplete(this.event, email.endsWith(Config.getString("google.email-suffix")), email);
    }
}
