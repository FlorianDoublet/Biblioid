package flq.projectbooks.data.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Author;
import flq.projectbooks.database.AuthorsDataSource;

/**
 * Created by flori on 25/01/2016.
 */
public class AuthorLibrary implements Serializable {
    private static AuthorLibrary authors;
    private static Context context;
    List<Author> authorList;
    private AuthorsDataSource datasource;

    public AuthorLibrary() {
        authorList = new ArrayList<>();
        datasource = new AuthorsDataSource(context);
        datasource.open();
        authorList = datasource.getAllAuthors();
        datasource.close();
    }

    public AuthorLibrary(Context _context) {
        context = _context;
        authors = new AuthorLibrary();
    }

    public static AuthorLibrary getInstance() {
        return authors;
    }

    public static AuthorLibrary getInstanceOrInitialize(Context _context){
        if(authors == null){
            new AuthorLibrary(_context);
        }
        return authors;
    }

    public Author Add(Author author) {
        authorList.add(author);
        datasource.open();
        Author new_author = datasource.createAuthor(author.getName()); //Add book to database
        authorList = datasource.getAllAuthors(); //Update authors
        datasource.close();
        return new_author;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public Author getNewAuthor() {
        return new Author();
    }

    public void deleteAuthor(Author author) {
        authorList.remove(author);
    }

    public Author getAuthorById(long id) {
        for (int j = 0; j < authorList.size(); j++) {
            if (authorList.get(j).getId() == id) {
                return authorList.get(j);
            }
        }
        return null;
    }

    //get an author with his name
    public Author getAuthorByName(String name) {
        for (Author author : authorList) {
            if (author.getName().equals(name)) {
                return author;
            }
        }
        return null;
    }

    public void deleteAuthorById(int id) {
        for (int j = 0; j < authorList.size(); j++) {
            if (authorList.get(j).getId() == id) {
                //Remove from database
                Author temp = authorList.get(j);
                datasource.open();
                datasource.deleteAuthor(temp);
                datasource.close();

                //Remove from local list
                authorList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        datasource.open();
        authorList = datasource.getAllAuthors();
        datasource.close();
    }


    public void updateOrAddAuthor(Author author) {
        long id = author.getId();
        if (id != -1) {
            for (int j = 0; j < authorList.size(); j++) {
                if (authorList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateAuthor(author); //Update database
                    datasource.close();
                    authorList.set(j, author); //Update local list
                    return;
                }
            }
        } else {
            datasource.open();
            datasource.createAuthor(author.getName()); //Add author to database
            authorList = datasource.getAllAuthors(); //Update authors
            datasource.close();
        }
    }


}
