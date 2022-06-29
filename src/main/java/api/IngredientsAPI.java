package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

public class IngredientsAPI extends MainAPI {

    @Step("Послать GET запрос на ручку /ingredients")
    public Response sendGetIngredients() {
        return reqSpec
                .get("/ingredients");
    }
}
