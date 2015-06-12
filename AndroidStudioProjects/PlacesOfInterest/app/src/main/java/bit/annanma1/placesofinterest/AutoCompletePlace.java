package bit.annanma1.placesofinterest;

public class AutoCompletePlace {

    private String id;
    private String description;

    public AutoCompletePlace(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
