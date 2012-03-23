package cz.alenkacz.bookfan.activity;

import cz.alenkacz.bookfan.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class BookDetailActivity extends Activity {
	
	private Spinner mShelfSp;
	private Button mAddBtn;
	private Button mCancelBtn;
	private Button mMoreInfoBtn;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        
        setupViews();
	}
	
	private void setupViews() {
		mShelfSp = (Spinner) findViewById(R.id.book_detail_shelf_sp);
		mAddBtn = (Button) findViewById(R.id.book_detail_add_btn);
		mCancelBtn = (Button) findViewById(R.id.book_detail_cancel_btn);
		mMoreInfoBtn = (Button) findViewById(R.id.book_detail_more_btn);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.shelf_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mShelfSp.setAdapter(adapter);
		
		mAddBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO add book
				finish();
			}
			
		});
		
		mCancelBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				finish();
			}
			
		});
		
		mMoreInfoBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO open webpage
			}
			
		});
	}
}
