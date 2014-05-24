package me.johnnywoof.command;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import me.johnnywoof.BungeeLock;
import me.johnnywoof.Settings;
import me.johnnywoof.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RegisterCommand extends Command{

	private final BungeeLock bl;
	
	public RegisterCommand(BungeeLock bl) {
        super("register", "bungeelock.register");
        this.bl = bl;
    }
 
    @SuppressWarnings("deprecation")
	public void execute(CommandSender sender, final String[] args) {
    	
    	if(args.length <= 1){
    		
    		sender.sendMessage((ChatColor.RED + "Usage: /register <password> <confirm password>"));
    		return;
    		
    	}else if(args.length > 2){
    		
    		sender.sendMessage((Settings.passspace));
    		return;
    		
    	}
    	
        if ((sender instanceof ProxiedPlayer)) {
        	
        	final ProxiedPlayer p = (ProxiedPlayer)sender;
              
    		if(!args[0].equals(args[1])){
    			
    			sender.sendMessage((Settings.passmatch));
        		return;
    			
    		}
    		
    		if(bl.vars.db.getTypeID() == 1){
    			
    			p.sendMessage((Settings.verifymes));
    			
    		}
        	
        	bl.getProxy().getScheduler().runAsync(this.bl, new Runnable(){
        	
					@Override
					public void run() {
						
						if(!bl.vars.hasAccount(bl.vars.getUUID(p))){
						
							UUID uuid = bl.vars.getUUID(p);
							
			        		String enp = null;
			        		
			        		try {
								enp = Utils.getEncrypted(args[0]);
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
								enp = null;
							}catch (InvalidKeySpecException e){
								
								e.printStackTrace();
								enp = null;
								
							}
			        		
			        		if(enp == null){
			        			
			        			p.sendMessage((Settings.accounterror));
			        			return;
			        			
			        		}
							
							bl.vars.db.setPassword(uuid, enp);
							
							bl.vars.setLoginStatus(uuid, true);
		        			
		        			p.sendMessage((Settings.regmes));
		        			
		        			//Garbage collector
		        			bl.vars.setHasAccountCache(uuid, false);
		        			bl.vars.setNeedsRegistration(uuid, false);
	        			
						}else{
			        		
			        		p.sendMessage((Settings.alreadyloggedin));
			        		
			        	}
						
					}
        			
        		});
        	  
        }else{
        	
        	sender.sendMessage(("This command does not have support for the console."));
        	
        }
    }
	
}
