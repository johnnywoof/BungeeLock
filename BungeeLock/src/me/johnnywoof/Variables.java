package me.johnnywoof;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import me.johnnywoof.database.Database;
import me.johnnywoof.util.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Variables {

	private ArrayList<UUID> loggedIn = new ArrayList<UUID>();
	private HashMap<String, UUID> cache = new HashMap<String, UUID>();
	private HashMap<UUID, Boolean> account = new HashMap<UUID, Boolean>();
	private HashMap<UUID, String> wason = new HashMap<UUID, String>();
	private HashMap<String, Long> denylogin = new HashMap<String, Long>();
	private ArrayList<UUID> needsregister = new ArrayList<UUID>();
	public Database db = null;
	
	public boolean hasAccountCache(UUID uuid){
		
		if(this.account.containsKey(uuid)){
			
			return this.account.get(uuid);
			
		}else{
			
			return false;
			
		}
		
	}
	
	public void removeWasOn(UUID uuid){
		this.wason.remove(uuid);
	}
	
	public String getWasOn(UUID uuid){
		
		if(this.wason.containsKey(uuid)){
			
			return this.wason.get(uuid);
			
		}else{
			
			return null;
			
		}
		
	}
	
	public void setWasOn(UUID uuid, String name){
		this.wason.put(uuid, name);
	}
	
	/**Returns amount of seconds*/
	public int isDeniedLogin(String ip){
		
		if(this.denylogin.containsKey(ip)){
			
			if(this.denylogin.get(ip) < System.currentTimeMillis()){
				
				this.denylogin.remove(ip);
				
				return -1;
				
			}else{
				
				return Math.round(((this.denylogin.get(ip) - System.currentTimeMillis()) / 1000));
				
			}
			
		}
		
		return -1;
		
	}
	
	public void setDeniedLogin(String ip, Long v){
		this.denylogin.put(ip, v);
	}
	
	public void setHasAccountCache(UUID uuid, boolean v){
		
		this.account.put(uuid, v);
		
	}
	
	public void setLoginStatus(UUID uuid, boolean v){
		
		for(int i = 0; i < this.loggedIn.size(); i++){
			
			if(this.loggedIn.get(i).equals(uuid)){
				
				this.loggedIn.remove(i);
				
			}
			
		}
		
		if(v){
			
			this.loggedIn.add(uuid);
			
		}
		
	}
	
	public void setNeedsRegistration(UUID uuid, boolean v){
		
		for(int i = 0; i < this.needsregister.size(); i++){
			
			if(this.needsregister.get(i) == uuid){
				
				this.needsregister.remove(i);
				
			}
			
		}
		
		if(v){
			
			this.needsregister.add(uuid);
			
		}
		
	}
	
	public boolean needsRegistration(UUID uuid){
		
		return this.needsregister.contains(uuid);
		
	}
	
	public void setUUIDinCache(String name, UUID uuid){
		this.cache.put(name, uuid);
	}
	
	public UUID getUUIDFromCache(String name){
		
		if(this.cache.containsKey(name)){
			
			return this.cache.get(name);
			
		}else{
			
			return null;
			
		}
		
	}
	
	public boolean updateUUID(String name){
		
		try {
			
			UUID uid = Utils.getUUID(name);
			
			if(uid != null){
			
				this.cache.put(name, uid);
			
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	public boolean hasUUIDInCache(String name){
		
		return this.cache.containsKey(name);
		
	}
	
	public boolean hasAccount(UUID uuid){
		
		return (this.db.getPassword(uuid) != null);
		
	}
	
	public boolean isLoggedIn(UUID uuid){
		
		return this.loggedIn.contains(uuid);
		
	}
	
	public UUID getUUID(ProxiedPlayer p){
		
		if(Settings.offlinemode){
			
			return this.getUUIDFromCache(p.getName());
			
		}else{
			
			if(p.getUniqueId() == null){
				
				//Attempt to use the cache
				ProxyServer.getInstance().getLogger().warning("UUID for player " + p.getName() + " is null! To resolve this please set post-uuids to true in the config file.");
				return this.getUUIDFromCache(p.getName());
				
			}else{
			
				return p.getUniqueId();
			
			}
			
		}
		
	}
	
}
