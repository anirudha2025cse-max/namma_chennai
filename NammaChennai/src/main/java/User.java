public class User {

    private int userId;
    private String name;
    private String username;
    private String password;
    private String phone;

    public User(int userId, String name, String username,
                String password, String phone) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
