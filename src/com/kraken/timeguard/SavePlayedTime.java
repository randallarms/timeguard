package com.kraken.timeguard;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

public class SavePlayedTime {
	
	TimeGuard plugin;
	HashMap<String, Date> startTimes;
	HashMap<String, Date> savedTimes;
    
    //Constructor
    public SavePlayedTime(TimeGuard plugin, HashMap<String, Date> startTimes, HashMap<String, Date> savedTimes) {
    	
    	this.plugin = plugin;
    	this.startTimes = startTimes;
    	this.savedTimes = savedTimes;

    }
    
    public void saveTask() {
    	
        for (Player player : plugin.getServer().getOnlinePlayers()) {
        	
        	String UUIDString = player.getUniqueId().toString();
      		Date startTime = startTimes.get(UUIDString);
      		Date savedTime = savedTimes.get(UUIDString);
      		Date currentTime = new Date();
      		
      		if (startTime != null) {
      			
    	  		int playedTime = plugin.getConfig().getInt(UUIDString + ".playedTime");
    	  		long minAccrued = 0;
    	  		
    	  		if (savedTime != null) {
    	  			minAccrued = TimeUnit.MILLISECONDS.toMinutes( currentTime.getTime() - savedTime.getTime() );
    	  		} else if (startTime != null) {
    	  			minAccrued = TimeUnit.MILLISECONDS.toMinutes( currentTime.getTime() - startTime.getTime() );
    	  		}
    	  		
    	  		plugin.getConfig().set(UUIDString + ".playedTime", playedTime + minAccrued);
    	  		plugin.saveConfig();
    	  		
      		} 
    	
        }
        
    }
        
}
