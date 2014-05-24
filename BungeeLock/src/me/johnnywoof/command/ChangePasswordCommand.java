package me.johnnywoof.command;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import me.johnnywoof.BungeeLock;
import me.johnnywoof.Settings;
import me.johnnywoof.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChangePasswordCommand extends Command{

	private final BungeeLock bl;
	
	public ChangePasswordCommand(BungeeLock bl) {
        super("changepassword", "bungeelock.change");
        this.bl = bl;
    }
 
    @SuppressWarnings("deprecation")
	public void execute(CommandSender sender, final String[] args) {
    	
    	if(args.length <= 2){
    		
    		sender.sendMessage((ChatColor.RED + "Usage: /changepassword <old password> <new password> <confirm new password>"));
    		return;
    		
    	}else if(args.length > 3){
    		
    		sender.sendMessage((Settings.passspace));
    		return;
    		
    	}
    	
        if ((sender instanceof ProxiedPlayer)) {
        	
        	final ProxiedPlayer p = (ProxiedPlayer)sender;
              
    		if(!args[1].equals(args[2])){
    			
    			sender.sendMessage((Settings.passmatch));
        		return;
    			
    		}
    		
    		if(bl.vars.db.getTypeID() == 1){
    			
    			p.sendMessage((Settings.verifymes));
    			
    		}
        	
        	bl.getProxy().getScheduler().runAsync(this.bl, new Runnable(){
        	
					@Override
					public void run() {
						
						if(bl.vars.hasAccount(bl.vars.getUUID(p)) && bl.vars.isLoggedIn(bl.vars.getUUID(p))){
						
			        		String enp = null;
			        		
			        		try {
								enp = Utils.getEncrypted(args[1]);
							} catch (InvalidKeySpecException e) {
								e.printStackTrace();
								enp = null;
							}catch(NoSuchAlgorithmException e){
								
								e.printStackTrace();
								enp = null;
								
							}
			        		
			        		if(enp == null){
			        			
			        			p.sendMessage((Settings.accounterror));
			        			return;
			        			
			        		}
							
							try {
								
								if(Utils.passwordMatches(bl.vars.db.getPassword(bl.vars.getUUID(p)), args[0])){
									
									bl.vars.db.setPassword(bl.vars.getUUID(p), enp);
									
									p.sendMessage((Settings.passchanged));
									
								}else{
									
									p.sendMessage((Settings.opasschange));
									return;
									
								}
								
							} catch (InvalidKeySpecException e) {
								p.sendMessage((Settings.accounterror));
			        			
								e.printStackTrace();
								
								return;
							}catch(NoSuchAlgorithmException e){
								
								p.sendMessage((Settings.accounterror));
			        			
								e.printStackTrace();
								
								return;
								
							}
	        			
						}else{
			        		
			        		p.sendMessage((Settings.alreadyloggedin));//If a player ever makes this fire I'll pay you $5 xD
			        		
			        	}
						
					}
        			
        		});
        	  
        }else{
        	
        	sender.sendMessage(("This command does not have support for the console."));
        	
        }
    }
	
}
