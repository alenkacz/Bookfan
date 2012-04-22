package cz.alenkacz.bookfan.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.imageloader.ImageLoader;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.provider.BooksProvider.Books;
import cz.alenkacz.bookfan.rest.pojo.LibraryBook;
import cz.alenkacz.bookfan.tools.Constants;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for the list of books in users library
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class BooksCursorAdapter extends CursorAdapter {
	
	private List<LibraryBook> mItems;
	private ImageLoader mImageLoader;
	
	public BooksCursorAdapter(Context context, Cursor c) {
        super(context, c);
        
        mImageLoader = ImageLoader.get(context);
	}
	
	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		TextView nameTv = (TextView) v.findViewById(R.id.book_name_tv);
        TextView authorTv = (TextView) v.findViewById(R.id.book_author_tv);
        ImageView iv = (ImageView) v.findViewById(R.id.book_item_iv);
        
        String name = cursor.getString(cursor.getColumnIndex(Books.TITLE));
        String author = cursor.getString(cursor.getColumnIndex(Books.AUTHOR));
        String image = cursor.getString(cursor.getColumnIndex(Books.IMAGE));
        String url = cursor.getString(cursor.getColumnIndex(Books.URL));
		
		nameTv.setText(name);
		authorTv.setText(author);
		mImageLoader.bind(iv, image, null);
		v.setTag(url);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = LayoutInflater.from(context)
			.inflate(R.layout.part_book_item, parent, false);
		return view;
	}
}
