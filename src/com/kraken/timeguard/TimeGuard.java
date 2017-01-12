// =========================================================================
// |TIMEGUARD v2.0
// | by Kraken | https://www.spigotmc.org/members/kraken_.287802/
// | code inspired maxleovince95 & Strahan @ SpigotMC forums -- thank you.
// |
// | Always free & open-source! If the main plugin is being sold/re-branded,
// | please let me know on the SpigotMC site, or wherever you can. Thanks!
// | Source code: https://github.com/randallarms/timeguard
// =========================================================================

package com.kraken.timeguard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class TimeGuard extends JavaPlugin implements Listener {
	
	TGListener listener;
	
	HashMap<String, Date> savedTimes = new HashMap<>();
	
    @Override
    public void onEnable() {
    	
    	getLogger().info("TimeGuard has been enabled.");
        listener = new TGListener(this);
        TimeGuard plugin = this;
        
		this.getServer().getScheduler().scheduleSyncRepeatingTask( this, new Runnable() {

            public void run() {

                HashMap<String, Date> startTimes = listener.getStartTimes();
                SavePlayedTime saver = new SavePlayedTime(plugin, startTimes, savedTimes);
                saver.saveTask();
                
    	  		for ( Player player : Bukkit.getOnlinePlayers() ) {
    	  			
        	  		String UUIDString = player.getUniqueId().toString();
        	  		Date savedTime = new Date();
        	  		
    	  			savedTimes.put(UUIDString, savedTime);
    	  			
    	  		}
                
            }

        }, 1200, 2420 );
		
    }
    
    @Override
    public void onDisable() {
    	
        getLogger().info("TimeGuard has been disabled.");
        
        HashMap<String, Date> startTimes = listener.getStartTimes();
        SavePlayedTime saver = new SavePlayedTime(this, startTimes, savedTimes);
        saver.saveTask();
                
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
    	
		this.getServer().getScheduler().scheduleSyncDelayedTask( this, new Runnable() {

            public void run() {

            	savedTimes.remove(e.getPlayer().getUniqueId().toString());
                
            }

        }, 300 );
    	
    }
    
    public HashMap<String, Date> getSavedTimes() {
    	
		return savedTimes;
    	
    }
    
  //TimeGuard commands
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = (Player) sender;
		Boolean isPlayer = sender instanceof Player;
		String UUIDString = player.getUniqueId().toString();
		
		if (isPlayer) {
			
			//Command: goto
			if ( cmd.getName().equalsIgnoreCase("version") ) {
			
				player.sendMessage(ChatColor.GRAY + "CURRENT: TimeGuard v1.3");
				
			}
			
			//Command: played
			else if ( cmd.getName().equalsIgnoreCase("played") ) {
				
				int playedTime = getConfig().getInt(UUIDString + ".playedTime");
				int playedHours = ( playedTime / 60 );
				int playedDays = ( playedHours / 24 );
				String playedPhrase = "";
				
				switch ( playedDays ) {
					
					case 1: 
						playedPhrase = String.valueOf(playedDays) + " day, ";
						break;
					default:
						playedPhrase = String.valueOf(playedDays) + " days, ";
						break;
						
				}
				
				switch ( playedHours - (playedDays * 24) ) {
				
					case 1: 
						playedPhrase = playedPhrase + (String.valueOf( playedHours - (playedDays * 24) ) + " hour, ");
						break;
					default:
						playedPhrase = playedPhrase + (String.valueOf( playedHours - (playedDays * 24) ) + " hours, ");
						break;
						
				}
				
				switch ( playedTime - (playedHours * 60) - (playedDays * 1440) ) {
				
					case 1:
						playedPhrase = playedPhrase + (String.valueOf(playedTime - (playedHours * 60) - (playedDays * 1440) ) + " minute");
						break;
					default:
						playedPhrase = playedPhrase + (String.valueOf(playedTime - (playedHours * 60) - (playedDays * 1440) ) + " minutes");
						break;
				
				}
				
				player.sendMessage(ChatColor.GREEN + "You have played for " + playedPhrase + " total.");
				return true;
				
			}
			
	    	//Command: goto
			else if ( cmd.getName().equalsIgnoreCase("goto") ) {
	        	
	        	switch (args.length) {
	        	
	        		// One argument
	        		case 1: 
	        			
		        		String worldName = args[0];
		        		World world = Bukkit.getServer().getWorld(args[0]);
		        		
		        		if (world != null) {
		        			
		        			int requiredMins = getConfig().getInt(worldName + ".timeguard");
		            		int playedTime = getConfig().getInt(UUIDString + ".playedTime");
		            		boolean playerIsVet = getConfig().getBoolean(UUIDString + ".exempt");
		            		
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
		    		        	
		                		player.sendMessage(ChatColor.GREEN + "Welcome to \"" + worldName + "\".");
		                		
		                		return true;
		                		
		                	} else {
		                		
		                		player.sendMessage(ChatColor.RED + "You were denied access to \"" + args[0] + "\".");
		                		
		                		return true;
		                		
		                	}
		        		
		        		}
		        	
		        	// No arguments, or more than one argument
	        		default:
	        			
	        			player.sendMessage(ChatColor.GRAY + "To go to a world, enter \"/goto <worldName>\"");
	        			break;
		        			
		        }
	            
	        //Command: timeguard <function> <world> <time>
	        } else if ( cmd.getName().equalsIgnoreCase("timeguard") ) {
				
				String targetID = "*";
	
	        	if ( player.isOp() ) {
	        		
    				World world = Bukkit.getServer().getWorld("world");
        			String worldName = "";
	        		
	        		switch (args.length) {
		        		
		        		// One argument
	        			case 1: 
			        		
		        		// Two arguments
	        			case 2: 
	        				
	        				switch (args[0]) {
	        				
	        					case "del":
		        				case "delete":
		        				case "remove":
		        					
			        				world = Bukkit.getServer().getWorld(args[1]);
				        			worldName = world.getName().toString();
				        			
			        				if ( getConfig().contains(worldName + ".timeguard") ) {
				        				getConfig().set(worldName, null);
				        				saveConfig();
				        				player.sendMessage(ChatColor.GREEN + "You deleted the timeguard on \"" + worldName + "\".");
			        				} else {
			        					player.sendMessage(ChatColor.RED + "Could not find a timeguard for \"" + worldName + "\".");
			        				}
				        			
				        			break;

				        		default:
				        			
				        			player.sendMessage(ChatColor.RED + "Your command was not recognized.");
				        			break;
				        			
				        		}
	        				
	        				break;
		        		
		        		// Three arguments
	        			case 3:
	        				
	        				switch (args[0]) {
		        			
		        				case "vet":
		        				case "veteran":
		        				case "exempt":
		        					
		        					switch (args[1]) {
		        					
		        						case "add":
		        					
		        							for (Player target : Bukkit.getServer().getOnlinePlayers()) {
		        								
		        								if ( args[2].equalsIgnoreCase(target.getName()) ) {
		        									targetID = target.getUniqueId().toString();
		        									getConfig().set(targetID + ".exempt", true);
		        									saveConfig();
		        									player.sendMessage(ChatColor.GREEN + "Player \"" + target.getName() + "\" promoted to veteran status.");
		        								}
		        								
		        							}
		        							
		        							if (targetID.equalsIgnoreCase("*")) {
	        									player.sendMessage(ChatColor.RED + "Player must be online to add veteran status.");
	        								}
		        							
				        					break;
				        					
		        						case "del":
		        						case "remove":
		        							
		        							for (Player target : Bukkit.getServer().getOnlinePlayers()) {
		        								
		        								if ( args[2].equalsIgnoreCase(target.getName()) ) {
		        									targetID = target.getUniqueId().toString();
		        									getConfig().set(targetID + ".exempt", false);
		        									saveConfig();
		        									player.sendMessage(ChatColor.GREEN + "Player \"" + target.getName() + "\" demoted from veteran status.");
		        								}
		        								
		        							}
		        							
		        							if (targetID.equalsIgnoreCase("*")) {
	        									player.sendMessage(ChatColor.RED + "Player must be online to remove veteran status.");
	        								}
		        							
				        					break;
				        					
				        				default: 
				        					
				        					player.sendMessage(ChatColor.RED + "To change veteran status, enter \"/timeguard vet <add/del> <name>\".");
				        					break;
			        					
		        					}
		        					
		        					break;
	        				
		        				case "add":
		        				
			        				world = Bukkit.getServer().getWorld(args[1]);
			        				
			        				try {
			        					worldName = world.getName().toString();
			        				} catch (NullPointerException npe) {
			        					player.sendMessage(ChatColor.RED + "World not found.");
			        					return true;
			        				}
				        			
				        			try {
				        				int minutes = Integer.parseInt(args[2]) * 60;
				        				if ( !(minutes > 576000) ) {
					        				getConfig().set(worldName + ".timeguard",  minutes);
					        				saveConfig();
					        				player.sendMessage(ChatColor.GREEN + "You timeguarded \"" + worldName + "\" to require " + args[2] + " hours of played time.");
				        				}
				        			} catch (NumberFormatException nfe) {
				        				player.sendMessage(ChatColor.RED + "Please enter an integer between 0 and 9600 hours.");
				        			}
				        			
				        			break;
				        			
				        		default:
				        			
				        			player.sendMessage(ChatColor.GRAY + "To timeguard a world, enter \"/timeguard add <worldName> <hours>\".");
				        			break;
				        			
	        				}
	        				
	        				break;
		        		
		        		// Unexpected number of arguments
		        		default:
		        				
		        			player.sendMessage(ChatColor.RED + "Your command was not recognized.");
		        			break;
		        			
	        		}
	        		
	        		return true;
	        		
	        	}
	        	
	        	player.sendMessage(ChatColor.RED + "Your command was not recognized, or you have insufficient permissions.");
	            return true;
	        	 
	        }
	    	
	        player.sendMessage(ChatColor.RED + "Your command was not recognized, or you have insufficient permissions.");
	        return true;
	    
	    } else {
	    	
	    	player.sendMessage(ChatColor.RED + "This command must be issued by a player.");
	        return true;
	    	
	    }
	
	}
    
}
