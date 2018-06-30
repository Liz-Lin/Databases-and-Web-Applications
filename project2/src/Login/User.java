package Login;

/**
 * This Login.User class only has the email field in this example.
 * <p>
 * However, in the real project, this Login.User class can contain many more things,
 * for example, the user's shopping cart items.
 */
public class User {

    private final String email;
    private final int id;

    public User(int id, String email) {
        this.id=id;
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
    public int getId() {
        return this.id;
    }
}
