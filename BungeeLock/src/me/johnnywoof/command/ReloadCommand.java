package me.johnnywoof.command;

import me.johnnywoof.BungeeLock;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command{

	private final BungeeLock bl;
	
	public ReloadCommand(BungeeLock bl) {
        super("blreload", "bungeelock.reload");
        this.bl = bl;
    }
 
	@SuppressWarnings("deprecation")
	public void execute(CommandSender sender, final String[] args) {
    	
    	bl.reloadConfig();
    	
    	sender.sendMessage(ChatColor.GREEN + "The config file has been reloaded!");
    	
    }
	
}
