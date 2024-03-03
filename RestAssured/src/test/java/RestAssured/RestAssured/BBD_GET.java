package RestAssured.RestAssured;

import org.testng.annotations.Test;


// make the package static so that no need to use the class name multiple time
import  static io.restassured.RestAssured.*;

public class BBD_GET {
	
	// given , when , then ;
	// URL = https://reqres.in/api/users?page=2

	
	@Test
	void test01()
	{
		baseURI = "https://reqres.in/api/users";
		given()
		.queryParam("page", "2")
		.when().get()
		.then().statusCode(200);
	}

}
