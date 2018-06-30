public class Actor {
    private String id;
    private String name;
    private Integer birthYear;

    public Actor(String id, String name, Integer birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Actor [ ");
        sb.append("id: " + getId());
        sb.append(", ");
        sb.append("name: " + getName());
        sb.append(", ");
        sb.append("birthYear: " + getBirthYear());
        sb.append(" ]");

        return sb.toString();

    }
}
