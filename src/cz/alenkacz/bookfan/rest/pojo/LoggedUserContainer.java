package cz.alenkacz.bookfan.rest.pojo;

import java.net.URLDecoder;

public class LoggedUserContainer {
	public LoggedUser userData;
	public String token;
	private String errormsg;
	
	public String getErrormsg() {
		return errormsg;
	}
	
	public void setErrormsg(String error) {
		errormsg = URLDecoder.decode(error);
	}
}
