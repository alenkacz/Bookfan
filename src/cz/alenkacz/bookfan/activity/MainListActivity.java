package cz.alenkacz.bookfan.activity;

import java.util.ArrayList;
import java.util.List;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.R.layout;
import cz.alenkacz.bookfan.dto.Book;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainListActivity extends Activity {
	
	private ListView mBooksList;
	private Button mAddButton;
	private List<Book> mBooks;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main_list);
		
		initTempList();
		setupViews();
	}
	
	private void initTempList() {
		mBooks = new ArrayList<Book>();
		
		mBooks.add(new Book("Test knihy"));
		mBooks.add(new Book("Kniha 2"));
		mBooks.add(new Book("Kniha 3"));
	}
	
	private void setupViews() {
		mBooksList = (ListView) findViewById(R.id.books_list_lv);
		mBooksList.setAdapter(new BooksAdapter(this, R.layout.part_book_item, mBooks));
		
		mAddButton = (Button) findViewById(R.id.books_add_btn);
		mAddButton.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO barcode
			}
			
		});
	}
}
