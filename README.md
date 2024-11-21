![JoinGuard Logo](JoinGuard.jpg "JoinGuard Logo")

# JoinGuard

JoinGuard is a powerful Minecraft plugin and web service duo designed to shield your server from disruptive players.
This comprehensive solution not only blocks harmful players from joining but also enables server owners to report offenders directly through an integrated web platform.
With seamless reporting, blacklisting, and robust security features, JoinGuard provides peace of mind and keeps your community safe from unwanted behavior.

[![BStats](https://bstats.org/signatures/bukkit/QC-JoinGuard.svg)](https://bstats.org/plugin/bukkit/QC-JoinGuard/23474)

## 🔒 Features

- **Player Blocking**: Prevents players deemed harmful from joining the server.
- **Automated Integration**: Automatic integration with external ban lists.
- **Report Option**: Allows server owners to report new harmful players for further action.
- **Whitelist Management**: Add/remove players from the whitelist to bypass restrictions.
- **Alt Viewer**: View a player's alternate accounts (alts).
- **Concurrent Connections**: Limit the number of concurrent connections from the same IP.
- **Maintenance Mode**: Prevents players from joining during maintenance mode.
- **Encrypted Database**: Stores data securely in encrypted format.
- **Discord Integration**: Allows players to log in to JoinGuard through discord for reporting access.
- **Online/Offline Support**: Compatible with both online and offline server modes.
- **Join Logging**: Logs blocked join attempts to a file, console or online operators.
- **Automatic Updates**: Notifies operators of available updates, applied with `/joinguard update` (restart required).
- **MultiVersion Support**: Supports multiple versions of Minecraft. (1.8, 1.9, 1.10, 1.11, 1.12, 1.13, 1.14, 1.15, 1.16, 1.17, 1.18, 1.19, 1.20, 1.21)
- **Tested Versions**: 1.8, 1.16, 1.17, 1.18, 1.19, 1.20, 1.21

## 🚀 Installation

1. Download the latest release of `QC-JoinGuard.jar` from the [Releases](https://github.com/GameTronic/QC-JoinGuard/releases) section.
2. Place the `.jar` file into the `plugins` folder of your Minecraft server.
3. Restart or reload your server to enable JoinGuard.

## 🔧 Commands

- `/joinguard report <player>`: Report a harmful player.
- `/joinguard login`: Log in to JoinGuard via Discord for reporting access.
- `/joinguard list`: View all blocked players.
- `/joinguard reload`: Reload the plugin configuration.
- `/joinguard whitelist <add/list/remove> <player>`: Manage the whitelist.
- `/joinguard alts <player>`: View a player’s alternate accounts.
- `/joinguard maintenance <on/off/status>`: Toggle maintenance mode or view its status.
- `/joinguard version`: View the current plugin version.
- `/joinguard update`: Update to the latest plugin version.

## 🔑 Permissions

- `qc.joinguard.cmd`: Access to /joinguard command.
- `qc.joinguard.cmd.report`: Allows you to report harmful players.
- `qc.joinguard.cmd.login`: Allows you to log in to JoinGuard, allowing you to report new harmful players.
- `qc.joinguard.cmd.list`: Allows you to view the list of blocked players.
- `qc.joinguard.cmd.reload`: Allows you to reload the plugin.
- `qc.joinguard.cmd.whitelist`: Allows you to manage the whitelist.
- `qc.joinguard.cmd.alts`: Allows you to view a player’s alternate accounts.
- `qc.joinguard.cmd.maintenance`: Allows you to toggle maintenance mode.
- `qc.joinguard.cmd.version`: Allows you to view the plugin version.
- `qc.joinguard.cmd.update`: Allows you to update the plugin to the latest version.
- `qc.joinguard.logger`: Receive notifications of blocked join attempts.
- `qc.joinguard.bypass.concurrent`: Allows you to bypass the concurrent connections limit.
- `qc.joinguard.bypass.maintenance`: Allows you to join the server during maintenance mode.

## ⚙️ Configuration

You can customize the plugin in `config.yml`:

```yaml
#
# QC-JoinGuard Config
#    by QuackDuck
#

# Server api key
api-key: "API_KEY"

# Message when blocked player is trying to log in
kick-message: "&cThis server is protected by JoinGuard."

# Whitelisted players that are allowed to join the server even if they are blocked.
whitelist: []

# Concurrent connections
# Maximum number of concurrent connections from the same IP
concurrent-connections:
  # Maximum number of concurrent connections, set to 0 to disable
  max: 3
  # Kick message when the limit is reached
  kick-message: "&cToo many players connected from your IP."
  # Is bypass permission working?
  # Players with 'qc.joinguard.bypass.concurrent' permission bypass the limit if true
  bypass: true

# Maintenance mode
# Only operators with 'qc.joinguard.bypass.maintenance' permission can join
maintenance:
  # Enable maintenance mode
  enabled: false
  # Kick message when maintenance mode is enabled
  message: "&cThe server is currently under maintenance. Please try again later."
  # Kick all players when maintenance mode is turned on
  kick-all: true

# Logger
# Should blocked join attempts be logged?
logger:
  # Log to file
  file: true
  # Log to console
  console: false
  # Log to online operators with 'qc.joinguard.logger' permission
  operator: false

# End of file
```

## 🆘 Support

For assistance with the **JoinGuard** plugin, please join our Discord community where you can ask questions and receive support:

[Join the QuackCode Discord Server](https://discord.gg/5PFQ4bhCeD)

If you encounter any issues, feel free to report them on our GitHub repository:

[Open an Issue on GitHub](https://github.com/GameTronic/QC-JoinGuard/issues)

## 🤝 Contributing

Contributions are welcome! For new features or improvements, submit a pull request:

[Submit a Pull Request on GitHub](https://github.com/GameTronic/QC-JoinGuard/pulls)

## 📜 License

JoinGuard is proprietary software. Unauthorized distribution or modification is prohibited, except for sharing this repository link. For more detailed legal information, please refer to the [LICENSE](LICENSE) file in this repository.

### Thank you for using JoinGuard!