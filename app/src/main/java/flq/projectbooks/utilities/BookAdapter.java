package flq.projectbooks.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.database.LinkTablesDataSource;

/**
 * Created by Lucas on 15/03/2016.
 */
public class BookAdapter extends BaseAdapter {
    private List<Book> books;
    private List<Boolean> isSelected;
    private GridView gridViewBooks;
    private boolean isMultiSelectionActivated = false;

    private Context mContext;

    private LayoutInflater mInflater;

    public BookAdapter(List<Book> books, Context mContext, GridView gridView) {
        this.mContext = mContext;
        this.books = books;
        this.isSelected = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            isSelected.add(false);
        }
        this.gridViewBooks = gridView;

        mInflater = LayoutInflater.from(mContext);
    }

    public void changeSelection(int index) {
        this.isSelected.set(index, !this.isSelected.get(index));
    }

    public boolean isMultiSelectionActivated() {
        return isMultiSelectionActivated;
    }

    public void activateMultiSelection() {
        isMultiSelectionActivated = true;
    }

    public void desactivateMultiSelection() {
        isMultiSelectionActivated = false;
        for (int i = 0; i < books.size(); i++) {
            isSelected.set(i, false);
        }
    }

    public void deleteSelectedBooks() {
        for (int i = isSelected.size() - 1; i >= 0; i--) {
            if (isSelected.get(i)) {
                BookLibrary.getInstance().deleteBookById((int) books.get(i).getId());
            }
        }
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

        ImageView imgView = ((ImageView) layoutItem.findViewById(R.id.img));
        if (books.get(i).getImage() != null) {
            byte[] img = books.get(i).getImage();
            imgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(img, 0, img.length)));
        } else {
            imgView.setImageResource(R.drawable.picturebook);
        }

        TextView txt = ((TextView) layoutItem.findViewById(R.id.text));
        txt.setText(books.get(i).getTitle());

        int bookWidth = gridViewBooks.getColumnWidth();
        int bookHeight = (int) ((float) bookWidth * (float) 3 / 2);

        if (bookHeight < 160) {
            layoutItem.findViewById(R.id.text).setVisibility(View.GONE);
        } else {
            layoutItem.findViewById(R.id.text).setVisibility(View.VISIBLE);
        }

        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.height = bookHeight;
        imgView.setLayoutParams(params);

        ImageView friendLogo = (ImageView) layoutItem.findViewById(R.id.friendIconImageView);

        if (books.get(i).getFriend_id() == -1) {
            friendLogo.setVisibility(View.GONE);

            txt.setPadding(0, 0, 0, 0);
        } else {
            friendLogo.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams paramsFriend = friendLogo.getLayoutParams();
            paramsFriend.height = bookWidth / 4;
            paramsFriend.width = bookWidth / 4;
            friendLogo.setLayoutParams(paramsFriend);

            txt.setPadding(0, 0, bookWidth / 4 + 3, 0);
        }

        View selectedBorder = layoutItem.findViewById(R.id.selectedLayout);
        if (isMultiSelectionActivated) {
            selectedBorder.setVisibility(View.VISIBLE);
            if (isSelected.get(i)) {
                selectedBorder.setBackground(mContext.getResources().getDrawable(R.drawable.selectedbookborder));
            } else {
                selectedBorder.setBackground(mContext.getResources().getDrawable(R.drawable.notselectedbookborder));
            }
        } else {
            selectedBorder.setVisibility(View.GONE);

        }

        return layoutItem;
    }

    public void addBooksToFilter(List<Category> cs){
        for (int i = isSelected.size() - 1; i >= 0; i--) {
            if (isSelected.get(i)) {
                if(books.get(i).getCategories() == null){
                    LinkTablesDataSource.feedBookWithCategories(books.get(i), cs);
                } else{
                    cs.addAll(books.get(i).getCategories());
                    LinkTablesDataSource.feedBookWithCategories(books.get(i), cs);

                }

            }
        }
    }
}
