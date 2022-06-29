package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import model.UserPOJO;

public class OrdersGetAPITest {
    private UserPOJO user;
    private UserAPI userAPI;
    private Response response;
    private boolean created;
    private boolean orderGotten;
    private Response deleteResponse;
    private String expectedMessage;
    private String actualMessage;
    private String accessToken;
    private OrdersAPI ordersAPI;
    private Response orderResponse;

    @Before
    public void setup() {
        ordersAPI = new OrdersAPI();
    }

    @After
    public void teardown() {
        if (created) {
            deleteResponse = userAPI.sendDeleteUser(accessToken);
            boolean deleted = userDeletedSuccess(deleteResponse);
        }
    }

    //ПОЛУЧЕНИЕ ЗАКАЗОВ КОНКРЕТНОГО ПОЛЬЗОВАТЕЛЯ БЕЗ АВТОРИЗАЦИИ
    @Test
    @DisplayName("Проверка получения заказа конкретного пользователя без авторизации")
    public void createOrderWithoutAuthSuccess() {
        expectedMessage = "You should be authorised";
        orderResponse = ordersAPI.sendGetOrdersWithoutAuth();
        actualMessage = orderResponse.then()
                .assertThat()
                .statusCode(401)
                .extract()
                .path("message");
        ;
        Assert.assertEquals("Ожидается сообщение о том, что нужно быть авторизованным", expectedMessage, actualMessage);
    }

    //ПОЛУЧЕНИЕ ЗАКАЗОВ КОНКРЕТНОГО ПОЛЬЗОВАТЕЛЯ С АВТОРИЗАЦИЕЙ
    @Test
    @DisplayName("Проверка получения заказа конкретного пользователя с авторизацией")
    public void createOrderWithAuthSuccess() {
        userAPI = new UserAPI();
        user = UserPOJO.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        orderResponse = ordersAPI.sendGetOrdersWithAuth(accessToken);
        orderGotten = orderResponse.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
        ;
        Assert.assertTrue("Ожидается, что будет получен список заказов конкретного пользователя", orderGotten);
    }

//    @Step("Получить ошибку о том, что нельзя получить заказы без авторизации - 401")
//    public String orderNotGottenMustBeAuth(Response response) {
//        return response.then()
//                .assertThat()
//                .statusCode(401)
//                .extract()
//                .path("message");
//    }

//    @Step("Получить статус об успешном получении заказов пользователя - 200")
//    public boolean orderGottenSuccess(Response response) {
//        return response.then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .path("success");
//    }

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

    @Step("Получить статус об успешном удалении пользователя - 202")
    public boolean userDeletedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(202)
                .extract()
                .path("success");
    }
}
