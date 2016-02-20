package flq.projectbooks.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import flq.projectbooks.R;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.libraries.FriendLibrary;

/**
 * Created by flori on 20/02/2016.
 */
public class CreateFriend extends ActionBarActivity {
    Friend friend = new Friend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_friend);

       Intent intent = getIntent();
        /*if (intent.hasExtra(DisplayFriends.GIVE_FRIEND)) {
            setTitle("Modification d'un ami");
           // friend = (Friend) intent.getSerializableExtra(DisplayFriends.GIVE_FRIEND);
        } else {
            setTitle("Création d'un ami");
            filter = new BookFilter();
        }*/
        friend = new Friend();

        ((TextView) findViewById(R.id.friendFirstName)).setText(friend.getFirstName());
        ((TextView) findViewById(R.id.friendLastName)).setText(friend.getLastName());
        ((TextView) findViewById(R.id.friendCloudLink)).setText(friend.getCloudLink());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void friendCreation(View view) {


        EditText firstName = (EditText) findViewById(R.id.friendFirstName);
        EditText lastName = (EditText) findViewById(R.id.friendLastName);
        EditText cloudLink = (EditText) findViewById(R.id.friendCloudLink);

        friend.setFirstName(firstName.getText().toString());
        friend.setLastName(lastName.getText().toString());
        friend.setCloudLink(cloudLink.getText().toString());

        FriendLibrary.getInstance().updateOrAddFriend(friend);

        finish();
    }


}
