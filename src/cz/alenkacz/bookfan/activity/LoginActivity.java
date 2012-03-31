package cz.alenkacz.bookfan.activity;

import java.io.InputStream;
import java.net.URLDecoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.*;
import com.google.gson.Gson;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.dto.UserLogin;
import cz.alenkacz.bookfan.rest.pojo.LoggedUserContainer;
import cz.alenkacz.bookfan.tools.Constants;
import cz.alenkacz.bookfan.tools.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	private static final String APP_ID = "251474701601587";
	private Facebook mFacebook;
	private SharedPreferences mPrefs;
	
	private Button mLoginBtn;
	private Button mFacebookLoginBtn;
	private EditText mEmailEt;
	private EditText mPasswordEt;
	
	private ProgressDialog mLoginDialog;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mPrefs = getPreferences(MODE_PRIVATE);
        mFacebook = new Facebook(APP_ID);
        
        getSupportActionBar().show();
        
        setupViews();
    }
    
    public void onResume() {    
        super.onResume();
        mFacebook.extendAccessTokenIfNeeded(this, null);
    }
    
    private void setupViews() {
    	mLoginBtn = (Button) findViewById(R.id.login_btn);
    	mFacebookLoginBtn = (Button) findViewById(R.id.login_fb_btn);
    	mEmailEt = (EditText) findViewById(R.id.login_email);
    	mPasswordEt = (EditText) findViewById(R.id.login_password);
    	 
    	mLoginBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				processLogin();
			}
    		
    	});
    	
    	mFacebookLoginBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				final Intent i = new Intent(getApplicationContext(), MainListActivity.class);
	        	startActivity(i);
			}
    		
    	});
    }
    
    /**
     * Retrieves data from edittext and tries to authorize user against server
     */
    private void processLogin() {
    	String email = mEmailEt.getText().toString();
    	String password = mPasswordEt.getText().toString();
    	
    	UserLogin us = new UserLogin(email, password);
    	mLoginDialog = ProgressDialog.show(LoginActivity.this, "", 
    			getString(R.string.login_progress), true);
    	new LoginAsyncTask().execute(us);
    }
    
    private boolean isLoggedIn() {
    	String access_token = mPrefs.getString(Constants.PREFS_FB_TOKEN, null);
        long expires = mPrefs.getLong(Constants.PREFS_FB_TOKEN_EXPIRES, 0);
        
        if(access_token != null) {
            mFacebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            mFacebook.setAccessExpires(expires);
        }
        
        return mFacebook.isSessionValid();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    private class LoginDialogListener implements DialogListener {

		public void onComplete(Bundle values) {
			SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(Constants.PREFS_FB_TOKEN, mFacebook.getAccessToken());
            editor.putLong(Constants.PREFS_FB_TOKEN_EXPIRES, mFacebook.getAccessExpires());
            editor.commit();
		}

		public void onFacebookError(FacebookError e) {
			Toast.makeText(LoginActivity.this, 
					getString(R.string.login_fb_error, e.getMessage()), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		public void onError(DialogError e) {
			Toast.makeText(LoginActivity.this, 
					getString(R.string.login_fb_error, e.getMessage()), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		public void onCancel() {
			// intentionally nothing
		}
    	
    }
    
    private class LoginAsyncTask extends AsyncTask<UserLogin, Void, String> {
    	
        protected String doInBackground(UserLogin... user) {
        	try{
	        	UserLogin userLogin;
	        	if(user.length > 0) {
	        		userLogin = user[0];
	        	} else {
	        		return null;
	        	}
	        	
	        	HttpClient hc = new DefaultHttpClient();
				HttpGet get = new HttpGet(Utils.getLoginUrl(userLogin));
	
				HttpResponse resp = hc.execute(get);
				int status = resp.getStatusLine().getStatusCode();
				if(status == 200) {
					InputStream isContent = resp.getEntity().getContent();
					
					String content = Utils.inputStreamToString(isContent);
					return content;
				}
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
			return null;
        }

        @Override
        protected void onPostExecute(String result) {
        	mLoginDialog.dismiss();
        	
            if(result != null) {
            	LoggedUserContainer user = new Gson().fromJson(result, LoggedUserContainer.class);
            	
            	if(user.getErrormsg() == null || user.getErrormsg().length() == 0) {
	            	SharedPreferences.Editor editor = mPrefs.edit();
	                editor.putString(Constants.PREFS_LOGIN_TOKEN, user.token);
	                editor.putString(Constants.PREFS_LOGIN_USERNAME, user.userData.fullname);
	                editor.putString(Constants.PREFS_LOGIN_USERID, user.userData.userId);
	                editor.commit();
	                
	                final Intent i = new Intent(getApplicationContext(), MainListActivity.class);
		        	startActivity(i);
            	} else {
            		loginFailed(user.getErrormsg());
            	}
            } else {
            	Toast.makeText(getApplicationContext(), getString(R.string.login_failed), 
            			Toast.LENGTH_LONG).show();
            }
        }
        
        private void loginFailed(String msg) {
        	StringBuilder sb = new StringBuilder(getString(R.string.login_failed));
        	
        	if(msg != null) {
        		sb.append(" MSG:");
        		sb.append(msg);
        	}
        	
        	Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
        }
    }
}