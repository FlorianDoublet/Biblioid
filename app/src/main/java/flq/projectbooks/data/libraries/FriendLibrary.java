package flq.projectbooks.data.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.Loan;
import flq.projectbooks.database.FriendsDataSource;

/**
 * Created by flori on 15/02/2016.
 */
public class FriendLibrary implements Serializable {
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

    public static FriendLibrary getInstanceOrInitialize(Context _context) {
        if (friends == null) {
            new FriendLibrary(_context);
        }
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

    public void deleteFriend(Friend friend) {
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

    //get an friend with his firstName and lastName in the same string
    public Friend getFriendByFirstNameAndLastName(String completeName) {
        String[] splitedName = completeName.split("\\s+", 2);
        List<String> splitedNameList = new ArrayList<String>();
        splitedNameList.addAll(Arrays.asList(splitedName));

        splitedNameList.removeAll(Arrays.asList("", null));

        if (splitedNameList.size() == 1) {
            for (Friend friend : friendList) {
                if (friend.getFirstName().equals(splitedNameList.get(0))) {
                    return friend;
                }
            }
        } else {
            for (Friend friend : friendList) {
                if (friend.getFirstName().equals(splitedNameList.get(0)) && friend.getLastName().equals(splitedNameList.get(1))) {
                    return friend;
                }
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

                //remove Loan if exist
                List<Loan> loanList = new ArrayList<Loan>(LoanLibrary.getInstance().getAllLoansByFriendID(temp.getId()));
                if (loanList != null) {
                    for (Loan loan : loanList) {
                        LoanLibrary.getInstance().deleteLoanById(loan.getId());
                    }
                }

                //remove books of our friend
                List<Book> bookList = new ArrayList<Book>(BookLibrary.getInstance().getBookList());
                if (bookList != null) {
                    for (Book book : bookList) {
                        if (book.getFriend_id() == temp.getId())
                            BookLibrary.getInstance().deleteBookById(book.getId());
                    }
                }

                //remove filters of our friend
                List<BookFilter> bookFilters = new ArrayList<BookFilter>(BookFilterCatalog.getInstance().getBookFilterList());
                if (bookFilters != null) {
                    for (BookFilter filter : bookFilters) {
                        if (filter.getFriend_id() == temp.getId())
                            BookFilterCatalog.getInstance().deleteFilterById(filter.getId());
                    }
                }
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

    public Friend findAndAddAFriend(String firstName, String lastName, String cloudLink) {
        if (firstName.equals(""))
            return new Friend(-1, "", "", "");
        Friend friend = getFriendByFirstName(firstName);
        if (friend == null) {
            friend = new Friend(-1, firstName, lastName, cloudLink);
        }
        long id = updateOrAddFriend(friend);
        friend.setId(id);
        return friend;
    }
}
