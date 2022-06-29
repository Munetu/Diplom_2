package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import model.IngredientsResponsePOJO;
import model.OrderPOJO;
import model.UserPOJO;

import java.util.List;

public class OrdersCreateAPITest {
    private UserPOJO user;
    private UserAPI userAPI;
    private Response response;
    private boolean created;
    private Response deleteResponse;
    private String expectedMessage;
    private String actualMessage;
    private String accessToken;
    private OrdersAPI ordersAPI;
    private IngredientsAPI ingredientsAPI;
    private OrderPOJO orderPOJO;
    private Response ingredientsResponse;
    private IngredientsResponsePOJO ingredientsResponsePOJO;
    private Response orderResponse;
    private boolean orderCreated;

    @Before
    public void setup() {
        ordersAPI = new OrdersAPI();
        ingredientsAPI = new IngredientsAPI();
    }

    @After
    public void teardown() {
        if (created) {
            deleteResponse = userAPI.sendDeleteUser(accessToken);
            boolean deleted = userDeletedSuccess(deleteResponse);
        }
    }

    //СОЗДАНИЕ ЗАКАЗА БЕЗ АВТОРИЗАЦИИ
    @Test
    @DisplayName("Проверка создания заказа без авторизации")
    public void createOrderWithoutAuthSuccess() {
        ingredientsResponse = ingredientsAPI.sendGetIngredients();
        ingredientsResponsePOJO = ingredientsList(ingredientsResponse);
        orderPOJO = new OrderPOJO(List.of(ingredientsResponsePOJO.getData().get(0).get_id()));
        orderResponse = ordersAPI.sendPostCreateOrderWithoutAuth(orderPOJO);
        orderCreated = orderCreatedSuccess(orderResponse);
        Assert.assertTrue("Ожидается, что заказ успеш создасться", orderCreated);
    }

    //СОЗДАНИЕ ЗАКАЗА БЕЗ АВТОРИЗАЦИИ И БЕЗ ИНГРЕДИЕНТОВ
    @Test
    @DisplayName("Проверка ошибки при создании заказа без авторизации и без ингридиентов")
    public void createOrderWithoutAuthNIngredients() {
        expectedMessage = "Ingredient ids must be provided";
        orderPOJO = new OrderPOJO(null);
        orderResponse = ordersAPI.sendPostCreateOrderWithoutAuth(orderPOJO);
        actualMessage = orderNotCreatedMustBeIds400(orderResponse);
        Assert.assertEquals("Ожидается сообщение о том, что нужно заполнить все обязательные поля", expectedMessage, actualMessage);
    }

    //СОЗДАНИЕ ЗАКАЗА БЕЗ АВТОРИЗАЦИИ И C НЕПРАВИЛЬНЫМ ID ИНГРЕДИЕНТА
    @Test
    @DisplayName("Проверка ошибки при создании заказа без авторизации и с неправильным _ID ингридиента")
    public void createOrderWithoutAuthNBadIngredientsIds() {
        orderPOJO = new OrderPOJO(List.of("123"));
        orderResponse = ordersAPI.sendPostCreateOrderWithoutAuth(orderPOJO);
        orderNotCreatedIncorrectIds500(orderResponse);
    }

    //СОЗДАНИЕ ЗАКАЗА С АВТОРИЗАЦИЕЙ
    @Test
    @DisplayName("Проверка создания заказа с авторизацией")
    public void createOrderWithAuthSuccess() {
        userAPI = new UserAPI();
        user = UserPOJO.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        ingredientsResponse = ingredientsAPI.sendGetIngredients();
        ingredientsResponsePOJO = ingredientsList(ingredientsResponse);
        orderPOJO = new OrderPOJO(List.of(ingredientsResponsePOJO.getData().get(0).get_id()));
        orderResponse = ordersAPI.sendPostCreateOrderWithAuth(orderPOJO, accessToken);
        orderCreated = orderCreatedSuccess(orderResponse);
        Assert.assertTrue("Ожидается, что заказ успеш создасться", orderCreated);
    }

    //СОЗДАНИЕ ЗАКАЗА С АВТОРИЗАЦИЕЙ И БЕЗ ИНГРЕДИЕНТОВ
    @Test
    @DisplayName("Проверка ошибки при создании заказа с авторизацией и без ингридиентов")
    public void createOrderWithAuthNIngredients() {
        expectedMessage = "Ingredient ids must be provided";
        userAPI = new UserAPI();
        user = UserPOJO.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        orderPOJO = new OrderPOJO(null);
        orderResponse = ordersAPI.sendPostCreateOrderWithAuth(orderPOJO, accessToken);
        actualMessage = orderNotCreatedMustBeIds400(orderResponse);
        Assert.assertEquals("Ожидается сообщение о том, что нужно заполнить все обязательные поля", expectedMessage, actualMessage);
    }

    //СОЗДАНИЕ ЗАКАЗА С АВТОРИЗАЦИЕЙ И C НЕПРАВИЛЬНЫМ ID ИНГРЕДИЕНТА
    @Test
    @DisplayName("Проверка ошибки при создании заказа с авторизацией и с неправильным _ID ингридиента")
    public void createOrderWithAuthNBadIngredientsIds() {
        userAPI = new UserAPI();
        user = UserPOJO.getRandom();
        response = userAPI.sendPostRequestRegisterUser(user);
        created = userCreatedSuccess(response);
        accessToken = userAccessToken(response);
        orderPOJO = new OrderPOJO(List.of("123"));
        orderResponse = ordersAPI.sendPostCreateOrderWithoutAuth(orderPOJO);
        orderNotCreatedIncorrectIds500(orderResponse);
    }

    public IngredientsResponsePOJO ingredientsList(Response response) {
        return response.body().as(IngredientsResponsePOJO.class);
    }

    @Step("Получить статус об успешном создании заказа - 200")
    public boolean orderCreatedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("success");
    }

    @Step("Получить сообщение о том, что для создания заказа должны быть посланы ID ингридиентов - 400")
    public String orderNotCreatedMustBeIds400(Response response) {
        return response.then()
                .assertThat()
                .statusCode(400)
                .extract()
                .path("message");
    }

    @Step("Получить сообщение о том, что были посланы неправильные ID ингридиентов - 400")
    public void orderNotCreatedIncorrectIds500(Response response) {
        response.then()
                .assertThat()
                .statusCode(500);
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

    @Step("Получить статус об успешном удалении пользователя - 202")
    public boolean userDeletedSuccess(Response response) {
        return response.then()
                .assertThat()
                .statusCode(202)
                .extract()
                .path("success");
    }
}
