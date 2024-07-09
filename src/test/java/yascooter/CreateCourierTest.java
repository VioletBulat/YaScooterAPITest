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
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(201));

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", testWord);
        courier.put("password", testWord);
        courier.put("firstName", testWord);

        given()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().log().all();

        isCourierExists = true;
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("Тест на проверку, что нельзя создать двух одинаковых курьеров")
    public void createExistingCourierError409Test() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(201));

        HashMap<String, String> firstCourier = new HashMap<>();
        firstCourier.put("login", testWord);
        firstCourier.put("password", testWord);
        firstCourier.put("firstName", testWord);

        given()
                .body(firstCourier)
                .when()
                .post("/api/v1/courier")
                .then().log().all();

        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(409));

        HashMap<String, String> secondCourier = new HashMap<>();
        secondCourier.put("login", testWord);
        secondCourier.put("password", testWord);
        secondCourier.put("firstName", testWord);

        given()
                .body(secondCourier)
                .when()
                .post("/api/v1/courier")
                .then().log().all();

        isCourierExists = true;
    }

    @Test
    @DisplayName("Попытка войти без логина")
    @Description("Тест на проверку, что нельзя войти без логина")
    public void missingLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", "");
        courier.put("password", testWord);
        courier.put("firstName", testWord);

        given()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().log().all();
    }

    @Test
    @DisplayName("Попытка войти без пароля")
    @Description("Тест на проверку, что нельзя войти без пароля")
    public void missingPasswordTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", testWord);
        courier.put("password", "");
        courier.put("firstName", testWord);

        given()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then().log().all();
    }

    @After
    public void deleteCourier() {
        if (isCourierExists) {
            Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOk200());

            HashMap<String, String> courier = new HashMap<>();
            courier.put("login", testWord);
            courier.put("password", testWord);

            Response loginResponse = given()
                    .body(courier)
                    .when()
                    .post("/api/v1/courier/login")
                    .then().log().all()
                    .extract().response();

            int courierId = loginResponse.then().extract().path("id");

            given()
                    .when()
                    .delete("/api/v1/courier/" + courierId);
        }
    }
}
