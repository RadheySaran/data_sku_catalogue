package RestAssured.RestAssured;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class Patch_Put_445ID {
	
	
	
	@Test
	void test01()
	{

		JSONObject jsonData = new JSONObject();
		jsonData.put("name", " SARAN");
		jsonData.put("job", " ANALYIST");
		
		RestAssured.baseURI = "https://reqres.in/api/users/445";
		RestAssured.given().header("Content-type" ,"application/json")
		.contentType(ContentType.JSON)
		.body(jsonData.toJSONString())
		.when().patch()
		.then().statusCode(200).log().all();
	}

}
