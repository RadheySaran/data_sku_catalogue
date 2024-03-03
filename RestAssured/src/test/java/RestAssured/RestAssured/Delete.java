package RestAssured.RestAssured;

import org.testng.annotations.Test;

import io.restassured.RestAssured;

public class Delete {
             
	// status  requ      string
	// 200 get/put - OK
	// 201 post - created
	// 204 deleted
	
	@Test
	void test01()
	{
	  RestAssured.baseURI= "https://reqres.in/api/users/2";
	  RestAssured.given()
	  .when().delete()
	  .then().log().all()
	  .statusCode(204);
	}
}
