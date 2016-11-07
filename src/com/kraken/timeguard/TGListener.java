package com.kraken.timeguard;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
      
      public HashMap<String, Date> getStartTimes() {
    	  return startTimes;
      }
      
}
