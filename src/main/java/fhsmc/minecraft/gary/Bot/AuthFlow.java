package fhsmc.minecraft.gary.Bot;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import fhsmc.minecraft.gary.Config;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AuthFlow {

    private SlashCommandEvent _event;

    private String deviceCode;
    private String accessToken;

    private Timer exec;

    public AuthFlow(SlashCommandEvent event) {
        _event = event;
        JSONObject data = null;
        try {
            data = GoogleOAuth.startAuthFlow();

            deviceCode = data.getString("device_code");
            String userCode = data.getString("user_code");
            int expires = data.getInt("expires_in");
            int interval = data.getInt("interval");

            String url = data.getString("verification_url");

            exec = new Timer();
            GoogleOAuth.pollGoogleAuth(deviceCode);
            exec.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    pollTask();
                }
            }, interval * 1000, interval * 1000);

            exec.schedule(new TimerTask() {
                @Override
                public void run() {
                    exec.cancel();
                    exec.purge();
                }
            }, expires * 1000);

            _event.getHook().editOriginalEmbeds(InfoEmbed.fromString("To get whitelisted, we need to make sure you have a valid school email. "
                    + "Follow the instructions below:\n\n"
                    + "· Go to "
                    + url
                    + " and enter the following code: "
                    + "**`" + userCode + "`**\n\n"
                    + "· Log in with your school google account\n\nAfterword you will be able to whitelist your Minecraft account.\n"
                    + "*We do not store your email or anything else about your Google account. "
                    + "This only needs to be done once to confirm you have a valid school email.*").build()
            ).queue();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pollTask() {
        try {
            JSONObject token_data = GoogleOAuth.pollGoogleAuth(deviceCode);
            if (token_data.has("error") && !token_data.get("error").equals("authorization_pending")) {
                throw new IOException("Error when polling Google servers: " + token_data.get("error"));
            } else {
                if (token_data.has("id_token")) {
                    postRecieveToken(token_data.getString("id_token"));
                    exec.cancel();
                    exec.purge();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            _event.getHook().editOriginalEmbeds(
                    InfoEmbed.fromString(":warning: Woops, looks like we got an error from Google servers! Contact staff for help.")
                            .build()
            ).queue();
        }
    }

    private void postRecieveToken(String token) {
        String email;
        try {
            DecodedJWT jwt = JWT.decode(token);
            email = jwt.getClaim("email").toString().replace("\"", "");
        } catch (JWTDecodeException exception){
            exception.printStackTrace();
            _event.getHook().editOriginalEmbeds(
                    InfoEmbed.fromString(":warning: Woops, looks like there was a problem with the data we got from Google servers. Contact staff for help.")
                            .build()
            ).queue();
            return;
        }

        if (email != null && email.endsWith(Config.getString("google.email-suffix"))) {
            GaryBot.authFlowComplete(_event, true, email);
        } else {
            GaryBot.authFlowComplete(_event, false, email);
        }
    }
}
