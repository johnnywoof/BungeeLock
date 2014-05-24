package me.johnnywoof;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import me.johnnywoof.command.ChangePasswordCommand;
import me.johnnywoof.command.LoginCommand;
import me.johnnywoof.command.RegisterCommand;
import me.johnnywoof.command.ReloadCommand;
import me.johnnywoof.database.FlatFile;
import me.johnnywoof.database.MySql;
import me.johnnywoof.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLock extends Plugin{

	public Variables vars;
	
	@Override
    public void onEnable(){
		
		this.getProxy().getPluginManager().registerListener(this, new BungeeLockListener(this));
		
		this.reloadConfig();
		
		this.getProxy().getPluginManager().registerCommand(this, new LoginCommand(this));
		this.getProxy().getPluginManager().registerCommand(this, new RegisterCommand(this));
		this.getProxy().getPluginManager().registerCommand(this, new ChangePasswordCommand(this));
		this.getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));
		
	}
	
	@Override
    public void onDisable() {
		
		
		
	}
	
	public boolean reloadConfig(){
		
		if(this.vars == null){
			this.vars = new Variables();
		}
		
		if(!this.getDataFolder().exists()){
			
			this.getDataFolder().mkdir();
			
		}
		
		File config = new File(this.getDataFolder() + File.separator + "config.txt");
		
		try{
		
			if(!config.exists()){
				
				config.createNewFile();
				
				Utils.copyFile(this.getResourceAsStream("config.txt"), new FileOutputStream(config));
				
			}

			BufferedReader br = new BufferedReader(new FileReader(config));
			
			String l = null;
			
			while((l = br.readLine()) != null){
				
				if(!l.startsWith("#")){
					
					if(l.contains(":")){
					
						String[] s = l.split(":");
					
						if(s.length >= 2){
							
							if(s[0].equalsIgnoreCase("auth-server-name")){
								
								Settings.authservername = s[1];
								
							}else if(s[0].equalsIgnoreCase("safe-mode")){
								
								Settings.safemode = Boolean.parseBoolean(s[1].replaceAll(" ", ""));
								
							}else if(s[0].equalsIgnoreCase("require-password-on-logout")){
								
								Settings.logoutpassword = Boolean.parseBoolean(s[1].replaceAll(" ", ""));
								
							}else if(s[0].equalsIgnoreCase("time-out")){
								
								Settings.kicktimeout = Integer.parseInt(s[1].replaceAll(" ", ""));
								
							}else if(s[0].equalsIgnoreCase("password-no-space")){
								
								Settings.passspace = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("login-message")){
								
								Settings.loginmes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("account-error")){
								
								Settings.accounterror = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("already-logged-in")){
								
								Settings.alreadyloggedin = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("mysql-message")){
								
								Settings.verifymes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("incorrect-message")){
								
								Settings.incorrectpass = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("password-changed")){
								
								Settings.passchanged = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("change-incorrect")){
								
								Settings.opasschange = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("password-no-match")){
								
								Settings.passmatch = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("not-logged-in-message")){
									
								Settings.notloggedinmes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("register-message")){
								
								Settings.regmes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("database-type")){
								
								if(s[1].replaceAll(" ", "").equalsIgnoreCase("mysql")){
									
									this.vars.db = new MySql();
									this.vars.db.check(this);
									
									this.getLogger().info("Using mysql as the database");
									
								}else{
									
									this.vars.db = new FlatFile();
									this.vars.db.check(this);
									
									this.getLogger().info("Using flatfile as the database");
									
								}
								
							}else if(s[0].equalsIgnoreCase("login-timeout")){
								
								Settings.logintimeout = Integer.parseInt(s[1].replaceAll(" ", ""));
								
							}else if(s[0].equalsIgnoreCase("post-uuids")){
									
								Settings.offlinemode = Boolean.parseBoolean(s[1].replaceAll(" ", ""));
								
							}else if(s[0].equalsIgnoreCase("not-registered-message")){
								
								Settings.notregisteredmes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("deny-login-message")){
									
								Settings.denyloginmes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("deny-login-message")){
									
								Settings.denyloginmes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("kicked-no-login")){
									
								Settings.nologinmes = this.getSentence(s[1]);
								
							}else if(s[0].equalsIgnoreCase("database-host")){
								
								Settings.dbhost = s[1];
								
							}else if(s[0].equalsIgnoreCase("database-port")){
								
								Settings.dbport = Integer.parseInt(s[1]);
								
							}else if(s[0].equalsIgnoreCase("database-name")){
								
								Settings.dbname = s[1];
								
							}else if(s[0].equalsIgnoreCase("database-username")){
								
								Settings.dbusername = s[1];
								
							}else if(s[0].equalsIgnoreCase("database-password")){
								
								Settings.dbpass = s[1];
								
							}else if(s[0].equalsIgnoreCase("database-prefix")){
								
								Settings.dbprefix = s[1];
								
							}else if(s[0].equalsIgnoreCase("version")){
								
								if(Integer.parseInt(s[1]) != 1){
									
									this.getLogger().severe("Your config version in bungeelock is out of date! Please consider deleting it for the new version.");
									
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
			br.close();
		
		}catch(IOException e){
			
			this.getLogger().severe("Failed to load config file! Plugin may not work correctly.");
			
			e.printStackTrace();
			
			return false;
			
		}
		
		return true;
		
	}
	
	//Long story how this got here
	private String getSentence(String l){
		
		return l.replaceAll("&", ChatColor.COLOR_CHAR + "");
		
	}
	
}
