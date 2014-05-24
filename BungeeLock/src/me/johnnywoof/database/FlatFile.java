package me.johnnywoof.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

import me.johnnywoof.BungeeLock;

public class FlatFile implements Database{

	private File passwords = null;
	
	@Override
	public String getPassword(UUID uuid) {
		
		String password = null;
		
		try{
		
			BufferedReader br = new BufferedReader(new FileReader(this.passwords));
		
			String l = null;
			
			while((l = br.readLine()) != null){
				
				if(l.startsWith(uuid.toString() + ":")){
					
					password = l.substring(l.indexOf(":") + 1, l.length());
					
					break;
					
				}
				
			}
			
			br.close();
			
		}catch(IOException e){
			
			e.printStackTrace();
			
			password = null;
			
		}
		
		if(password != null){
		
			if(password.isEmpty()){
				
				password = null;
				
			}
		
		}
		
		return password;
		
	}

	@Override
	public void setPassword(UUID uuid, String epass) {
		
		ArrayList<String> lines = new ArrayList<String>();
		
		try{
			
			BufferedReader br = new BufferedReader(new FileReader(this.passwords));
		
			String l = null;
			
			while((l = br.readLine()) != null){
					
				UUID cid = UUID.fromString(l.substring(0, l.indexOf(":")));
					
				if(cid != uuid){
						
					lines.add(l);
						
				}
				
				break;
				
			}
			
			br.close();
			
			if(this.passwords.exists()){
				this.passwords.delete();
			}
			
			this.passwords.createNewFile();
			
			PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(this.passwords, true)));
			
			for(String line : lines){
				
				w.println(line);
				
			}
			
			w.println(uuid.toString() + ":" + epass);
			
			w.close();
			
		}catch(IOException e){
			
			//Somewhat tries to make a backup
			System.out.println("WARNING! Failed to manage passwords, dumping file contents in log");
			
			for(String l : lines){
				
				System.out.println(l);
				
			}
			
			e.printStackTrace();
			
		}
		
		lines.clear();//Free up resources
		
	}

	@Override
	public void check(BungeeLock bl) {
		
		this.passwords = new File(bl.getDataFolder() + File.separator + "accounts.txt");
		
		if(!this.passwords.exists()){
			
			try {
				
				this.passwords.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	@Override
	public int getTypeID() {
		return 0;
	}

}
