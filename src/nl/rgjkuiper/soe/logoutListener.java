package nl.rgjkuiper.soe;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class logoutListener implements Listener {
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		
		if(SaveOnEmpty.instance.running)
			return;
		
		SaveOnEmpty.instance.running = true;
					
		SaveOnEmpty.instance.log("Player logged out, checking online players...", Level.INFO);
		if(SaveOnEmpty.instance.getServer().getOnlinePlayers().length == 1){
			SaveOnEmpty.instance.log("No more players online, waiting " + SaveOnEmpty.instance.timeout + " seconds before proceeding...", Level.INFO);
			
			SaveOnEmpty.instance.ID = SaveOnEmpty.instance.getServer().getScheduler().scheduleSyncDelayedTask(SaveOnEmpty.instance, new Runnable() {
				
				public void run(){
					// Check is vrijwel overbodig nu
					// Kijken of het slim is om weg te halen
					if(SaveOnEmpty.instance.getServer().getOnlinePlayers().length == 0){
						SaveOnEmpty.instance.log("No players online, saving world...", Level.INFO);
						SaveOnEmpty.instance.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");
						SaveOnEmpty.instance.log("Saved the world!", Level.INFO);
						SaveOnEmpty.instance.running = false;
					}else{
						SaveOnEmpty.instance.log("New player(s) came online in the waiting period... Aborting...", Level.INFO);
						SaveOnEmpty.instance.running = false;
					}
				}
				
			}, SaveOnEmpty.instance.timeout*20L);
			
			//SaveOnEmpty.instance.map.put("quit", ID);
		
		}else{
			SaveOnEmpty.instance.log("There are other players online, aborting...", Level.INFO);
		}
    }
	
	@EventHandler
    public static void onLogin(PlayerLoginEvent event){
		//if(SaveOnEmpty.instance.map.containsKey("quit")){
		if(SaveOnEmpty.instance.running){
			//SaveOnEmpty.instance.getServer().getScheduler().cancelTask(SaveOnEmpty.instance.map.get("quit"));
			SaveOnEmpty.instance.getServer().getScheduler().cancelTask(SaveOnEmpty.instance.ID);
			SaveOnEmpty.instance.log("New player(s) came online in the waiting period... Aborting...", Level.INFO);
			SaveOnEmpty.instance.running = false;
		}
	}
}
