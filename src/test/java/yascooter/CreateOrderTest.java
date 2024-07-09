package yascooter;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final static String URL = "https://qa-scooter.praktikum-services.ru/";

    private final String firstName;
    private final String lastName;
    private final String address;
    private final int metroStation;
    private final String phoneNumber;
    private final int rentalDays;
    private final String deliveryDate;
    private final String comment;
    private final List<String> colors;

    public CreateOrderTest(String firstName, String lastName, String address, int metroStation,
                           String phoneNumber, int rentalDays, String deliveryDate, String comment, List<String> colors) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phoneNumber = phoneNumber;
        this.rentalDays = rentalDays;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[] data() {
        return new Object[][] {
                {"John", "Doe", "123 Main St", 5, "555-1234", 7, "2024-07-10", "Additional comment", List.of("BLACK")},
                {"Jane", "Smith", "456 Elm St", 9, "555-5678", 5, "2024-07-12", "No special requests", List.of("GREY")},
                {"Alice", "Johnson", "789 Oak St", 2, "555-9876", 3, "2024-07-15", "Urgent delivery needed", List.of()},
                {"Bob", "Williams", "321 Pine St", 1, "555-4321", 10, "2024-07-20", "Large group event", Arrays.asList("BLACK", "GREY")}
        };
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Тест на проверку успешного создания заказа")
    public void createOrderTest() {

        StringBuilder colorsJson = new StringBuilder("[");
        for (int i = 0; i < colors.size(); i++) {
            colorsJson.append("\n\t\"").append(colors.get(i)).append("\"\n");
            if (i < colors.size() - 1) {
                colorsJson.append(",");
            }
        }
        colorsJson.append("]");

        String requestBody = "{\n" +
                "  \"firstName\": \"" + firstName + "\",\n" +
                "  \"lastName\": \"" + lastName + "\",\n" +
                "  \"address\": \"" + address + "\",\n" +
                "  \"metroStation\": " + metroStation + ",\n" +
                "  \"phoneNumber\": \"" + phoneNumber + "\",\n" +
                "  \"rentalDays\": " + rentalDays + ",\n" +
                "  \"deliveryDate\": \"" + deliveryDate + "\",\n" +
                "  \"comment\": \"" + comment + "\",\n" +
                "  \"color\": " + colorsJson + "\n" +
                "}";

        Response responsePost = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(URL + "api/v1/orders")
                .then()
                .extract().response();

        responsePost.then().assertThat().body("track", notNullValue());
        int trackNumber = responsePost.path("track");

        given()
                .when()
                .get(URL + "api/v1/orders/track?t=" + trackNumber)
                .then()
                .assertThat()
                .statusCode(200);
    }
}
