package nl.rgjkuiper.soe;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SaveOnEmpty extends JavaPlugin{
	public static SaveOnEmpty instance;
	public final static Logger log = Logger.getLogger("Minecraft");
	public int timeout = 60;
	public boolean running = false;
	public int ID;
	private int bukkitID = 73869;
	
	
	
	public void onEnable(){
		SaveOnEmpty.instance = this;
		
		new Updater(this, this.bukkitID, this.getFile(), Updater.UpdateType.DEFAULT, false);
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {  }
		
		this.saveDefaultConfig();
		this.timeout = this.getConfig().getInt("timeout");
		
		this.getServer().getPluginManager().registerEvents(new logoutListener(), this);
		
		this.log("Enabled SaveOnEmpty", Level.INFO);
	}
	
	public void onDisable(){
		this.log("Disabled SaveOnEmpty", Level.INFO);
	}
	
	public void log(String s, Level l){
		SaveOnEmpty.log.log(l, "[SaveOnEmpty] " + s);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!cmd.getName().equalsIgnoreCase("soe"))
			return false;
		
		if(args.length == 0){
			this.showHelp(sender); return true;
		}else if(args[0].equalsIgnoreCase("reload")){
			if(sender.hasPermission("saveonempty.admin")){
				this.reloadSOE();
				sender.sendMessage(ChatColor.AQUA + "SaveOnEmpty config reloaded!");
				
				return true;
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "No permission!");
				
				return false;
			}
		}else if(args[0].equalsIgnoreCase("timeout")){
			if(sender.hasPermission("saveonempty.admin")){
				if(args.length == 2 && isNumeric(args[1])){
					int sec = Integer.parseInt(args[1]);
					int oldsec = SaveOnEmpty.instance.timeout;
					this.getConfig().set("timeout", sec);
					saveConfig();
					this.reloadSOE();
					sender.sendMessage(ChatColor.AQUA + "Timeout time changed from " + oldsec + " seconds to " + sec + " seconds.");
					return true;
				}else{
					sender.sendMessage(ChatColor.RED + "Please fill in an timout amount in seconds.");
					sender.sendMessage(ChatColor.AQUA + "/soe timeout 60");
					return false;	
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "No permission!");
				return false;
			}
		}else if(args[0].equalsIgnoreCase("save-now")){
			if(sender.hasPermission("saveonempty.save") || sender.hasPermission("saveonempty.admin")){
				SaveOnEmpty.instance.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");
				sender.sendMessage(ChatColor.AQUA + "Saved the world!");
				
				return true;
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "No permission!");
				return false;
			}
		}else{
			sender.sendMessage(ChatColor.DARK_RED + "This command does not exist!");
			return false;
		}
	}
	
	private void reloadSOE(){
		reloadConfig();
		this.timeout = this.getConfig().getInt("timeout");
	}
	
	private void showHelp(CommandSender sender){
		if(sender.hasPermission("saveonempty.save") || sender.hasPermission("saveonempty.admin")){
			sender.sendMessage("----" + ChatColor.GREEN + "SaveOnEmpty Help" + ChatColor.WHITE + "----");
			sender.sendMessage(ChatColor.AQUA + "/soe" + ChatColor.WHITE + " | " + ChatColor.GREEN + "Shows this info.");
			sender.sendMessage(ChatColor.AQUA + "/soe save-now" + ChatColor.WHITE + " | " + ChatColor.GREEN + "Save the world manually");
		}else{
			sender.sendMessage(ChatColor.DARK_RED + "No permission!");
			return;
		}
		
		if(sender.hasPermission("saveonempty.admin")){
			sender.sendMessage(ChatColor.AQUA + "/soe timeout" + ChatColor.WHITE + " | " + ChatColor.GREEN + "Set the timeout in seconds");
			sender.sendMessage(ChatColor.AQUA + "/soe reload" + ChatColor.WHITE + " | " + ChatColor.GREEN + "Reloads the config");
		}
		return;
	}
	
	private static boolean isNumeric(String str){  
	  try{  
	    Double.parseDouble(str);  
	  }catch(NumberFormatException nfe){  
	    return false;  
	  }  
	  return true;  
	}
}