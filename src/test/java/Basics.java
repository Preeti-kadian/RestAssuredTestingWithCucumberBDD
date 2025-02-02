package test.java;

import files.ReUsableMethods;
import files.payload;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Basics {
    public static void main(String[] args) {
        //validate Add Place API is working as expected
        //given- all input details
        //when- Submit the API - resource, HTTP method
        //Then - Validate the response
        RestAssured.baseURI = "https://rahulshettyacademy.com";
        String response = given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json")
                .body(payload.AddPlace())
                .when().post("/maps/api/place/add/json")
                .then().assertThat().statusCode(200).body("scope", equalTo("APP"))
                .header("Server", "Apache/2.4.52 (Ubuntu)").extract().response().asString();

        //Print the response body
        System.out.println("API response is as : "+response);

        //Parsing json to validate the Place_ID field in response
        JsonPath js = new JsonPath(response); //For parsing json
        String placeId = js.getString("place_id");
        System.out.println("Place_id value is : "+placeId);

        //update Place_id
        String newAddress= "Summer walk, USA";
        given().log().all().queryParam("key","qaclick123").header("Content-Type", "application/json")
                .body("{\n\"place_id\":\""+placeId+"\",\n\"address\":\""+newAddress+"\",\n\"key\":\"qaclick123\"\n}")
                .when().put("/maps/api/place/update/json")
                .then().assertThat().log().all().statusCode(200).body("msg", equalTo("Address successfully updated"));

        //Get Place
        String getPlaceResponse = given().log().all().queryParam("key", "qaclick123")
                .queryParam("place_id", placeId)
                .when().get("/maps/api/place/get/json")
                .then().assertThat().log().all().statusCode(200).extract().response().asString();

        JsonPath js1 = ReUsableMethods.rawToJson(getPlaceResponse);
        String actualAddress = js1.getString("address");
        System.out.println("Adress after update is : "+actualAddress);
        //verify address is updated as expected
        Assert.assertEquals(actualAddress, newAddress);


    }
}
