package cz.alenkacz.bookfan.rest.pojo;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class BookAddResultContainer {
	public int saved;
	private String errormsg;
	
	public String getErrormsg() {
		return errormsg;
	}
	
	public void setErrormsg(String error) {
		this.errormsg = URLDecoder.decode(error);
	}
}
