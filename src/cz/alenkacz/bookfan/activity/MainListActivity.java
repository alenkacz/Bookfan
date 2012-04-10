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

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.dto.Book;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends BaseActivity {

	private ListView mBooksList;
	private View mEmptyView;
	private TextView mEmptyDownloadTv;
	private Button mEmptyDownloadBtn;
	private TextView mEmptyTv;
	private Button mEmptyBtn;
	
	private SharedPreferences mPrefs;
	private ProgressDialog mDownloadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);

		mPrefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

		setupViews();
		getSavedCategories();
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
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.list, menu);
        return true;
    }

	private void setupViews() {
		mEmptyView  = (View) findViewById(R.id.books_list_empty_layout);
		mEmptyDownloadTv = (TextView) findViewById(R.id.books_list_empty_download_tv);
		mEmptyDownloadBtn = (Button) findViewById(R.id.books_list_empty_download_btn);
		mEmptyTv = (TextView) findViewById(R.id.books_list_empty_tv);
		mEmptyBtn = (Button) findViewById(R.id.books_list_empty_btn);
		mBooksList = (ListView) findViewById(R.id.books_list_lv);
		
		showPrompt();

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
	
	private void showPrompt() {
		if(mPrefs.getBoolean(Constants.PREFS_SYNCED, false)) {
			showAddPrompt();
		} else {
			showDownloadPrompt();
		}
	}
	
	private void showDownloadPrompt() {
		mEmptyDownloadTv.setVisibility(View.VISIBLE);
		mEmptyDownloadBtn.setVisibility(View.VISIBLE);
		mEmptyTv.setVisibility(View.GONE);
		mEmptyBtn.setVisibility(View.GONE);
	}
	
	private void showAddPrompt() {
		mEmptyDownloadTv.setVisibility(View.GONE);
		mEmptyDownloadBtn.setVisibility(View.GONE);
		mEmptyTv.setVisibility(View.VISIBLE);
		mEmptyBtn.setVisibility(View.VISIBLE);
	}

	private void downloadBooks() {
		mDownloadingDialog = ProgressDialog.show(MainListActivity.this, "",
				getString(R.string.book_library_download_pending), true);
		new LibraryFetchAsyncTask().execute();
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
            case R.id.menu_shelf_old:
            	item.setChecked(true);
            	shelfSelected(ShelfEnum.old);
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
		
	}
	
	private void getSavedCategories() {
		ContentResolver cr = getContentResolver();
		final String[] projection = { Books._ID, Books.AUTHOR, Books.IMAGE, 
				Books.SHELF_ID, Books.TITLE, Books.URL };
		Cursor result = cr.query(Books.CONTENT_URI, projection, null, null, null);
		
		BooksCursorAdapter adapter = new BooksCursorAdapter(this, result);
		mBooksList.setAdapter(adapter);
		
		if(result.getCount() == 0) {
			mBooksList.setEmptyView(findViewById(R.id.books_list_empty_layout));
		}
	}

	private class LibraryFetchAsyncTask extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... nothing) {
			try {
				String token = mPrefs
						.getString(Constants.PREFS_LOGIN_TOKEN, "");
				HttpClient hc = Utils.getDefaultHttpClientWithCookie(mPrefs
						.getString(Constants.PREFS_LOGIN_TOKEN, ""));
				HttpGet get = new HttpGet(Utils.getLibraryGetUrl(token));

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

			if (result != null) {
				BooksLibraryContainer downloaded = new Gson().fromJson(result,
						BooksLibraryContainer.class);
				deleteAllInDb();
				saveToDb(downloaded.books);
				
				setSyncedFlag();
				getSavedCategories();
			} else {
				showPrompt();
				mBooksList.setEmptyView(findViewById(R.id.books_list_empty_layout));
			}
		}
		
		private void deleteAllInDb() {
			ContentResolver cr = getContentResolver();
			final String[] projection = { Books._ID };
			Cursor result = cr.query(Books.CONTENT_URI, projection, null, null, null);
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
				values.put(Books.SHELF_ID, 1);
				values.put(Books.TITLE, book.getBOOK_TITLE());
				values.put(Books.AUTHOR, book.getPT_FULL_NAME());
				values.put(Books.IMAGE, book.getBOOK_THUMB());
				values.put(Books.SERVER_UID, book.UNI_BOOK_KEY);
				values.put(Books.URL, Utils.createBookUrl(book.UNI_BOOK_KEY, 
						book.getBOOK_URL_ALIAS()));
				
				cr.insert(Books.CONTENT_URI, values);	
			}
		}
		
		private void setSyncedFlag() {
			Editor edit = mPrefs.edit();
			edit.putBoolean(Constants.PREFS_SYNCED, true);
			edit.commit();
		}
	}
}
