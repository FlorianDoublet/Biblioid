package flq.projectbooks.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flq.projectbooks.R;

/**
 * Created by Lucas on 10/03/2016.
 */
public class FilterAdapter  extends BaseAdapter {

    private List<List<ImageView>> mListImagesViews;
    private List<String> names ;
    private List<String> filterNbBooks ;
    private List<String> filterNbFriendBooks ;
    private List<Boolean> belongToAFriend ;
    private boolean elementDeleted = false;

    private Context mContext;

    private LayoutInflater mInflater;

    public FilterAdapter(List<List<ImageView>> mListImagesViews, List<String> names, List<String> filterNbBooks, List<String> filterNbFriendBooks,  List<Boolean> belongToAFriend, Context mContext){
        this.mListImagesViews = mListImagesViews;
        this.names = names;
        this.filterNbBooks = filterNbBooks;
        this.mContext = mContext;
        this.filterNbFriendBooks = filterNbFriendBooks;
        this.belongToAFriend = belongToAFriend;

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return names.size();
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
        LinearLayout layoutItem;
        if (view == null) {
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.filter_detail, viewGroup, false);
        } else {
            layoutItem = (LinearLayout) view;
        }

        LinearLayout layout = (LinearLayout)layoutItem.findViewById(R.id.booksIconDisplay);
        layout.removeAllViews();
        for(ImageView img : mListImagesViews.get(i)){
            if(img.getParent() != null) {
                ((LinearLayout) img.getParent()).removeAllViews();
            }
            layout.addView(img);
        }
        TextView name = (TextView)layoutItem.findViewById(R.id.name);
        TextView nbBooks = (TextView)layoutItem.findViewById(R.id.nbLivre);
        TextView nbBooksFriend = (TextView)layoutItem.findViewById(R.id.nbLivreFriend);
        name.setText(names.get(i));
        nbBooks.setText(filterNbBooks.get(i));
        nbBooksFriend.setText(filterNbFriendBooks.get(i));

        if(belongToAFriend.get(i)){
            layoutItem.findViewById(R.id.filterBelongToFriendIcon).setVisibility(View.VISIBLE);
        }else{
            layoutItem.findViewById(R.id.filterBelongToFriendIcon).setVisibility(View.GONE);
        }

        return layoutItem;
    }

    public void deleteElement(int i){
        elementDeleted = true;
        mListImagesViews.remove(i);
        names.remove(i);
        filterNbBooks.remove(i);
    }
}
