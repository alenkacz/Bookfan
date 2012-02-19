package cz.alenkacz.bookfan.activity;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.R.layout;
import cz.alenkacz.bookfan.tools.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private static final String APP_ID = "251474701601587";
	private Facebook mFacebook;
	private SharedPreferences mPrefs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mPrefs = getPreferences(MODE_PRIVATE);
        mFacebook = new Facebook(APP_ID);
        
        if(!isLoggedIn()) {
        	mFacebook.authorize(this, new LoginDialogListener());
        } else {
        	final Intent i = new Intent(this, MainActivity.class);
        	startActivity(i);
        }
        
    }
    
    public void onResume() {    
        super.onResume();
        mFacebook.extendAccessTokenIfNeeded(this, null);
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
}