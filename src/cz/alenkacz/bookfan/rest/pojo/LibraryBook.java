package cz.alenkacz.bookfan.rest.pojo;

public class LibraryBook {
	public String UNI_BOOK_KEY;
	private String BOOK_TITLE;
	private String BOOK_URL_ALIAS;
	private String BOOK_THUMB;
	private String PT_FULL_NAME;
	
	public String getBOOK_TITLE() {
		return BOOK_TITLE;
	}
	public void setBOOK_TITLE(String bOOK_TITLE) {
		BOOK_TITLE = bOOK_TITLE;
	}
	public String getBOOK_URL_ALIAS() {
		return BOOK_URL_ALIAS;
	}
	public void setBOOK_URL_ALIAS(String bOOK_URL_ALIAS) {
		BOOK_URL_ALIAS = bOOK_URL_ALIAS;
	}
	public String getBOOK_THUMB() {
		return BOOK_THUMB;
	}
	public void setBOOK_THUMB(String bOOK_THUMB) {
		BOOK_THUMB = bOOK_THUMB;
	}
	public String getPT_FULL_NAME() {
		return PT_FULL_NAME;
	}
	public void setPT_FULL_NAME(String pT_FULL_NAME) {
		PT_FULL_NAME = pT_FULL_NAME;
	}
	
	
}
