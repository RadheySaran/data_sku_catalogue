package HTTP_Vlaidate;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class BDD_Velidate {
	
	
	
	@Test
	void Test01()
	{
     given()
	
	.when().get("https://reqres.in/api/users/2")
	
	.then()
	.statusCode(200)
	.log().all()
	.statusLine("HTTP/1.1 200 OK");
	}

}
