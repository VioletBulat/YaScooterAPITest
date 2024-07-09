package yascooter;

import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class CheckCourierLoginTest {

    private final static String URL = "https://qa-scooter.praktikum-services.ru/";
    String testWord = "someone";
    int courierId;

    @Before
    public void createCourier() {
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
    }

    @Test
    @DisplayName("Успешный логин")
    @Description("Тест на проверку, что курьер может залогиниться")
    public void courierCanLoginTest() {
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

        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @DisplayName("Попытка войти без логина")
    @Description("Тест на проверку, что нельзя залогиниться без логина")
    public void missingLoginTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", "");
        courier.put("password", testWord);

        given()
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then().log().all();
    }

    // проверка: вход без пароля выдает ошибку
    @Test
    @DisplayName("Попытка войти без пароля")
    @Description("Тест на проверку, что нельзя залогиниться без пароля")
    public void missingPasswordTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        HashMap<String, String> courier = new HashMap<>();
        courier.put("login", testWord);
        courier.put("password", "");

        given()
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then().log().all();
    }

    @After
    public void deleteCourier() {
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

        if (courierId != 0) {
            given()
                    .when()
                    .delete("/api/v1/courier/" + courierId);
        }
    }
}
