package flq.projectbooks.data.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Publisher;
import flq.projectbooks.database.MySQLiteHelper;
import flq.projectbooks.database.PublishersDataSource;

/**
 * Created by flori on 13/02/2016.
 */
public class PublisherLibrary implements Serializable {
    private static PublisherLibrary publishers;
    private static Context context;
    List<Publisher> publisherList;
    private PublishersDataSource datasource;

    public PublisherLibrary() {
        publisherList = new ArrayList<>();
        datasource = new PublishersDataSource(context);
        datasource.open();
        publisherList = datasource.getAllPublishers();
        datasource.close();
    }

    public PublisherLibrary(MySQLiteHelper dbHelper) {
        publisherList = new ArrayList<>();
        datasource = new PublishersDataSource(dbHelper);
        datasource.open();
        publisherList = datasource.getAllPublishers();
        datasource.close();
    }

    public PublisherLibrary(Context _context) {
        context = _context;
        publishers = new PublisherLibrary();
    }

    public static PublisherLibrary getInstance() {
        return publishers;
    }

    public static PublisherLibrary getInstanceOrInitialize(Context _context) {
        if (publishers == null) {
            new PublisherLibrary(_context);
        }
        return publishers;
    }

    public Publisher Add(Publisher publisher) {
        publisherList.add(publisher);
        datasource.open();
        Publisher new_publisher = datasource.createPublisher(publisher.getName()); //Add book to database
        publisherList = datasource.getAllPublishers(); //Update publishers
        datasource.close();
        return new_publisher;
    }

    public List<Publisher> getPublisherList() {
        return publisherList;
    }

    public Publisher getNewPublisher() {
        return new Publisher();
    }

    public void deletePublisher(Publisher publisher) {
        publisherList.remove(publisher);
    }

    public Publisher getPublisherById(long id) {
        for (int j = 0; j < publisherList.size(); j++) {
            if (publisherList.get(j).getId() == id) {
                return publisherList.get(j);
            }
        }
        return null;
    }

    //get an publisher with his name
    public Publisher getPublisherByName(String name) {
        for (Publisher publisher : publisherList) {
            if (publisher.getName().equals(name)) {
                return publisher;
            }
        }
        return null;
    }

    public void deletePublisherById(int id) {
        for (int j = 0; j < publisherList.size(); j++) {
            if (publisherList.get(j).getId() == id) {
                //Remove from database
                Publisher temp = publisherList.get(j);
                datasource.open();
                datasource.deletePublisher(temp);
                datasource.close();

                //Remove from local list
                publisherList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        datasource.open();
        publisherList = datasource.getAllPublishers();
        datasource.close();
    }


    public long updateOrAddPublisher(Publisher publisher) {
        long id = publisher.getId();
        if (id != -1) {
            for (int j = 0; j < publisherList.size(); j++) {
                if (publisherList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updatePublisher(publisher); //Update database
                    datasource.close();
                    publisherList.set(j, publisher); //Update local list
                }
            }
        } else {
            datasource.open();
            id = datasource.createPublisher(publisher.getName()).getId(); //Add publisher to database
            publisherList = datasource.getAllPublishers(); //Update publishers
            datasource.close();
        }
        return id;
    }

    public Publisher findAndAddAPublisher(String name) {
        if (name.equals(""))
            return new Publisher(-1, "");
        Publisher publisher = getPublisherByName(name);
        if (publisher == null) {
            publisher = new Publisher(-1, name);
        }
        long id = updateOrAddPublisher(publisher);
        publisher.setId(id);
        return publisher;
    }
}
