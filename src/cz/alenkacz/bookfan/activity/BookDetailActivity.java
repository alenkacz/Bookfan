package cz.alenkacz.bookfan.activity;

import cz.alenkacz.bookfan.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BookDetailActivity extends Activity {
	
	private Spinner mShelfSp;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        
        setupViews();
	}
	
	private void setupViews() {
		mShelfSp = (Spinner) findViewById(R.id.book_detail_shelf_sp);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.shelf_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mShelfSp.setAdapter(adapter);
	}
}
