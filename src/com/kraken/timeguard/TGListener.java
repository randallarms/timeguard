package com.kraken.timeguard;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TGListener implements Listener {
	
	public static TimeGuard plugin;
	TimeGuard configGetter;
	
	HashMap<String, Date> startTimes = new HashMap<>();
	
      public TGListener(TimeGuard plugin) {
    	  
    	  plugin.getServer().getPluginManager().registerEvents(this, plugin);
    	  configGetter = plugin;
    	  
      }
      
      @EventHandler
      public void onPlayerJoin(PlayerJoinEvent e) {
	  		
	  		Player player = e.getPlayer();
	  		String UUIDString = player.getUniqueId().toString();
	  		Date startTime = new Date();

	  		startTimes.put(UUIDString, startTime);
	  		
	  		if ( !player.hasPlayedBefore() ) {
	  			configGetter.getConfig().set(UUIDString + ".name", player.getName());
	  			configGetter.getConfig().set(UUIDString + ".exempt", false);
		  		configGetter.saveConfig();
	  		}
    	  
      }
      
      @EventHandler
      public void onPlayerQuit(PlayerQuitEvent e) {
    	  
	  		Player player = e.getPlayer();
	  		String UUIDString = player.getUniqueId().toString();
	  		
	  		SavePlayedTime saver = new SavePlayedTime(configGetter, startTimes, configGetter.getSavedTimes());
	  		saver.saveTask();
		  		
		  	startTimes.remove(UUIDString);
	  		
      }
      
      @EventHandler
      public void onPlayerTeleport(PlayerTeleportEvent e) {
    	  
    	  Player player = e.getPlayer();
	  	  String UUIDString = player.getUniqueId().toString();
	  	  
		  	String worldName = e.getTo().getWorld().getName();
			World world = e.getTo().getWorld();
			
			if (world != null) {
				
				int requiredMins = configGetter.getConfig().getInt(worldName + ".timeguard");
	    		int playedTime = configGetter.getConfig().getInt(UUIDString + ".playedTime");
	    		boolean playerIsVet = configGetter.getConfig().getBoolean(UUIDString + ".exempt");
	    		
	    		if ( requiredMins == 0 || playedTime >= requiredMins || playerIsVet ) {
	        		
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
	        		
	        	} else {
	        		
	        		e.setCancelled(true);
	        		player.sendMessage(ChatColor.RED + "You were denied access to \"" + worldName + "\".");
	        		
	        	}
			
			}
    	  
      }
      
      public HashMap<String, Date> getStartTimes() {
    	  return startTimes;
      }
      
}
