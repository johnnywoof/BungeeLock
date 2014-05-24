package me.johnnywoof.database;

import java.util.UUID;

import me.johnnywoof.BungeeLock;

public interface Database {

	public String getPassword(UUID uuid);
	
	public void setPassword(UUID uuid, String epass);
	
	public void check(BungeeLock bl);
	
	/**0 = flatfile, 1 = mysql*/
	public int getTypeID();
	
}
