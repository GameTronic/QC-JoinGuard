package me.quackduck.qcjoinguard.cmds;
// Created by QuackDuck
import com.google.common.base.Joiner;
import me.quackduck.qcjoinguard.QC_JoinGuard;
import me.quackduck.qcjoinguard.api.Api;
import me.quackduck.qcjoinguard.files.Alts;
import me.quackduck.qcjoinguard.api.Auth;
import me.quackduck.qcjoinguard.api.Blocked;
import me.quackduck.qcjoinguard.files.Updater;
import me.quackduck.qcjoinguard.misc.*;
import me.quackduck.qcjoinguard.schedulers.AutoSave;
import me.quackduck.qcjoinguard.schedulers.UpdateLists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Cmd implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("joinguard")) {
			if (!sender.hasPermission("qc.joinguard.cmd")) {
				noAccess(sender);
				return true;
			}
			if (args.length == 0) {
				sendUsage(sender);
				return true;
			}
			switch (args[0].toLowerCase()) {
				case "reload":
					if (!sender.hasPermission("qc.joinguard.cmd.reload")) {
						noAccess(sender);
						return true;
					}
					handleReload(sender);
					break;
				case "report":
					if (!sender.hasPermission("qc.joinguard.cmd.report")) {
						noAccess(sender);
						return true;
					}
					handleReport(sender, args);
					break;
				case "login":
					if (!sender.hasPermission("qc.joinguard.cmd.login")) {
						noAccess(sender);
						return true;
					}
					handleLogin(sender);
					break;
				case "list":
					if (!sender.hasPermission("qc.joinguard.cmd.list")) {
						noAccess(sender);
						return true;
					}
					handleList(sender);
					break;
				case "whitelist":
					if (!sender.hasPermission("qc.joinguard.cmd.whitelist")) {
						noAccess(sender);
						return true;
					}
					handleWhitelist(sender, args);
					break;
				case "alts":
					if (!sender.hasPermission("qc.joinguard.cmd.alts")) {
						noAccess(sender);
						return true;
					}
					handleAlts(sender, args);
					break;
				case "maintenance":
					if (!sender.hasPermission("qc.joinguard.cmd.maintenance")) {
						noAccess(sender);
						return true;
					}
					handleMaintenance(sender, args);
					break;
				case "version":
					if (!sender.hasPermission("qc.joinguard.cmd.version")) {
						noAccess(sender);
						return true;
					}
					handleVersion(sender);
					break;
				case "update":
					if (!sender.hasPermission("qc.joinguard.cmd.update")) {
						noAccess(sender);
						return true;
					}
					handleUpdate(sender);
					break;
				default:
					sendUsage(sender);
			}
			return true;
        }
		return true;
	}

	private void handleReload(CommandSender sender) {
		AutoSave.stop();
		UpdateLists.stop();
		Config.reload();
		AutoSave.start();
		Auth.generateUUID();
		Auth.tryLoggingIn();
		UpdateLists.start();
		sendMessage(sender, QC_JoinGuard.prefix + Colors.GREEN + " Konfiguracja została przeładowana oraz baza danych została zaktualizowana!");
		if (!Auth.isLoggedIn()) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("qc.joinguard.cmd.login") || player.isOp()) {
					Auth.notify(player);
				}
			}
			Auth.notify(Bukkit.getConsoleSender());
		}
	}

	private void handleReport(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Tylko gracz może użyć tej komendy!");
			return;
		}
		if (args.length != 2) {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard report <gracz>");
			return;
		}
        Player reportedPlayer = Bukkit.getPlayer(args[1]);
		if (reportedPlayer == null) {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Gracz " + args[1] + " jest offline!");
			return;
		}
		Player reporter = (Player) sender;
		JSONObject reportJson = createReportJson(reportedPlayer, reporter);
		String base64Report = Base64.getEncoder().encodeToString(reportJson.toString().getBytes(StandardCharsets.UTF_8));
		String url = "https://joinguard.raidvm.com/login/oauth2?state=" + base64Report;
		if (Utils.getGeneralMCVersion() > 16) {
			BaseComponent message = createClickableMessage(QC_JoinGuard.prefix + Colors.AQUA + " &nKliknij tutaj aby wysłać zgłoszenie!", url);
			sender.spigot().sendMessage(message);
		} else {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.AQUA + " Kliknij w link aby wysłać zgłoszenie!");
			sendMessage(sender, Colors.BLUE + url);
		}
	}

	private void handleLogin(CommandSender sender) {
		if (!(sender instanceof Player)) {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Tylko gracz może użyć tej komendy!");
			return;
		}
		String response = Api.getPayload("https://joinguard.raidvm.com/api/check?api=" + Auth.getKey(), "status");
		if (response == null) {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Brak odpowiedzi od API!");
			return;
		}
		if (Config.getBoolean("debug", false)) {
			Utils.debug(response);
		}
		if (response.equalsIgnoreCase("ok")) {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.GREEN + " Jesteś już zalogowany!");
		} else if(response.equalsIgnoreCase("Invalid API key")) {
			String url = "https://joinguard.raidvm.com/api/register?state=" + Auth.getKey();
			if (Utils.getGeneralMCVersion() > 16) {
				BaseComponent message = createClickableMessage(QC_JoinGuard.prefix + Colors.AQUA + " &nKliknij tutaj aby się zarejestrować!", url);
				sender.spigot().sendMessage(message);
			} else {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.AQUA + " Kliknij w link aby się zarejestrować!");
				sendMessage(sender, Colors.BLUE + url);
			}
		} else {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Brak odpowiedzi od API!");
		}
	}

	private void handleList(CommandSender sender) {
		if (Blocked.getNames().isEmpty()) {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.YELLOW + " Brak zablokowanych graczy.");
			return;
		}
		sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Lista zablokowanych:");
		sendMessage(sender, Colors.YELLOW + Joiner.on(", ").skipNulls().join(Blocked.getNames()));
	}

	private void handleWhitelist(CommandSender sender, String[] args) {
		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("add")) {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard whitelist add <gracz>");
			} else if (args[1].equalsIgnoreCase("list")) {
				if (Config.getStringList("whitelist").isEmpty()) {
					sendMessage(sender, QC_JoinGuard.prefix + Colors.YELLOW + " Brak odblokowanych graczy.");
					return;
				}
				sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Lista odblokowanych:");
				sendMessage(sender, Colors.YELLOW + Joiner.on(", ").skipNulls().join(Config.getStringList("whitelist")));
			} else if (args[1].equalsIgnoreCase("remove")) {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard whitelist remove <gracz>");
			} else {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard whitelist <add/list/remove>");
			}
		} else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("add")) {
				if (Config.getStringList("whitelist").contains(args[2])) {
					sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Gracz " + args[2] + " jest już odblokowany!");
				} else {
					List<String> list = Config.getStringList("whitelist");
					list.add(args[2]);
					Config.set("whitelist", list);
					Config.save();
					sendMessage(sender, QC_JoinGuard.prefix + Colors.GREEN + " Gracz " + args[2] + " został odblokowany!");
				}
			} else if (args[1].equalsIgnoreCase("list")) {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard whitelist list");
			} else if (args[1].equalsIgnoreCase("remove")) {
				if (Config.getStringList("whitelist").contains(args[2])) {
					List<String> list = Config.getStringList("whitelist");
					list.remove(args[2]);
					Config.set("whitelist", list);
					Config.save();
					sendMessage(sender, QC_JoinGuard.prefix + Colors.GREEN + " Gracz " + args[2] + " został zablokowany!");
				} else {
					sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Gracz " + args[2] + " jest już zablokowany!");
				}
			} else {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard whitelist <add/list/remove>");
			}
		} else {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard whitelist <add/list/remove>");
		}
	}

	private void handleAlts(CommandSender sender, String[] args) {
		if (args.length == 2) {
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null) {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Gracz " + args[1] + " jest offline!");
				return;
			}
			sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Konta powiązane z graczem " + args[1] + ":");
			List<String> hIps = new ArrayList<>();
			for (Map.Entry<String, AltsEntry> entry : Alts.getDb().entrySet()) {
				if (entry.getValue().getPlayers().contains(p.getUniqueId())) {
					hIps.add(entry.getKey());
				}
			}
			List<UUID> alts = new ArrayList<>();
			for (String ip : hIps) {
				alts.addAll(Alts.getDb().get(ip).getPlayers());
			}
			List<String> names = new ArrayList<>();
			for (UUID uuid : alts) {
				String name = Bukkit.getOfflinePlayer(uuid).getName();
				if (name != null && !names.contains(name)) {
					names.add(name);
				}
			}
			sendMessage(sender, Colors.YELLOW + Joiner.on(", ").skipNulls().join(names));
		} else {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard alts <gracz>");
		}
	}

	private void handleMaintenance(CommandSender sender, String[] args) {
		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("on")) {
				if (Config.getBoolean("maintenance.enabled", false)) {
					sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Serwer jest już w trybie konserwacji!");
				} else {
					Config.set("maintenance.enabled", true);
					Config.save();
					if (Config.getBoolean("maintenance.kick-all", true)) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (!p.hasPermission("qc.joinguard.bypass.maintenance") && !p.isOp()) {
								p.kickPlayer(Colors.color(Config.getString("maintenance.message")));
							}
						}
					}
					sendMessage(sender, QC_JoinGuard.prefix + Colors.GREEN + " Włączono tryb konserwacji!");
				}
			} else if (args[1].equalsIgnoreCase("off")) {
				if (Config.getBoolean("maintenance.enabled", false)) {
					Config.set("maintenance.enabled", false);
					Config.save();
					sendMessage(sender, QC_JoinGuard.prefix + Colors.GREEN + " Wyłączono tryb konserwacji!");
				} else {
					sendMessage(sender, QC_JoinGuard.prefix + Colors.RED + " Serwer nie jest w trybie konserwacji!");
				}
			} else if (args[1].equalsIgnoreCase("status")) {
				if (Config.getBoolean("maintenance.enabled", false)) {
					sendMessage(sender, QC_JoinGuard.prefix + Colors.YELLOW + " Serwer jest w trybie konserwacji!");
				} else {
					sendMessage(sender, QC_JoinGuard.prefix + Colors.YELLOW + " Serwer nie jest w trybie konserwacji!");
				}
			} else {
				sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard maintenance <on/off/status>");
			}
		} else {
			sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Użyj /joinguard maintenance <on/off/status>");
		}
	}

	private void handleVersion(CommandSender sender) {
		sendMessage(sender, QC_JoinGuard.prefix + Colors.YELLOW + " Zainstalowana wersja: " + QC_JoinGuard.plugin.getDescription().getVersion());
		Updater.checkForUpdates(sender);
	}

	private void handleUpdate(CommandSender sender) {
		Updater.downloadNewVersion(sender);
	}

	private void sendUsage(CommandSender sender) {
		sendMessage(sender, QC_JoinGuard.prefix + Colors.GOLD + " Nieprawidłowy argument!");
		sendMessage(sender, Colors.GOLD + "Użyj /joinguard <alts/list/login/maintenance/reload/report/whitelist/version/update>");
	}

	private JSONObject createReportJson(Player reported, Player reporter) {
		JSONObject reportedJson = new JSONObject();
		reportedJson.put("nick", reported.getName());
		reportedJson.put("uuid", reported.getUniqueId().toString());
		reportedJson.put("ip", Api.getPlayerIP(reported));
		JSONObject reportingJson = new JSONObject();
		reportingJson.put("nick", reporter.getName());
		JSONObject serverJson = new JSONObject();
		serverJson.put("ip", QC_JoinGuard.plugin.getServerIP());
		serverJson.put("port", Bukkit.getPort());
		JSONObject reportJson = new JSONObject();
		reportJson.put("reported", reportedJson);
		reportJson.put("reporting", reportingJson);
		reportJson.put("server", serverJson);
		return reportJson;
	}

	@SuppressWarnings("deprecation")
    private BaseComponent createClickableMessage(String messageText, String url) {
		BaseComponent message;
		if (Utils.getGeneralMCVersion() > 20) {
			message = TextComponent.fromLegacy(Colors.hex(messageText));
		} else {
			message = new TextComponent(TextComponent.fromLegacyText(Colors.hex(messageText)));
		}
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return message;
	}

	private void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(Colors.color(message));
	}

	private void noAccess(CommandSender sender) {
		sender.sendMessage(Colors.color(QC_JoinGuard.prefix + Colors.RED + " Nie masz dostępu do tej komendy!"));
	}
}
