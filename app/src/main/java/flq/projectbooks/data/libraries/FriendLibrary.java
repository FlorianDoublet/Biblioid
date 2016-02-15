package flq.projectbooks.data.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Friend;
import flq.projectbooks.database.FriendsDataSource;

/**
 * Created by flori on 15/02/2016.
 */
public class FriendLibrary implements Serializable{
    private static FriendLibrary friends;
    private static Context context;
    List<Friend> friendList;
    private FriendsDataSource datasource;

    public FriendLibrary() {
        friendList = new ArrayList<>();
        datasource = new FriendsDataSource(context);
        datasource.open();
        friendList = datasource.getAllFriends();
        datasource.close();
    }

    public FriendLibrary(Context _context) {
        context = _context;
        friends = new FriendLibrary();
    }

    public static FriendLibrary getInstance() {
        return friends;
    }

    public Friend Add(Friend friend) {
        friendList.add(friend);
        datasource.open();
        Friend new_friend = datasource.createFriend(friend.getFirstName(), friend.getLastName(), friend.getCloudLink()); //Add friend to database
        friendList = datasource.getAllFriends(); //Update friends
        datasource.close();
        return new_friend;
    }

    public List<Friend> getFriendList() {
        return friendList;
    }

    public Friend getNewFriend() {
        return new Friend();
    }

    public void deleteBook(Friend friend) {
        friendList.remove(friend);
    }

    public Friend getFriendById(long id) {
        for (int j = 0; j < friendList.size(); j++) {
            if (friendList.get(j).getId() == id) {
                return friendList.get(j);
            }
        }
        return null;
    }

    //get an friend with his name
    public Friend getFriendByFirstName(String firstName) {
        for (Friend friend : friendList) {
            if (friend.getFirstName().equals(firstName)) {
                return friend;
            }
        }
        return null;
    }

    public void deleteFriendById(int id) {
        for (int j = 0; j < friendList.size(); j++) {
            if (friendList.get(j).getId() == id) {
                //Remove from database
                Friend temp = friendList.get(j);
                datasource.open();
                datasource.deleteFriend(temp);
                datasource.close();

                //Remove from local list
                friendList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        datasource.open();
        friendList = datasource.getAllFriends();
        datasource.close();
    }


    public long updateOrAddFriend(Friend friend) {
        long id = friend.getId();
        if (id != -1) {
            for (int j = 0; j < friendList.size(); j++) {
                if (friendList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateFriend(friend); //Update database
                    datasource.close();
                    friendList.set(j, friend); //Update local list
                }
            }
        } else {
            datasource.open();
            id = datasource.createFriend(friend.getFirstName(), friend.getLastName(), friend.getCloudLink()).getId(); //Add friend to database
            friendList = datasource.getAllFriends(); //Update friends
            datasource.close();
        }
        return id;
    }

    public Friend findAndAddAFriend(String firstName, String lastName, String cloudLink){
        if(firstName.equals(""))
            return new Friend(-1,"", "", "");
        Friend friend = getFriendByFirstName(firstName);
        if(friend == null){
            friend = new Friend(-1, firstName, lastName, cloudLink);
        }
        long id = updateOrAddFriend(friend);
        friend.setId(id);
        return friend;
    }
}