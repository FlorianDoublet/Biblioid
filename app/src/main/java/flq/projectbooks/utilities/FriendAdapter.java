package flq.projectbooks.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.libraries.FriendLibrary;

/**
 * Created by flori on 20/03/2016.
 */
public class FriendAdapter extends BaseAdapter {

    private List<String> names;
    private boolean elementDeleted = false;
    private ListView mListView;

    private Context mContext;

    private LayoutInflater mInflater;

    public FriendAdapter(ListView mListView, List<String> names, Context mContext) {
        this.names = names;
        this.mContext = mContext;
        this.mListView = mListView;

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
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.friend_detail, viewGroup, false);
        } else {
            layoutItem = (LinearLayout) view;
        }
        TextView name = (TextView) layoutItem.findViewById(R.id.friendCompleteName);
        name.setText(names.get(i));
        ImageButton downloadButton = (ImageButton) layoutItem.findViewById(R.id.download_friendDB_button);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = mListView.getPositionForView((View) view.getParent());
                Friend friend = FriendLibrary.getInstance().getFriendList().get(position);
                FriendCloudLibraryProcess friendLibProcess = new FriendCloudLibraryProcess(mContext, friend);
                friendLibProcess.execute();

            }

        });

        return layoutItem;
    }

    public void deleteElement(int i) {
        elementDeleted = true;
        names.remove(i);
    }

}
