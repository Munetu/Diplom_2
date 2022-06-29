package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPOJO {
    private String email;
    private String password;
    private String name;

    public static UserPOJO getRandom() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@ymail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        return new UserPOJO(email, password, name);
    }
}
