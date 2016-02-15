package flq.projectbooks.data;

/**
 * Created by Florian on 09/02/2016.
 */
public class Publisher {

    private long id;
    private String name;

    //contructors
    public Publisher(){
        this.id = -1;
        this.name = "";
    }

    public Publisher(String name) {
        this.name = name;
    }

    public Publisher(long id, String name) {
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
