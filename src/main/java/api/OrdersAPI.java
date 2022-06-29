package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.OrderPOJO;

public class OrdersAPI extends MainAPI {

    @Step("Послать POST запрос на ручку /orders без accessToken")
    public Response sendPostCreateOrderWithoutAuth(OrderPOJO orderPOJO) {
        return reqSpec.body(orderPOJO)
                .when()
                .post("/orders");
    }

    @Step("Послать POST запрос на ручку /orders с accessToken")
    public Response sendPostCreateOrderWithAuth(OrderPOJO orderPOJO, String accessToken) {
        String pureToken = accessToken.substring(7);
        return reqSpec.auth().oauth2(pureToken)
                .and()
                .body(orderPOJO)
                .when()
                .post("/orders");
    }

    @Step("Послать GET запрос на ручку /orders без accessToken")
    public Response sendGetOrdersWithoutAuth() {
        return reqSpec
                .get("/orders");
    }

    @Step("Послать GET запрос на ручку /orders с accessToken")
    public Response sendGetOrdersWithAuth(String accessToken) {
        String pureToken = accessToken.substring(7);
        return reqSpec.auth().oauth2(pureToken)
                .when()
                .get("/orders");
    }
}
