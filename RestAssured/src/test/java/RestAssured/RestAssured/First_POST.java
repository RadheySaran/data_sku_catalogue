package RestAssured.RestAssured;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class First_POST {
	
	 // URL = https://reqres.in/api/users
	
	
	@Test
	void test01()
	{
		JSONObject jsonData = new JSONObject();
		jsonData.put("name", "radhey");
		jsonData.put("job", "QA-II");
		
		RestAssured.baseURI = "https://reqres.in/api/users";
		RestAssured.given().header("Content-type" ,"application/json")
		.contentType(ContentType.JSON)
		.body(jsonData.toJSONString())
		.when().post()
		.then().statusCode(201).log().all();
		// .log.all() = to print all the response and status code in console.
		
		
		/* response in console
		 * { "name": "radhey", 
		 * "job": "QA-II", 
		 * "id": "445",                - UNIQUE ID
		 * "createdAt":"2024-01-27T06:28:27.608Z" }
		 */
		
	}

	
	
}
