package flq.projectbooks;


import java.io.Serializable;

/**
 * Created by flori on 25/01/2016.
 */
public class Author implements Serializable {
    private long id;
    private String name;

    //constructor
    public Author(){
        id = -1;
        name= "";
    }

    public Author(String name){
        id = -1;
        this.name = name;
    }

    public Author(long id, String name){
        this.id = id;
        this.name = name;
    }

    //getter and setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
