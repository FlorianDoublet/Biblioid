package flq.projectbooks.utilities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.data.Book;

/**
 * Created by Lucas on 15/03/2016.
 */
public class BookAdapter extends BaseAdapter {
    private List<Book> books;
    private boolean elementDeleted = false;
    private GridView gridViewBooks;

    private Context mContext;

    private LayoutInflater mInflater;

    public BookAdapter(List<Book> books, Context mContext, GridView gridView){
        this.mContext = mContext;
        this.books = books;
        this.gridViewBooks = gridView;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FrameLayout layoutItem;
        if (view == null) {
            layoutItem = (FrameLayout) mInflater.inflate(R.layout.book_detail, viewGroup, false);
        } else {
            layoutItem = (FrameLayout) view;
        }



        ImageView imgView = ((ImageView)layoutItem.findViewById(R.id.img));
        if(books.get(i).getImage() != null){
            byte[] img = books.get(i).getImage();
            imgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(img, 0, img.length)));
        }else{
            imgView.setImageResource(R.drawable.picturebook);
        }

        ((TextView)layoutItem.findViewById(R.id.text)).setText(books.get(i).getTitle());

        int bookWidth = gridViewBooks.getColumnWidth() ;
        int bookHeight = (int)((float)bookWidth * (float)3/2);

        if(bookHeight < 160){
            layoutItem.findViewById(R.id.text).setVisibility(View.GONE);
        }else{
            layoutItem.findViewById(R.id.text).setVisibility(View.VISIBLE);
        }

        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.height = bookHeight;
        imgView.setLayoutParams(params);

        return layoutItem;
    }

}
