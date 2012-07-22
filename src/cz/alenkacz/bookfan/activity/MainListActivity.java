package cz.alenkacz.bookfan.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.viewpagerindicator.TitlePageIndicator;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.dto.UserLogin;
import cz.alenkacz.bookfan.provider.BooksProvider.Books;
import cz.alenkacz.bookfan.rest.pojo.BookSearchContainer;
import cz.alenkacz.bookfan.rest.pojo.BooksLibraryContainer;
import cz.alenkacz.bookfan.rest.pojo.LibraryBook;
import cz.alenkacz.bookfan.rest.pojo.LoggedUserContainer;
import cz.alenkacz.bookfan.tools.Constants;
import cz.alenkacz.bookfan.tools.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity with list of books in user's library
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class MainListActivity extends BaseActivity {

	private ListView mBooksList;
	private Button mEmptyDownloadBtn;
	private Button mEmptyBtn;
	
	private SharedPreferences mPrefs;
	private ProgressDialog mDownloadingDialog;
	
	private final static int SHELFS_NUM  = 4;
	private LayoutInflater layoutInflater;
	
	MyAdapter mAdapter;
    ViewPager mPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mPrefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
		
		mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(!isLoggedIn()) {
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(i);
			finish();
		}
		
		//setupViews();
		//displayCurrentShelf();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.list, menu);
        
        try {
        	menu.getItem(2).getSubMenu().getItem(getCurrentShelfId()-1)
        		.setChecked(true);
        } catch(Exception e) {
        	// intentionally do nothing
        }
        
        return true;
    }

	
	private void setupViews() {
		mEmptyDownloadBtn = (Button) findViewById(R.id.books_list_empty_download_btn);
		mEmptyBtn = (Button) findViewById(R.id.books_list_empty_btn);
		mBooksList = (ListView) findViewById(R.id.books_list_lv);

		mBooksList.setEmptyView(findViewById(R.id.books_list_empty_layout));
		mBooksList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				Intent i = new Intent("android.intent.action.VIEW", 
						Uri.parse((String)view.getTag()));
				startActivity(i);
			}

		});
		
		mEmptyDownloadBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				downloadBooks();
			}
			
		});
		
		mEmptyBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				initScan();
			}
			
		});
	}

	private void downloadBooks() {
		int shelfId = mPrefs.getInt(Constants.PREFS_SHELF_ID, 1);
		mDownloadingDialog = ProgressDialog.show(MainListActivity.this, "",
				getString(R.string.book_library_download_pending), true);
		new LibraryFetchAsyncTask().execute(shelfId);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");

				Intent i = new Intent(getApplicationContext(),
						BookDetailActivity.class);
				i.putExtra(Constants.EXTRA_ISBN, contents);
				startActivity(i);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.menu_refresh:
				downloadBooks();
				return true;
            case R.id.menu_shelf_home:
            	item.setChecked(true);
            	shelfSelected(ShelfEnum.home);
            	return true;
            case R.id.menu_shelf_read:
            	item.setChecked(true);
            	shelfSelected(ShelfEnum.read);
            	return true;
            case R.id.menu_shelf_reading:
            	item.setChecked(true);
            	shelfSelected(ShelfEnum.reading);
            	return true;
            case R.id.menu_shelf_toread:
            	item.setChecked(true);
            	shelfSelected(ShelfEnum.toread);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	private void shelfSelected(ShelfEnum shelf) {
		Editor e = mPrefs.edit();
		e.putInt(Constants.PREFS_SHELF_ID, shelf.ordinal());
		e.commit();
		
		displayShelf(shelf.ordinal());
	}
	
	private int getCurrentShelfId() {
		return mPrefs.getInt(Constants.PREFS_SHELF_ID, 1);
	}
	
	private void displayCurrentShelf() {
		int shelfId = getCurrentShelfId();
		displayShelf(shelfId);
	}
	
	private void displayShelf(int shelfId) {
		ContentResolver cr = getContentResolver();
		final String[] projection = { Books._ID, Books.AUTHOR, Books.IMAGE, 
				Books.SHELF_ID, Books.TITLE, Books.URL };
		Cursor result = cr.query(Books.CONTENT_URI, projection, 
				Books.SHELF_ID + "=" + shelfId, null, null);
		
		BooksCursorAdapter adapter = new BooksCursorAdapter(this, result);
		mBooksList.setAdapter(adapter);
		
		if(result.getCount() == 0) {
			mBooksList.setEmptyView(findViewById(R.id.books_list_empty_layout));
		}
	}

	private class LibraryFetchAsyncTask extends AsyncTask<Integer, Void, String> {

		protected String doInBackground(Integer... shelfs) {
			try {
				int shelfId = 1;
				if(shelfs.length > 0) {
					shelfId = shelfs[0];
				} else {
					return null;
				}
				
				String token = mPrefs
						.getString(Constants.PREFS_LOGIN_TOKEN, "");
				HttpClient hc = Utils.getDefaultHttpClientWithCookie(mPrefs
						.getString(Constants.PREFS_LOGIN_TOKEN, ""));
				HttpGet get = new HttpGet(Utils.getLibraryGetUrl(token, shelfId));

				HttpResponse resp = hc.execute(get);
				int status = resp.getStatusLine().getStatusCode();
				if (status == 200) {
					InputStream isContent = resp.getEntity().getContent();

					return Utils.inputStreamToString(isContent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			mDownloadingDialog.dismiss();

			Log.d(Constants.LOGTAG, "received result: "+ result);
			
			if (result != null) {
				BooksLibraryContainer downloaded = new Gson().fromJson(result,
						BooksLibraryContainer.class);
				if(downloaded.books == null) {
					Toast.makeText(getApplicationContext(), getString(R.string.login_token_error), 
		        			Toast.LENGTH_LONG).show();
					logout();
				} else {
					deleteCurrentShelfInDb();
					saveToDb(downloaded.books);
				}
				
				displayCurrentShelf();
			} else {
				mBooksList.setEmptyView(findViewById(R.id.books_list_empty_layout));
			}
		}
		
		private void deleteCurrentShelfInDb() {
			int shelfId = mPrefs.getInt(Constants.PREFS_SHELF_ID, 1);
			
			ContentResolver cr = getContentResolver();
			final String[] projection = { Books._ID };
			Cursor result = cr.query(Books.CONTENT_URI, projection, 
					Books.SHELF_ID + "=" + shelfId, null, null);
			if(result.moveToFirst()) {
				while(!result.isAfterLast()) {
					String id = result.getString(result.getColumnIndex(Books._ID));
					cr.delete(Books.CONTENT_URI, Books._ID + "=" + id, null);
					result.moveToNext();
				}
			}
		}
		
		private void saveToDb(List<LibraryBook> books) {
			for(LibraryBook book : books) {
				ContentResolver cr = getContentResolver();
				ContentValues values = new ContentValues();
				values.put(Books.SHELF_ID, getCurrentShelfId());
				values.put(Books.TITLE, book.getBOOK_TITLE());
				values.put(Books.AUTHOR, book.getPT_FULL_NAME());
				values.put(Books.IMAGE, book.getBOOK_THUMB());
				values.put(Books.SERVER_UID, book.UNI_BOOK_KEY);
				values.put(Books.URL, Utils.createBookUrl(book.UNI_BOOK_KEY, 
						book.getBOOK_URL_ALIAS()));
				
				cr.insert(Books.CONTENT_URI, values);	
			}
		}
	}
	
	public static class MyAdapter extends FragmentPagerAdapter {
		
		public MyAdapter(FragmentManager fm) {
            super(fm);
        }
		
		@Override
		public Fragment getItem(int position) {
			return ArrayListFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return SHELFS_NUM;
		}	
		
		@Override
		public String getPageTitle(int number){
			if(number == ShelfEnum.home.ordinal()) {
				return "Mám ji doma";
			} else if(number == ShelfEnum.read.ordinal()) {
				return "Četl jsem";
			} else if(number == ShelfEnum.reading.ordinal()) {
				return "Právě čtu";
			} else {
				return "Chci číst";
			}
		}
	}
    
    public static class ArrayListFragment extends ListFragment {
        ShelfEnum mShelf;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();
            
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            args.putString("title", "aa");
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mShelf = getArguments() != null ? ShelfEnum.values()[getArguments().getInt("num")] : ShelfEnum.reading;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.part_fragment_pager_list, container, false);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, new String[]{ "aa", "bb" }));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
}
