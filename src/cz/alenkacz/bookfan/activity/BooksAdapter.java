package cz.alenkacz.bookfan.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.imageloader.ImageLoader;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.dto.Book;
import cz.alenkacz.bookfan.rest.pojo.LibraryBook;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BooksAdapter extends ArrayAdapter<LibraryBook> {
	
	private List<LibraryBook> mItems;
	private ImageLoader mImageLoader;
	
	public BooksAdapter(Context context, int textViewResourceId, List<LibraryBook> items) {
        super(context, textViewResourceId, items);
        this.mItems = items;
        
        mImageLoader = ImageLoader.get(context);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.part_book_item, null);
            }
            LibraryBook book = mItems.get(position);
            if (book != null) {
                    TextView tt = (TextView) v.findViewById(R.id.book_name_tv);
                    TextView authorTv = (TextView) v.findViewById(R.id.book_author_tv);
                    ImageView iv = (ImageView) v.findViewById(R.id.book_item_iv);
                    
                    if (tt != null) {
                          tt.setText(book.getBOOK_TITLE());                            
                    }
                    
                    if(book.getBOOK_THUMB() != null) {
            			mImageLoader.bind(iv, book.getBOOK_THUMB(), null);
            		}
                    
                    if (authorTv != null) {
                    	authorTv.setText(book.getPT_FULL_NAME());
                    }
            }
            return v;
    }
}
