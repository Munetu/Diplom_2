package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import model.UserCredentials;
import model.UserPOJO;

public class UserLoginAPITest {
    private UserPOJO user;
    private UserAPI userAPI;
    private Response response;
    private boolean created;
    private boolean loginSuccess;
    private Response loginResponse;
    private String actualMessage;
    private String accessToken;

    @Before
    public void setup() {
        userAPI = new UserAPI();
        user = UserPOJO.getRandom();
    }

    @After
    public void teardown() {
        if (created) {
            Response deleteResponse = userAPI.sendDeleteUser(accessToken);
            boolean deleted = userDeletedSuccess(deleteResponse);
        }
    }

    //ЛОГИН ПОД СУЩЕСТВУЮЩИМ ПОЛЬЗОВАТЕЛЕМ
    @Test
    @DisplayName("Проверка логина пользователя")
    public void loginUserSuccess() {
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        UserCredentials credentials = UserCredentials.from(user);
        loginResponse = userAPI.sendPostLoginUser(credentials);
        loginSuccess = userLoginSuccess(loginResponse);
        Assert.assertTrue("Авторизация не прошла, ожидается boolean success = true", loginSuccess);
    }

    //ЛОГИН С НЕВЕРНЫМ ЛОГИНОМ И ПАРОЛЕМ
    @Test
    @DisplayName("Проверка ошибки при логине пользователя со значением Email = null")
    public void loginUserWithoutEmail() {
        String expectedMessage = "email or password are incorrect";
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        UserCredentials credentials = UserCredentials.withoutEmail(user);
        loginResponse = userAPI.sendPostLoginUser(credentials);
        actualMessage = userLoginIncorrectCredentials(loginResponse);
        Assert.assertEquals("Ожидается сообщение о некорректном email или password", expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Проверка ошибки при логине пользователя со значением Password = null")
    public void loginUserWithoutPassword() {
        String expectedMessage = "email or password are incorrect";
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        UserCredentials credentials = UserCredentials.withoutPassword(user);
        loginResponse = userAPI.sendPostLoginUser(credentials);
        actualMessage = userLoginIncorrectCredentials(loginResponse);
        Assert.assertEquals("Ожидается сообщение о некорректном email или password", expectedMessage, actualMessage);
    }

    @Step("Получить статус об успешном создании пользователя - 200")
    public boolean userCreatedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }

    @Step("Получить accessToken")
    public String userAccessToken(Response response) {
        return response.then()
                .extract()
                .path("accessToken");
    }

    @Step("Получить статус об успешном логине пользователя - 200")
    public boolean userLoginSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }

    @Step("Получить статус об успешном удалении пользователя - 202")
    public boolean userDeletedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(202)
                .extract()
                .path("success");
    }

    @Step("Получить ошибку о некорректных кредах - 401")
    public String userLoginIncorrectCredentials(Response response) {
        return response.then()
                .assertThat()
                .statusCode(401)
                .extract()
                .path("message");
    }
}
