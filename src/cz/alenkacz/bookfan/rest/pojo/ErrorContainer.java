package cz.alenkacz.bookfan.rest.pojo;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Error from backend
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class ErrorContainer {
	private String errormsg;
	
	public String getErrormsg() {
		return errormsg;
	}
	
	public void setErrormsg(String error) {
		this.errormsg = URLDecoder.decode(error);
	}
}
