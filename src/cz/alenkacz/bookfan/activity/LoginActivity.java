package cz.alenkacz.bookfan.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.*;
import com.google.gson.Gson;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.dto.UserLogin;
import cz.alenkacz.bookfan.rest.pojo.FBUser;
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

/**
 * Activity with login and facebook connect functionality
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class LoginActivity extends SherlockActivity {
	private Facebook mFacebook;
	private SharedPreferences mPrefs;
	
	private Button mLoginBtn;
	private Button mFacebookLoginBtn;
	private EditText mEmailEt;
	private EditText mPasswordEt;
	
	private ProgressDialog mLoginDialog;
	private ProgressDialog mFbLoginDialog;
	private LoginActivity mActivity;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mPrefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        mActivity = this;
        mFacebook = new Facebook(getString(R.string.config_app_id)); 
        
        setupViews();
        
        if(isLoggedIn()) {
        	final Intent i = new Intent(getApplicationContext(), MainListActivity.class);
        	startActivity(i);
        	finish();
        }
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
				executeLogin();
			}
    		
    	});
    	
    	mFacebookLoginBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				String access_token = mPrefs.getString(Constants.PREFS_FB_TOKEN, null);
		        long expires = mPrefs.getLong(Constants.PREFS_FB_TOKEN_EXPIRES, 0);
		        if(access_token != null) {
		            mFacebook.setAccessToken(access_token);
		        }
		        if(expires != 0) {
		            mFacebook.setAccessExpires(expires);
		        }
		        
				if(mFacebook.isSessionValid()) {
					processFBLoginSuccess();
				} else {
					mFacebook.authorize(mActivity, new LoginDialogListener());
				}
			}
    		
    	});
    }
    
    /**
     * Retrieves data from edittext and tries to authorize user against server
     */
    private void executeLogin() {
    	String email = mEmailEt.getText().toString();
    	String password = mPasswordEt.getText().toString();
    	
    	UserLogin us = new UserLogin(email, password);
    	mLoginDialog = ProgressDialog.show(LoginActivity.this, "", 
    			getString(R.string.login_progress), true);
    	new LoginAsyncTask().execute(us);
    }
    
    private boolean isLoggedIn() {
    	String token = mPrefs.getString(Constants.PREFS_LOGIN_TOKEN, null);
    	
    	return (token != null);
    }
    
    private void processFBLoginSuccess() {
    	try {
        	String json = mFacebook.request("me");
        	FBUser user = new Gson().fromJson(json, FBUser.class);
        	
        	mFbLoginDialog = ProgressDialog.show(LoginActivity.this, "", 
        			getString(R.string.login_progress), true);
        	new FacebookLoginAsyncTask().execute(user.id);
        } catch(IOException e) {
        	e.printStackTrace();
        	// authorization failed msg
        }
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
            
            processFBLoginSuccess();
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
			String a = "a";
			String b = a;
		}
    	
    }
    
    private void processLogin(LoggedUserContainer user) {
    	if(user.getErrormsg() == null || user.getErrormsg().length() == 0) {
        	SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(Constants.PREFS_LOGIN_TOKEN, user.token);
            editor.putString(Constants.PREFS_LOGIN_USERNAME, user.userData.fullname);
            editor.putString(Constants.PREFS_LOGIN_USERID, user.userData.userId);
            editor.commit();
            
            final Intent i = new Intent(this, MainListActivity.class);
            startActivity(i);
            finish();
    	} else {
    		loginFailed(user.getErrormsg());
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
    
    private class FacebookLoginAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... tokens) {
			try {
				String token;
				if(tokens.length > 0) {
					token = tokens[0];
				} else {
					return null;
				}
				
				HttpClient hc = new DefaultHttpClient();
				HttpGet get = new HttpGet(Utils.getFBLoginUrl(token, 
						getString(R.string.config_salt)));
	
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
			mFbLoginDialog.dismiss();
        	
            if(result != null) {
            	LoggedUserContainer user = new Gson().fromJson(result, 
            			LoggedUserContainer.class);
            	processLogin(user);
            }
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
            	
            	processLogin(user);
            } else {
            	Toast.makeText(getApplicationContext(), getString(R.string.login_failed), 
            			Toast.LENGTH_LONG).show();
            }
        }
    }
}