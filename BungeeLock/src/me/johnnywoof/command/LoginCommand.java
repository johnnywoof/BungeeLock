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

public class LoginCommand extends Command{

	private BungeeLock bl;
	
	public LoginCommand(BungeeLock var) {
        super("login", "bungeelock.login");
        this.bl = var;
    }
 
    @SuppressWarnings("deprecation")
	public void execute(CommandSender sender, final String[] args) {
    	
    	if(args.length <= 0){
    		
    		sender.sendMessage((ChatColor.RED + "Usage: /login <password>"));
    		return;
    		
    	}
    	
        if ((sender instanceof ProxiedPlayer)) {
        	
        	final ProxiedPlayer p = (ProxiedPlayer)sender;
              
        	if(!bl.vars.isLoggedIn(bl.vars.getUUID(p))){
        		
        		if(bl.vars.db.getTypeID() == 1){
        			
        			p.sendMessage((Settings.verifymes));
        			
        		}
        		
    			bl.getProxy().getScheduler().runAsync(this.bl, new Runnable(){

					@Override
					public void run() {
						
						UUID uuid = bl.vars.getUUID(p);
						
						String enp = bl.vars.db.getPassword(uuid);
						
						try {
							
							if(Utils.passwordMatches(enp, args[0])){
								
								bl.vars.setLoginStatus(uuid, true);
								p.sendMessage((Settings.loginmes));
								
								if(bl.vars.getWasOn(uuid) != null){
									
			        				p.connect(bl.getProxy().getServerInfo(bl.vars.getWasOn(uuid)));
			        				
			        				bl.vars.removeWasOn(uuid);
			        				
			        			}
								
								bl.vars.setHasAccountCache(uuid, false);//Garbage collector
								
							}else{
								
								bl.getLogger().info(p.getName() + " [" + p.getAddress().toString() + "] entered an incorrect password, which was \"" + args[0] + "\".");
								
								p.sendMessage((Settings.incorrectpass));
								
							}
							
						} catch (InvalidKeySpecException e) {
							p.sendMessage((Settings.accounterror));
							e.printStackTrace();
						}catch (NoSuchAlgorithmException e){
							
							p.sendMessage((Settings.accounterror));
							e.printStackTrace();
							
						}
						
					}
    				
    			});
        		
        	}else{
        		
        		p.sendMessage((Settings.alreadyloggedin));
        		
        	}
        	  
        }else{
        	
        	sender.sendMessage(("This command does not have support for the console."));
        	
        }
    }
	
}
