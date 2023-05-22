package it.unibs.ingsw.users.registered_users;

import it.unibs.ingsw.mylib.xml_utils.Parsable;

import java.util.ArrayList;

/**
 * Classe statica, serve per elaborare le informazioni contenute nel file .xml,
 * che contiene gli utenti autorizzati ad accedere a Ristorante.
 */
public class RegisteredUser implements Parsable {
    private String category;
    private String id;
    private String username;
    private String password;

    public static final String START_STRING = "user";
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili di istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("category");
        ATTRIBUTE_STRINGS.add("id");
        ATTRIBUTE_STRINGS.add("username");
        ATTRIBUTE_STRINGS.add("password");
    }


    /**
     * Metodo necessaio, siccome {@code RegisteredUser} implementa Parsable.
     */
    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setCategory);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setId);
        setters.put(ATTRIBUTE_STRINGS.get(2), this::setUsername);
        setters.put(ATTRIBUTE_STRINGS.get(3), this::setPassword);
    }

    @Override
    public String getStartString() {
        return START_STRING;
    }

    // Setters
    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters
    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {return username; }

    public String getPassword() {return password; }

    @Override
    public String toString() {
        return "User{" +
                "category='" + category + '\'' +
                ", id='" + id + '\'' +
                ", username=" + username +
                ", password=" + password +
                '}';
    }
}
