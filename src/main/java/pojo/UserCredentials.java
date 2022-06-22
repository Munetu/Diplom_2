package pojo;

import lombok.Data;

@Data
public class UserCredentials {
    private String email;
    private String password;

    public UserCredentials(UserPOJO userPOJO) {
        this.email = userPOJO.getEmail();
        this.password = userPOJO.getPassword();
    }

    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserCredentials from(UserPOJO userPOJO) {
        return new UserCredentials(userPOJO);
    }

    public static UserCredentials withoutEmail(UserPOJO userPOJO) {
        return new UserCredentials("", userPOJO.getPassword());
    }

    public static UserCredentials withoutPassword(UserPOJO userPOJO) {
        return new UserCredentials(userPOJO.getEmail(), "");
    }


}
