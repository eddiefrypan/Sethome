package com.eddiefrypan.sethome;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Main plugin;
	private HashMap<String, Location> homes;

	@Override
	public void onEnable() {
		plugin = this;
		homes = new HashMap<String, Location>();
		loadConfig();
		getLogger().info(
				getDescription().getName() + " version "
						+ getDescription().getVersion() + " has been enabled");
	}

	@Override
	public void onDisable() {
		getLogger().info(getDescription().getName() + " has been disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String name = player.getName();
			Location location = player.getLocation();
			if (label.equalsIgnoreCase("sethome")) {
				homes.put(name, location);
				player.sendMessage(ChatColor.GREEN + "Home set!");

				String sWorld = location.getWorld().getName();
				String sX = "" + location.getX();
				String sY = "" + location.getY();
				String sZ = "" + location.getZ();
				String sPitch = "" + location.getPitch();
				String sYaw = "" + location.getYaw();

				String sLocation = name + "," + sWorld + "," + sX + "," + sY
						+ "," + sZ + "," + sPitch + "," + sYaw + ";";

				String raw = getConfig().getString("homes");
				String newHomes;
				if (raw == null || raw == "")
					newHomes = sLocation;
				else {
					if (raw.contains(name)) {
						int playerIndex = raw.indexOf(name);
						int dashIndex = raw.indexOf(";", playerIndex);
						newHomes = raw.replaceFirst(
								raw.substring(playerIndex, dashIndex + 1),
								sLocation);
					} else
						newHomes = raw + sLocation;
				}

				getConfig().set("homes", newHomes);
				saveConfig();
			} else if (label.equalsIgnoreCase("home")) {
				if (homes.containsKey(name)) {
					player.teleport(homes.get(name));
				} else
					player.sendMessage(ChatColor.RED
							+ "You do not have a home. Use /sethome");
			}
		} else {
			sender.sendMessage("You must be a player to use sethome");
		}

		return true;
	}

	private void loadConfig() {
		String raw = getConfig().getString("homes");

		// eddiefrypan,world,x,y,z,pitch,yaw
		if (raw != null && raw != "") {
			String[] players = raw.split(";");

			for (int i = 0; i < players.length; i++) {
				String[] values = players[i].split(",");

				String name = values[0];

				World world = getServer().getWorld(values[1]);
				double x = Double.parseDouble(values[2]);
				double y = Double.parseDouble(values[3]);
				double z = Double.parseDouble(values[4]);
				float pitch = Float.parseFloat(values[5]);
				float yaw = Float.parseFloat(values[6]);

				Location location = new Location(world, x, y, z, yaw, pitch);

				homes.put(name, location);
			}
		}
	}
}
