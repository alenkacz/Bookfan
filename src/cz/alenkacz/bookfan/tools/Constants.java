package cz.alenkacz.bookfan.tools;

/**
 * Constant strings and configuration
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class Constants {
	public static final String LOGTAG = "Bookfan";
	
	public static final String PREFS = "BookfanPreferences";
	public static final String PREFS_FB_TOKEN = "fbAccessToken";
	public static final String PREFS_FB_TOKEN_EXPIRES = "fbTokenExpires";
	
	public static final String PREFS_LOGIN_TOKEN = "loginToken";
	public static final String PREFS_LOGIN_USERNAME = "loginUsername";
	public static final String PREFS_LOGIN_USERID = "loginUserid";
	
	public static final String PREFS_SHELF_ID = "shelfId";
	
	public static final String EXTRA_ISBN = "isbnExtra";
	
	public static final String COOKIE_NAME = "PHPSESSID";
	public static final String COOKIE_DOMAIN = "http://www.bookfan.eu";
	
	public static final String BOOK_URL = "http://www.bookfan.eu/kniha/";
	
	public static final String BACKEND_LOGIN_URL = "http://www.bookfan.eu/api/2/login?";
	public static final String BACKEND_BOOK_ADD_URL = "http://www.bookfan.eu/api/2/library/add/";
	public static final String BACKEND_BOOK_FIND_URL = "http://www.bookfan.eu/api/2/search/book?q=";
	public static final String BACKEND_LIBRARY_GET_URL = "http://www.bookfan.eu/api/2/fetch-library/";
}
