# GaryTheGatekeeper

Gary is a gate system for Discord and Bungeecord. It was made specifically for controlling a server whitelist via Discord, but only if the user can log in with a valid school google email.

Gary is also meant to work on a server running Gyser, with both Java and Bedrock accounts

### Usage

To run Gary, the JAR artifact can be placed in the Bungeecord `plugins` folder. Once run once, Gary will generate a [config.yml](/src/main/resources/config.yml) where you can place your API tokens, and other configuration.

In Discord, make sure to invite Gary to your Discord with the following URL
```
https://discord.com/api/oauth2/authorize?client_id=<your-client-id>&permissions=0&scope=bot%20applications.commands
```
This will invite Gary as a bot account, and give him permission to create slash commands

#### Commands
All of Gary's Discord commands are subcommands of the `/whitelist` command:
- `info` - Show info about what Gary is, and if you are authorized, your whitelist data
- `set <Java|Bedrock> <username>` - Set your Java or Bedrock username
- `remove <Java|Bedrock>` - Remove your Java or Bedrock username from the whitelist

**In game** commands are under the `/gary` command. There are also the `gwhitelist` and `gw` aliases. This command requires the `gary.command` permission.
- `set <discord-id> <java|bedrock> <username>` - Set a user's username by Discord ID
- `add <java|bedrock> <username>` - Add a user to the whitelist without a Discord ID or email
- `remove <java|bedrock> <username>` - Remove a username from the whitelist
- `allow <discord-id>` - Allow a Discord ID to use slash commands without authenticating
- `revoke <discord-id>` - Remove a Discord ID from the list of authenticated users. **Users can just authenticate themselves again.**
- `block <discord-id>` - Block a Discord ID from using slash commands or joining the game.
- `show <id|email|username> <value>` - Show information about a user, similar to the `info` slash command

### What does Gary store?
The only thing that Gary stores from you logging into Google is your email. No other information or tokens are saved that would give him access to your account at a later time. This email is linked to the Discord ID that was used to first run the command in order to prevent multiple Discord accounts using the same email.

### TODO List
- [X] ~~Username regex~~
- [X] ~~*More database methods*~~
  - [X] ~~Actually fetch values instead of just checking if they exist~~
- [X] ~~Remove username method~~
- [ ] *In-game commands*
  - [X] ~~anonymous add~~
  - [X] ~~by ID add~~
  - [X] ~~remove by username~~
  - [X] ~~allow discord id~~
  - [X] ~~remove discord id~~
  - [ ] show whitelist
  - [X] show individual user info
- [X] ~~Clear UUID when username changed with command~~
- [X] ~~Whitelist kick message~~
- [X] ~~Cooldowns~~
- [X] ~~Show whitelisted usernames on info command~~
- [X] ~~Check id Discord ID disabled in listeners~~

- [X] ~~*need to store email to prevent using same email on multiple discord accounts*~~
- [ ] Expand to handle bans and stuff