package cz.alenkacz.bookfan.activity;

import java.util.ArrayList;
import java.util.List;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.dto.Book;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BooksAdapter extends ArrayAdapter<Book> {
	
	private List<Book> mItems;
	
	public BooksAdapter(Context context, int textViewResourceId, List<Book> items) {
        super(context, textViewResourceId, items);
        this.mItems = items;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.part_book_item, null);
            }
            Book book = mItems.get(position);
            if (book != null) {
                    TextView tt = (TextView) v.findViewById(R.id.book_item_tv);
                    if (tt != null) {
                          tt.setText(book.name);                            
                    }
            }
            return v;
    }
}
