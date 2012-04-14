package cz.alenkacz.bookfan.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import cz.alenkacz.bookfan.dto.UserLogin;

public class Utils {
	public static String getLoginUrl(UserLogin user) {
		StringBuilder sb = new StringBuilder(Constants.BACKEND_LOGIN_URL);
		sb.append("email=");
		sb.append(user.email);
		sb.append("&password=");
		sb.append(user.password);
		
		return sb.toString();
	}
	
	public static String getFBLoginUrl(String token, String salt) {
		StringBuilder sb = new StringBuilder(Constants.BACKEND_LOGIN_URL);
		sb.append("fbuid=");
		sb.append(getSaltedHash(token, salt));
		
		return sb.toString();
	}
	
	public static String getLibraryGetUrl(String token, int shelfId) {
		StringBuilder sb = new StringBuilder(Constants.BACKEND_LIBRARY_GET_URL);
		sb.append(token);
		sb.append("/");
		sb.append(shelfId);
		
		return sb.toString();
	}
	
	public static String createBookUrl(String uid, String title) {
		StringBuilder sb = new StringBuilder(Constants.BOOK_URL);
		sb.append(uid);
		sb.append("/");
		sb.append(title);
		
		return sb.toString();
	}
	
	public static String getBookAddUrl(String bookId, int shelfId, String token) {
		StringBuilder sb = new StringBuilder(Constants.BACKEND_BOOK_ADD_URL);
		sb.append(bookId);
		sb.append("/");
		sb.append(shelfId);
		sb.append("/");
		sb.append(token);
		
		return sb.toString();
	}
	
	public static String getBookFindUrl(String isbn) {
		StringBuilder sb = new StringBuilder(Constants.BACKEND_BOOK_FIND_URL);
		sb.append(isbn);
		
		return sb.toString();
	}
	
	public static String inputStreamToString(InputStream is) {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader r = new BufferedReader(new InputStreamReader(is, "utf-8"), 1000);
			String l = null;
			while ((l = r.readLine()) != null) {
				sb.append(l);
			}
			
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String getSaltedHash(String item, String salt) {
		String toHash = item + salt;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(toHash.getBytes("UTF-8"));
			
			return Utils.convertByteHashToHex(md.digest());
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String convertByteHashToHex(byte[] data) { 
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < data.length; i++) { 
	        int halfbyte = (data[i] >>> 4) & 0x0F;
	        int two_halfs = 0;
	        do { 
	            if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                buf.append((char) ('0' + halfbyte));
	            else 
	                buf.append((char) ('a' + (halfbyte - 10)));
	            halfbyte = data[i] & 0x0F;
	        } while(two_halfs++ < 1);
	    } 
	    return buf.toString();
	} 
	
	public static DefaultHttpClient getDefaultHttpClientWithCookie(String token) {
		DefaultHttpClient client = new DefaultHttpClient();

		if (token != null && !token.equals("")) {
			BasicClientCookie c = new BasicClientCookie(Constants.COOKIE_NAME, token);
			c.setPath("/");
			c.setDomain(Constants.COOKIE_DOMAIN);
			client.getCookieStore().addCookie(c);
		}

		return client;
    }

}
