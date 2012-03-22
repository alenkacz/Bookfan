package cz.alenkacz.bookfan.activity;

import java.util.ArrayList;
import java.util.List;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.R.layout;
import cz.alenkacz.bookfan.dto.Book;
import android.app.Activity;
import android.content.Intent;
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
				Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
				startActivity(intent);
				//Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		        //intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		        //startActivityForResult(intent, 0);
			}
			
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	            // Handle successful scan
	        } else if (resultCode == RESULT_CANCELED) {
	            // Handle cancel
	        }
	    }
	}
}
