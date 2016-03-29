package flq.projectbooks.UI.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.utilities.FriendAdapter;


/**
 * Created by flori on 20/02/2016.
 */
public class DisplayFriends extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private int selectedFilterIndex;
    private ListView friendList;
    private FriendAdapter listAdapter;
    private FriendLibrary friendLibrary;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_friend);

        friendList = (ListView) findViewById(R.id.friendList);
        friendList.setItemsCanFocus(false);

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "TODO -> ca affiche le filtre", Toast.LENGTH_SHORT).show();

            }
        });

        friendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterIndex = position;
                PopupMenu popupMenu = new PopupMenu(DisplayFriends.this, view);
                popupMenu.setOnMenuItemClickListener(DisplayFriends.this);
                popupMenu.inflate(R.menu.friendclickpopup);
                popupMenu.show();

                return true;
            }
        });

        friendLibrary = FriendLibrary.getInstance();

        createListView(friendLibrary);
    }

    private void createListView(FriendLibrary friends) {
        ListView friendList = (ListView) findViewById(R.id.friendList);

        List<String> names = new ArrayList<>();
        List<String> filterNbBooks = new ArrayList<>();
        for (Friend friend : friends.getFriendList()) {
            String completeName = friend.getFirstName();
            if (friend.getLastName() != null || friend.getLastName() != "")
                completeName += " " + friend.getLastName();
            names.add(completeName);
        }

        listAdapter = new FriendAdapter(friendList, names, this);

        friendList.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_display_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.create_friend_option:
                Intent intent = new Intent(this, CreateFriend.class);
                startActivityForResult(intent, 0);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        createListView(friendLibrary);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_friend:
                FriendLibrary.getInstance().deleteFriendById((int) friendLibrary.getFriendList().get(selectedFilterIndex).getId());
                listAdapter.deleteElement(selectedFilterIndex);
                listAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Ami supprimé", Toast.LENGTH_SHORT).show();

                return true;
        }
        return true;
    }


}
