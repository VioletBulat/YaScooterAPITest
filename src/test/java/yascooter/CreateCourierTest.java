package yascooter;

import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class CreateCourierTest {

    private final static String URL = "https://qa-scooter.praktikum-services.ru/";
    boolean isCourierExists;
    String testWord = "someone";

    @Before
    public void courierDefaultValue() {
        isCourierExists = false;
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Тест на проверку, что новый курьер создается успешно")
    public void createCourierOk201Test() {

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", testWord);
        courier.put("password", testWord);
        courier.put("firstName", testWord);

        given()
                .spec(Specifications.requestSpec(URL))
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().log().all()
                .spec(Specifications.responseSpecUnique(201));

        isCourierExists = true;
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("Тест на проверку, что нельзя создать двух одинаковых курьеров")
    public void createExistingCourierError409Test() {

        HashMap<String, String> firstCourier = new HashMap<>();
        firstCourier.put("login", testWord);
        firstCourier.put("password", testWord);
        firstCourier.put("firstName", testWord);

        given()
                .spec(Specifications.requestSpec(URL))
                .body(firstCourier)
                .when()
                .post("/api/v1/courier")
                .then().log().all()
                .spec(Specifications.responseSpecUnique(201));

        HashMap<String, String> secondCourier = new HashMap<>();
        secondCourier.put("login", testWord);
        secondCourier.put("password", testWord);
        secondCourier.put("firstName", testWord);

        given()
                .spec(Specifications.requestSpec(URL))
                .body(secondCourier)
                .when()
                .post("/api/v1/courier")
                .then().log().all()
                .spec(Specifications.responseSpecUnique(409));

        isCourierExists = true;
    }

    @Test
    @DisplayName("Попытка войти без логина")
    @Description("Тест на проверку, что нельзя войти без логина")
    public void missingLoginTest() {

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", "");
        courier.put("password", testWord);
        courier.put("firstName", testWord);

        given()
                .spec(Specifications.requestSpec(URL))
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().log().all()
                .spec(Specifications.responseSpecError400());
    }

    @Test
    @DisplayName("Попытка войти без пароля")
    @Description("Тест на проверку, что нельзя войти без пароля")
    public void missingPasswordTest() {

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", testWord);
        courier.put("password", "");
        courier.put("firstName", testWord);

        given()
                .spec(Specifications.requestSpec(URL))
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().log().all()
                .spec(Specifications.responseSpecError400());
    }

    @After
    public void deleteCourier() {
        if (isCourierExists) {

            HashMap<String, String> courier = new HashMap<>();
            courier.put("login", testWord);
            courier.put("password", testWord);

            Response loginResponse = given()
                    .spec(Specifications.requestSpec(URL))
                    .body(courier)
                    .when()
                    .post("/api/v1/courier/login")
                    .then().log().all()
                    .spec(Specifications.responseSpecOk200())
                    .extract().response();

            int courierId = loginResponse.then().extract().path("id");

            given()
                    .spec(Specifications.requestSpec(URL))
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .spec(Specifications.responseSpecOk200());
        }
    }
}
