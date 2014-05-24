package me.johnnywoof;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.johnnywoof.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeLockListener implements Listener {

	private final BungeeLock bl;
	
	public BungeeLockListener(BungeeLock bl){
		
		this.bl = bl;
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(PreLoginEvent event){//This event is async (I hope, checked the javadocs)
		
		int time = bl.vars.isDeniedLogin(event.getConnection().getAddress().getAddress().getHostAddress());
		
		if(time > 0){
			
			event.setCancelReason(Settings.denyloginmes.replaceAll(".time.", String.valueOf(time)));
			
			event.setCancelled(true);
			
			return;
			
		}
		
		UUID uuid = null;
		
		if(!bl.vars.hasUUIDInCache(event.getConnection().getName())){
				
			try {
				uuid = Utils.getUUID(event.getConnection().getName());
			} catch (IOException e) {
				uuid = null;
				e.printStackTrace();
			}
		
		}else{
			
			uuid = bl.vars.getUUIDFromCache(event.getConnection().getName());
			
		}
		
		if(Settings.offlinemode){

			if(uuid == null){
				
				bl.getLogger().info("Disconnected " + event.getConnection().getName() + " [" + event.getConnection().getAddress().getAddress().toString() + "] because of failure to get uuid.");
				
				event.setCancelReason("Could not get your uuid! If persits please contact an admin.");
				
				event.setCancelled(true);
				
				return;
				
			}else{
				
				//No need to store it in the cache if in online mode
				bl.vars.setUUIDinCache(event.getConnection().getName(), uuid);
				
			}
			
		}
		
		if(uuid != null){
		
			bl.vars.setHasAccountCache(uuid, bl.vars.hasAccount(uuid));
		
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
		
		if(event.getSender() instanceof ProxiedPlayer){
			
    		ProxiedPlayer p = (ProxiedPlayer) event.getSender();
    		
			if(!bl.vars.isLoggedIn(bl.vars.getUUID(p))){
			
				String mes = event.getMessage().toLowerCase();
				
				boolean con = false;
				
				if(bl.vars.needsRegistration(bl.vars.getUUID(p))){
					
					con = !mes.equalsIgnoreCase("/register") && !mes.startsWith("/register ");
					
				}else{
					
					con = !mes.equalsIgnoreCase("/login") && !mes.startsWith("/login ");
					
				}
				
				if(con){
					
					p.sendMessage(((bl.vars.needsRegistration(bl.vars.getUUID(p)) ? Settings.notregisteredmes : Settings.notloggedinmes)));
						
					event.setCancelled(true);
						
				}
				
			}
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerConnect(ServerConnectEvent event){
		
		if(!event.getTarget().getName().equalsIgnoreCase(Settings.authservername)){
			
			if(!bl.vars.isLoggedIn(bl.vars.getUUID(event.getPlayer()))){
				
				event.getPlayer().sendMessage(((bl.vars.needsRegistration(bl.vars.getUUID(event.getPlayer())) ? Settings.notregisteredmes : Settings.notloggedinmes)));
				 
				event.setCancelled(true);
				
			}
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PostLoginEvent event){
		
		ServerInfo si = bl.getProxy().getServerInfo(Settings.authservername);
		
		if(si == null){
		
			bl.getLogger().warning("[BungeeLock] The auth server \"" + Settings.authservername + "\" does not exist!");
			
			if(Settings.safemode){
				
				Utils.kickWithDelay(this.bl, event.getPlayer(), 1000, ChatColor.RED + "There was an error when logging you in! Please contact an administrator!");
				
			}
		
		}else{
			
			if(event.getPlayer().hasPermission("bungeelock.register.force") || bl.vars.hasAccountCache(bl.vars.getUUID(event.getPlayer()))){
				
				String ln = Utils.getLastServer(this.bl, bl.vars.getUUID(event.getPlayer()));
				
				if(ln != null){
					
					if(bl.getProxy().getServerInfo(ln) != null){
					
						bl.vars.setWasOn(bl.vars.getUUID(event.getPlayer()), ln);
					
					}
					
				}
				
				event.getPlayer().setReconnectServer(si);
				
				final ProxiedPlayer p = event.getPlayer();
				
				if(!bl.vars.hasAccountCache(bl.vars.getUUID(p))){
				
					bl.vars.setNeedsRegistration(bl.vars.getUUID(p), true);
				
				}
				
				bl.getProxy().getScheduler().schedule(this.bl, new Runnable(){

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						
						if(!bl.vars.needsRegistration(bl.vars.getUUID(p))){
							
							p.sendMessage((Settings.notloggedinmes));
						
						}else{
							
							p.sendMessage((Settings.notregisteredmes));
							
						}
						
					}
					
				}, 6, TimeUnit.SECONDS);
				
				if(Settings.kicktimeout > -1){
					
					bl.getProxy().getScheduler().schedule(this.bl, new Runnable(){

						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							
							if(!bl.vars.isLoggedIn(bl.vars.getUUID(p))){
							
								p.disconnect((Settings.nologinmes));
							
							}
							
						}
						
					}, Settings.kicktimeout, TimeUnit.SECONDS);
					
				}
				
			}else{
				
				bl.vars.setLoginStatus(bl.vars.getUUID(event.getPlayer()), true);
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent event){
		
		UUID uuid = bl.vars.getUUID(event.getPlayer());
		
		if(!bl.vars.isLoggedIn(uuid)){
			
			if(Settings.logintimeout > 0){
				
				bl.vars.setDeniedLogin(event.getPlayer().getAddress().getAddress().getHostAddress(), (System.currentTimeMillis() + (Settings.logintimeout * 1000)));
				
			}
			
		}
		
		if(!event.getPlayer().getServer().getInfo().getName().equals(Settings.authservername)){
		
			Utils.setLastServer(this.bl, event.getPlayer().getServer().getInfo().getName(), uuid);
		
		}
		
		if(Settings.logoutpassword){
			
			bl.vars.setLoginStatus(uuid, false);
			//Garbage collector
			bl.vars.setHasAccountCache(uuid, false);
			bl.vars.setNeedsRegistration(uuid, false);
			
		}
		
	}
	
}
