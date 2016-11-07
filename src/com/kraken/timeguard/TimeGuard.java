// =========================================================================
// |TIMEGUARD v1.0
// | by Kraken | https://www.spigotmc.org/members/kraken_.287802/
// | code inspired maxleovince95 on SpigotMC forums -- thank you.
// |
// | Always free & open-source! If the main plugin is being sold/re-branded,
// | please let me know on the SpigotMC site, or wherever you can. Thanks!
// | Source code: https://github.com/randallarms/timeguard
// =========================================================================

package com.kraken.timeguard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class TimeGuard extends JavaPlugin {
	
	TGListener listener;
	
    @Override
    public void onEnable() {
    	
    	getLogger().info("TimeGuard has been enabled.");
        listener = new TGListener(this);
		
    }
    
    @Override
    public void onDisable() {
    	
        getLogger().info("TimeGuard has been disabled.");
        
        HashMap<String, Date> startTimes = listener.getStartTimes();
        
        for (Player player : this.getServer().getOnlinePlayers()) {
        	
        	String UUIDString = player.getUniqueId().toString();
      		Date startTime = startTimes.get(UUIDString);
      		Date currentTime = new Date();
      		
      		if (startTime != null) {
      			
    	  		int playedTime = getConfig().getInt(UUIDString + ".playedTime");
    	  		
    	  		long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime.getTime() - startTime.getTime());
    	  		
    	  		getConfig().set(UUIDString + ".playedTime", playedTime + minutes);
    	  		saveConfig();
      		
      		} 
        	
        }
                
    }
    
  //TimeGuard commands
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			return false;
		}
		Player player = (Player) sender;
		String UUIDString = player.getUniqueId().toString();
		
		if (cmd.getName().equalsIgnoreCase("played")) {
			
			int playedTime = getConfig().getInt(UUIDString + ".playedTime");
			double playedHours = (playedTime / 60);
			if ( playedHours == 0 ) {
				player.sendMessage(ChatColor.GREEN + "You have played for over " + playedTime + " minutes total.");
			} else {
				player.sendMessage(ChatColor.GREEN + "You have played for over " + playedHours + " hours total.");
			}
			return true;	
		}
		
    	//Command: goto
		if (cmd.getName().equalsIgnoreCase("goto")) {
			if ( args.length == 1 ) {
				String worldName = args[0];
				World world = Bukkit.getServer().getWorld(args[0]);

				if (world != null) {

					int requiredMins = getConfig().getInt(worldName + ".timeguard");
				int playedTime = getConfig().getInt(UUIDString + ".playedTime");

				if ( requiredMins == 0 || playedTime >= requiredMins ) {

					int y = 255;
					boolean isOnLand = false;
					while (isOnLand == false) {
						Location tele = new Location(world, 0, y, 0);
						if (tele.getBlock().getType() != Material.AIR) {
							isOnLand = true;
							tele = new Location(world, 0, y+3, 0);
							player.teleport(tele);
						} else y--;
					}

					player.sendMessage(ChatColor.GREEN + "Welcome to \"" + worldName + "\".");

					return true;

				} else {

					player.sendMessage(ChatColor.RED + "You were denied access to \"" + args[0] + "\".");

					return true;

				}

				} else {

					player.sendMessage(ChatColor.GRAY + "World name not found!");

				}

			} else {

				player.sendMessage(ChatColor.GRAY + "To go to a world, enter \"/goto <worldName>\"");

			}

        //Command: timeguard <time> <world>
        } else if (cmd.getName().equalsIgnoreCase("timeguard")) {

        	if ( player.isOp() ) {
        		
        		if ( args.length == 0 ) {
        			
        			player.sendMessage(ChatColor.GRAY + "To timeguard a world, enter \"/timeguard <hours> <worldName>\"");
        			return true;
        			
        		} else if ( args.length == 1 ) {
        			
        			World world = player.getWorld();
        			String worldName = world.getName().toString();
        			
        			try {
        				int minutes = Integer.parseInt(args[0]) * 60;
        				if ( !(minutes > 57600) ) {
	        				getConfig().set(worldName + ".timeguard",  minutes);
	        				saveConfig();
	        				player.sendMessage(ChatColor.GREEN + "You timeguarded this world to require " + args[0] + " hours of played time.");
        				}
        			} catch (NumberFormatException nfe) {
        				player.sendMessage(ChatColor.GRAY + "To timeguard a world, enter \"/timeguard <hours> <worldName>\"");
        			}
        			return true;
        			
        		} else if ( args.length == 2 ) {
        			
        			World world = Bukkit.getServer().getWorld(args[1]);
        			String worldName = world.getName().toString();
        			
        			try {
        				int minutes = Integer.parseInt(args[0]) * 60;
        				if ( !(minutes > 57600) ) {
	        				getConfig().set(worldName + ".timeguard",  minutes);
	        				saveConfig();
	        				player.sendMessage(ChatColor.GREEN + "You timeguarded " + worldName + " to require " + args[0] + " hours of played time.");
        				}
        			} catch (NumberFormatException nfe) {
        				player.sendMessage(ChatColor.GRAY + "To timeguard a world, enter \"/timeguard <hours> <worldName>\"");
        			}
        			return true;
        			
        		} else {
        			
        			player.sendMessage(ChatColor.GRAY + "To timeguard a world, enter \"/timeguard <hours> <worldName>\"");
        			return true;
        			
        		}
        		
        	}
        	
        	player.sendMessage(ChatColor.RED + "Your command was not recognized, or you have insufficient permissions.");
            return true;
        	 
        }
    	
        player.sendMessage(ChatColor.RED + "Your command was not recognized, or you have insufficient permissions.");
        return true;
        
    }
    
}
