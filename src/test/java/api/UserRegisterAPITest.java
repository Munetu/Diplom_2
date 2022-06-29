package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import model.UserPOJO;

public class UserRegisterAPITest {
    private UserPOJO user;
    private UserAPI userAPI;
    private Response response;
    private boolean created;
    private Response deleteResponse;
    private boolean deleted;
    private String actualMessage;
    private String accessToken;

    @Before
    public void setup() {
        userAPI = new UserAPI();
    }

    @After
    public void teardown() {
        if (created) {
            deleteResponse = userAPI.sendDeleteUser(accessToken);
            deleted = userDeletedSuccess(deleteResponse);
        }
    }

    //СОЗДАТЬ УНИКАЛЬНОГО ПОЛЬЗОВАТЕЛЯ
    @Test
    @DisplayName("Проверка создания пользователя")
    public void createUserSuccess() {
        user = UserPOJO.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        Assert.assertTrue("Пользователь не был создан, ошибка регистрации", created);
    }

    //СОЗДАТЬ ПОЛЬЗОВАТЕЛЯ, КОТОРЫЙ УЖЕ БЫЛ ЗАРЕГЕСТРИРОВАН
    @Test
    @DisplayName("Проверка ошибки при создании пользователя, который уже был создан")
    public void createUserWhenAlreadyExists() {
        String expectedMessage = "User already exists";
        user = UserPOJO.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        response = userAPI.sendPostRequestRegisterUser(user);
        actualMessage = response.then()
                .assertThat()
                .statusCode(403)
                .extract()
                .path("message");
        ;
        Assert.assertEquals("Ожидается сообщение о том, что УЗ уже существует", expectedMessage, actualMessage);
    }

    //CОЗДАТЬ ПОЛЬЗОВАТЕЛЯ И НЕ ЗАПОЛНИТЬ ОДНО ИЗ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ
    @Test
    @DisplayName("Проверка ошибки при создании пользователя без Email")
    public void createUserWithoutEmail() {
        String expectedMessage = "Email, password and name are required fields";
        user = new UserPOJO(null, "password", "name");
        response = userAPI.sendPostRequestRegisterUser(user);
        actualMessage = userNotCreated403(response);
        Assert.assertEquals("Ожидается сообщение о том, что нужно заполнить все обязательные поля", expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Проверка ошибки при создании пользователя без Password")
    public void createUserWithoutPassword() {
        String expectedMessage = "Email, password and name are required fields";
        user = new UserPOJO("email@ymail.ru", null, "name");
        response = userAPI.sendPostRequestRegisterUser(user);
        actualMessage = userNotCreated403(response);
        Assert.assertEquals("Ожидается сообщение о том, что нужно заполнить все обязательные поля", expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Проверка ошибки при создании пользователя без Name")
    public void createUserWithoutName() {
        String expectedMessage = "Email, password and name are required fields";
        user = new UserPOJO("email@ymail.ru", "password", null);
        response = userAPI.sendPostRequestRegisterUser(user);
        actualMessage = userNotCreated403(response);
        Assert.assertEquals("Ожидается сообщение о том, что нужно заполнить все обязательные поля", expectedMessage, actualMessage);
    }

    @Step("Получить статус об успешном создании пользователя - 200")
    public boolean userCreatedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }

//    @Step("Получить ошибку о том, что такой пользователь уже существует на сервере - 403")
//    public String userAlreadyExists403(Response response){
//        return response.then()
//                .assertThat()
//                .statusCode(403)
//                .extract()
//                .path("message");
//    }

    @Step("Получить ошибку о том, что пользователь не создан - 403")
    public String userNotCreated403(Response response) {
        return response.then()
                .assertThat()
                .statusCode(403)
                .extract()
                .path("message");
    }

    @Step("Получить accessToken")
    public String userAccessToken(Response response) {
        return response.then()
                .extract()
                .path("accessToken");
    }

    @Step("Получить статус об успешном удалении пользователя - 202")
    public boolean userDeletedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(202)
                .extract()
                .path("success");
    }
}
