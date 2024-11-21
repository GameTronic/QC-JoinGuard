package me.quackduck.qcjoinguard.cmds;
// Created by QuackDuck
import me.quackduck.qcjoinguard.misc.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CmdComplete implements TabCompleter {
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		List<String> commands = new ArrayList<>();
		List<String> subcommands = new ArrayList<>();
		List<String> players = new ArrayList<>();
		if (args.length == 1) {
			commands.add("report");
			commands.add("login");
			commands.add("list");
			commands.add("reload");
			commands.add("whitelist");
			commands.add("alts");
			commands.add("maintenance");
			commands.add("version");
			commands.add("update");
			StringUtil.copyPartialMatches(args[0], commands, completions);
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("report")) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					players.add(player.getName());
				}
				StringUtil.copyPartialMatches(args[1], players, completions);
			} else if (args[0].equalsIgnoreCase("whitelist")) {
				subcommands.add("add");
				subcommands.add("list");
				subcommands.add("remove");
				StringUtil.copyPartialMatches(args[1], subcommands, completions);
			} else if (args[0].equalsIgnoreCase("alts")) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					players.add(player.getName());
				}
				StringUtil.copyPartialMatches(args[1], players, completions);
			} else if (args[0].equalsIgnoreCase("maintenance")) {
				subcommands.add("on");
				subcommands.add("off");
				subcommands.add("status");
				StringUtil.copyPartialMatches(args[1], subcommands, completions);
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("whitelist")) {
				if (args[1].equalsIgnoreCase("add")) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						players.add(player.getName());
					}
					players.removeAll(Config.getStringList("whitelist"));
					StringUtil.copyPartialMatches(args[2], players, completions);
				} else if (args[1].equalsIgnoreCase("remove")) {
                    players.addAll(Config.getStringList("whitelist"));
					StringUtil.copyPartialMatches(args[2], players, completions);
				}
			}
		}
		return completions;
	}
}
