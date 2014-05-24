package me.johnnywoof.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.HttpsURLConnection;

import me.johnnywoof.BungeeLock;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class Utils {

public static String getEncrypted(String text) throws NoSuchAlgorithmException, InvalidKeySpecException{
		
        String generatedSecuredPasswordHash = generateStorngPasswordHash(text);
        return generatedSecuredPasswordHash;
		
	}

public static String getLastServer(BungeeLock bl, UUID uuid){
	
	String text = null;
	
	File f = new File(bl.getDataFolder() + File.separator + "last.txt");
	
	try{
		
		if(!f.exists()){
			
			f.createNewFile();
			
		}
	
		BufferedReader br = new BufferedReader(new FileReader(f));
	
		String l = null;
		
		while((l = br.readLine()) != null){
			
			if(l.startsWith(uuid.toString() + ":")){
				
				text = l.substring(l.indexOf(":") + 1, l.length());
				
				break;
				
			}
			
		}
		
		br.close();
		
	}catch(IOException e){
		
		e.printStackTrace();
		
		text = null;
		
	}
	
	if(text != null){
	
		if(text.isEmpty()){
			
			text = null;
			
		}
	
	}
	
	return text;
	
}

public static void setLastServer(BungeeLock bl, String name, UUID uuid){
	
	ArrayList<String> lines = new ArrayList<String>();
	
	File f = new File(bl.getDataFolder() + File.separator + "last.txt");
	
	try{
		
		if(!f.exists()){
			
			f.createNewFile();
			
		}
		
		BufferedReader br = new BufferedReader(new FileReader(f));
	
		String l = null;
		
		while((l = br.readLine()) != null){
				
			UUID cid = UUID.fromString(l.substring(0, l.indexOf(":")));
				
			if(cid != uuid){
					
				lines.add(l);
					
			}
			
			break;
			
		}
		
		br.close();
		
		if(f.exists()){
			f.delete();
		}
		
		f.createNewFile();
		
		PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
		
		for(String line : lines){
			
			w.println(line);
			
		}
		
		w.println(uuid.toString() + ":" + name);
		
		w.close();
		
	}catch(IOException e){
		
		e.printStackTrace();
		
	}
	
	lines.clear();//Free up resources
	
}

public static UUID getUUID(String name) throws IOException{
	
	String url = "https://api.mojang.com/profiles/page/1";
	URL obj = new URL(url);
	HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

	con.setRequestMethod("POST");
	con.setRequestProperty("User-Agent", "Server-" + ProxyServer.getInstance().getName().replaceAll(" ", "-"));
	con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	con.setRequestProperty("Content-Type", "application/json");
	con.setUseCaches(false);
	con.setDoInput(true);
	con.setDoOutput(true);

	String urlParameters = "{\"name\":\"" + name + "\",\"agent\":\"minecraft\"}";

	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	wr.writeBytes(urlParameters);
	wr.flush();
	wr.close();

	BufferedReader in = new BufferedReader(
	        new InputStreamReader(con.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	}
	in.close();
	
	//AHA! Take that Json >:D Our own parser doesn't need external libraries!
	
	String res = response.toString().replaceAll(name, "");
	
	res = res.substring(0, (res.lastIndexOf("]") - 12));
	
	res = res.substring(res.lastIndexOf("\"") + 1, res.length());
	
	response = null;
	
	return UUID.fromString(res.substring(0, 8) + "-" + res.substring(8, 12) + "-" + res.substring(12, 16) + "-" + res.substring(16, 20) + "-" +res.substring(20, 32));
	
}

/**
 * 
 * Kicks a player after x milliseconds
 * 
 * */
public static void kickWithDelay(Plugin plug, final ProxiedPlayer p, int delay, final String message){
	
	plug.getProxy().getScheduler().schedule(plug, new Runnable(){

		@Override
		public void run() {
			
			p.disconnect(new TextComponent(message));
			
		}
		
	}, delay, TimeUnit.MILLISECONDS);
	
}
	
	public static boolean passwordMatches(String encrpted, String text) throws NoSuchAlgorithmException, InvalidKeySpecException{
		
		return validatePassword(text, encrpted);
		
	}
	
	 private static String generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
	    {
	        int iterations = 1000;
	        char[] chars = password.toCharArray();
	        byte[] salt = getSalt().getBytes();
	         
	        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        byte[] hash = skf.generateSecret(spec).getEncoded();
	        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
	    }
	     
	    private static String getSalt() throws NoSuchAlgorithmException
	    {
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        sr.nextBytes(salt);
	        return salt.toString();
	    }
	     
	    private static String toHex(byte[] array) throws NoSuchAlgorithmException
	    {
	        BigInteger bi = new BigInteger(1, array);
	        String hex = bi.toString(16);
	        int paddingLength = (array.length * 2) - hex.length();
	        if(paddingLength > 0)
	        {
	            return String.format("%0"  +paddingLength + "d", 0) + hex;
	        }else{
	            return hex;
	        }
	    }
	    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
	    {
	        String[] parts = storedPassword.split(":");
	        int iterations = Integer.parseInt(parts[0]);
	        byte[] salt = fromHex(parts[1]);
	        byte[] hash = fromHex(parts[2]);
	         
	        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        byte[] testHash = skf.generateSecret(spec).getEncoded();
	         
	        int diff = hash.length ^ testHash.length;
	        for(int i = 0; i < hash.length && i < testHash.length; i++)
	        {
	            diff |= hash[i] ^ testHash[i];
	        }
	        return diff == 0;
	    }
	    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
	    {
	        byte[] bytes = new byte[hex.length() / 2];
	        for(int i = 0; i<bytes.length ;i++)
	        {
	            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
	        }
	        return bytes;
	    }
	
	public static void copyFile(InputStream in, FileOutputStream out) throws IOException{
 
    	    byte[] buffer = new byte[1024];
 
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = in.read(buffer)) > 0){
 
    	    	out.write(buffer, 0, length);
 
    	    }
 
    	    in.close();
    	    out.close();
		
	}
	
}
