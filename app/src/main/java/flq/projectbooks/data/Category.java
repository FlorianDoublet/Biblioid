package flq.projectbooks.data;

import java.io.Serializable;

/**
 * Created by flori on 08/02/2016.
 */
public class Category implements Serializable {
    private long id;
    private String name;


    //constructors
    public Category() {
        id = -1;

        name = "";
    }

    public Category(String name) {
        id = -1;
        this.name = name;
    }

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    //getters and setters
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
