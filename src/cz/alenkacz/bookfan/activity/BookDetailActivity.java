package cz.alenkacz.bookfan.activity;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.google.android.imageloader.ImageLoader;
import com.google.gson.Gson;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.rest.pojo.Book;
import cz.alenkacz.bookfan.rest.pojo.BookSearchContainer;
import cz.alenkacz.bookfan.tools.Constants;
import cz.alenkacz.bookfan.tools.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetailActivity extends BaseActivity {
	
	private Spinner mShelfSp;
	private Button mAddBtn;
	private Button mCancelBtn;
	private Button mMoreInfoBtn;
	
	private View mContentLayout;
	private TextView mLoadingTv;
	
	private TextView mNameTv;
	private TextView mAuthorTv;
	private TextView mYearTv;
	private TextView mIsbnTv;
	private ImageView mBookImageIv;
	
	private ImageView mStars1Iv;
	private ImageView mStars2Iv;
	private ImageView mStars3Iv;
	private ImageView mStars4Iv;
	private ImageView mStars5Iv;
	
	private SharedPreferences mPrefs;
	
	private ImageLoader mImageLoader;
	
	private ProgressDialog mSearchingDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        
        mImageLoader = ImageLoader.get(this);
        mPrefs = getPreferences(MODE_PRIVATE);
        String isbn = getIntent().getStringExtra(Constants.EXTRA_ISBN);
        
        if(isbn != null) {
        	mSearchingDialog = ProgressDialog.show(BookDetailActivity.this, "", 
        			getString(R.string.book_search_pending), true);
        	
        	setupViews();
            new BookFindAsyncTask().execute(isbn);
        } else {
        	// TODO empty view
        	setupViews();
        	setupMockupView();
        }
	}
	
	private void setupViews() {
		mContentLayout = (View) findViewById(R.id.book_detail_content_layout);
		mLoadingTv = (TextView) findViewById(R.id.book_detail_loading);
		mShelfSp = (Spinner) findViewById(R.id.book_detail_shelf_sp);
		mAddBtn = (Button) findViewById(R.id.book_detail_add_btn);
		mCancelBtn = (Button) findViewById(R.id.book_detail_cancel_btn);
		mMoreInfoBtn = (Button) findViewById(R.id.book_detail_more_btn);
		
		mNameTv = (TextView) findViewById(R.id.book_detail_name);
		mAuthorTv = (TextView) findViewById(R.id.book_detail_author);
		mYearTv = (TextView) findViewById(R.id.book_detail_year);
		mIsbnTv = (TextView) findViewById(R.id.book_detail_isbn);
		mBookImageIv = (ImageView) findViewById(R.id.book_detail_image_iv);
		
		mStars1Iv = (ImageView) findViewById(R.id.book_detail_star_1_iv);
		mStars2Iv = (ImageView) findViewById(R.id.book_detail_star_2_iv);
		mStars3Iv = (ImageView) findViewById(R.id.book_detail_star_3_iv);
		mStars4Iv = (ImageView) findViewById(R.id.book_detail_star_4_iv);
		mStars5Iv = (ImageView) findViewById(R.id.book_detail_star_5_iv);
		
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
	
	private void setupMockupView() {
		mContentLayout.setVisibility(View.VISIBLE);
		mLoadingTv.setVisibility(View.GONE);
	}
	
	private void fillBookDetail(BookSearchContainer bookContainer) {
		mContentLayout.setVisibility(View.VISIBLE);
		mLoadingTv.setVisibility(View.GONE);
		
		Book book = bookContainer.results;
		
		mNameTv.setText(book.BOOK_TITLE);
		mAuthorTv.setText(book.AUTHOR_FULL_NAME);
		mYearTv.setText(book.BOOK_YEAR);
		mIsbnTv.setText(book.BOOK_ISBN);
		
		mImageLoader.bind(mBookImageIv, 
				"http://img.pemic.cz/sortimg/009/8/5/0098555-23.jpg", null);
		
		fillStarsView(book.BOOK_STARS);
	}
	
	private void fillStarsView(String stars) {
		try {
			float rating = Integer.parseInt(stars);
			if(rating <= 1) {
				if(rating < 1) {
					mStars1Iv.setImageResource(R.drawable.star_half);
				} else {
					mStars1Iv.setImageResource(R.drawable.star_full);
				}
			} else if(rating <= 2) {
				mStars1Iv.setImageResource(R.drawable.star_full);
				if(rating < 2) {
					mStars2Iv.setImageResource(R.drawable.star_half);
				} else {
					mStars2Iv.setImageResource(R.drawable.star_full);
				}	
			} else if(rating <= 3) {
				mStars1Iv.setImageResource(R.drawable.star_full);
				mStars2Iv.setImageResource(R.drawable.star_full);
				if(rating < 3) {
					mStars3Iv.setImageResource(R.drawable.star_half);
				} else {
					mStars3Iv.setImageResource(R.drawable.star_full);
				}	
			} else if(rating <= 4) {
				mStars1Iv.setImageResource(R.drawable.star_full);
				mStars2Iv.setImageResource(R.drawable.star_full);
				mStars3Iv.setImageResource(R.drawable.star_full);
				if(rating < 4) {
					mStars4Iv.setImageResource(R.drawable.star_half);
				} else {
					mStars4Iv.setImageResource(R.drawable.star_full);
				}
			} else {
				mStars1Iv.setImageResource(R.drawable.star_full);
				mStars2Iv.setImageResource(R.drawable.star_full);
				mStars3Iv.setImageResource(R.drawable.star_full);
				mStars4Iv.setImageResource(R.drawable.star_full);
				if(rating < 5) {
					mStars5Iv.setImageResource(R.drawable.star_half);
				} else {
					mStars5Iv.setImageResource(R.drawable.star_full);
				}
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	private class BookFindAsyncTask extends AsyncTask<String, Void, String> {
    	
        protected String doInBackground(String... isbns) {
        	try{
	        	String isbn;
	        	if(isbns.length > 0) {
	        		isbn = isbns[0];
	        	} else {
	        		return null;
	        	}
	        	
	        	HttpClient hc = Utils.getDefaultHttpClientWithCookie(
	        			mPrefs.getString(Constants.PREFS_LOGIN_TOKEN, ""));
				HttpGet get = new HttpGet(Utils.getBookFindUrl(isbn));
	
				HttpResponse resp = hc.execute(get);
				int status = resp.getStatusLine().getStatusCode();
				if(status == 200) {
					InputStream isContent = resp.getEntity().getContent();
					
					return Utils.inputStreamToString(isContent);
				}
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
			return null;
        }

        @Override
        protected void onPostExecute(String result) {
        	mSearchingDialog.dismiss();
        	
            if(result != null) {
            	BookSearchContainer book = new Gson().fromJson(result, BookSearchContainer.class);
            	if((book.getErrormsg() == null || book.getErrormsg().length() == 0) &&
            			book.results != null) {
            		fillBookDetail(book);
            	} else {
            		searchFailed();
            	}
            } else {
            	searchFailed();
            }
        }
        
        private void searchFailed() {
        	Toast.makeText(getApplicationContext(), getString(R.string.book_not_found), 
        			Toast.LENGTH_LONG).show();
        	finish();
        }
    }
}
