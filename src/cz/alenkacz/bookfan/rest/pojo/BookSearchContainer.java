package cz.alenkacz.bookfan.rest.pojo;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class BookSearchContainer {
	public Book results;
	private String errormsg;
	
	public String getErrormsg() {
		return errormsg;
	}
	
	public void setErrormsg(String error) {
		this.errormsg = URLDecoder.decode(error);
	}
}
