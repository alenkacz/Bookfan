package cz.alenkacz.bookfan;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ResponseCache;

import android.content.ComponentName;
import android.os.Environment;
import android.util.Log;

import com.google.android.imageloader.ImageLoader;

import cz.alenkacz.bookfan.tools.Constants;

/**
 * Base Application class with added ImageLoader functionality
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class Application extends android.app.Application {
	private ImageLoader mImageLoader;
	
	@Override
	public synchronized Object getSystemService(String name) {
		if (ImageLoader.IMAGE_LOADER_SERVICE.equals(name)) {
			if (mImageLoader == null) {
				mImageLoader = new ImageLoader();
			}
			return mImageLoader;
		}
		
		return super.getSystemService(name);
	}
}
