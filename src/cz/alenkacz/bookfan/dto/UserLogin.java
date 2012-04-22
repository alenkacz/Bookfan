package cz.alenkacz.bookfan.dto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.alenkacz.bookfan.tools.Utils;

/**
 * Stores user credentials during the login process
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class UserLogin {
	public String email;
	public String password;
	
	public UserLogin(String email, String password) {
		this.email = email.trim();
		this.password = getPasswordHash(password.trim());
	}
	
	public String getPasswordHash(String pass) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(pass.getBytes("UTF-8"));
			
			return Utils.convertByteHashToHex(md.digest());
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
