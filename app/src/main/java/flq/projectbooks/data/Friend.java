package flq.projectbooks.data;

import java.io.Serializable;

/**
 * Created by flori on 14/02/2016.
 */
public class Friend implements Serializable {

    private long id;
    private String firstName;
    private String lastName;
    private String cloudLink;

    //constructors
    public Friend(){
        id = -1;
        firstName = "";
        lastName = "";
        cloudLink = "";
    }

    public Friend(String firstName, String lastName, String cloudLink) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cloudLink = cloudLink;
    }

    public Friend(long id, String firstName, String lastName, String cloudLink) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cloudLink = cloudLink;
    }

    //getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCloudLink() {
        return cloudLink;
    }

    public void setCloudLink(String cloudLink) {
        this.cloudLink = cloudLink;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
