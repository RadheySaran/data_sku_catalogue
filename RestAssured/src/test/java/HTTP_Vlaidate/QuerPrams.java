package HTTP_Vlaidate;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class QuerPrams {
	
	// https://reqres.in/api/users?page=2
	
	@Test
	void Test01()
	{
		RequestSpecification specfction = RestAssured.given();
		specfction.baseUri("https://reqres.in");
		specfction.basePath("/api/users");
		specfction.queryParam("page", 2).queryParam("id", 10);
		
		//perform get reqst
		Response respons = specfction.get();
		String rspnsString  =respons.getBody().asString();
		System.out.println(rspnsString);
		
		JsonPath path = respons.jsonPath();
		String firstName = path.get("data.first_name");
		
		Assert.assertEquals(firstName, "Byron");
		
	}
 
}
