package employee;

public class Employee {

    private final String email;
    private final String fullname;

    public Employee(String email, String fullname) {
        this.email = email;
        this.fullname=fullname;
    }

    public String getEmail() {
        return this.email;
    }
    public String getFullname() {return this.fullname;}
}
