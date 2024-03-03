package RestAssured.RestAssured;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class Put_ID_445 {
	
//	{
//	    "name": "radhey",
//	    "job": "QA-II",
//	    "id": "445",
//	    "createdAt": "2024-01-27T06:28:27.608Z"
//	}
//	
	
	
	// https://reqres.in/api/users/2
	//https://reqres.in/api/users/445 - change to update the other id
	
	@Test
	void test01()
	{
		JSONObject jsonData = new JSONObject();
		jsonData.put("name", "RADHEY SARAN");
		jsonData.put("job", "QUALITY ANALYIST");
		
		RestAssured.baseURI = "https://reqres.in/api/users/445";
		RestAssured.given().header("Content-type" ,"application/json")
		.contentType(ContentType.JSON)
		.body(jsonData.toJSONString())
		.when().put()
		.then().statusCode(200).log().all();
	}

}
