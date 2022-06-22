package api;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class MainAPI {
    protected final String URL = "https://stellarburgers.nomoreparties.site/api";
    protected final RequestSpecification reqSpec = given()
            .header("Content-Type", "application/json")
            .baseUri(URL);

}
