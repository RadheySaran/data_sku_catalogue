package HTTP_Vlaidate;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class Validte_JSON_BODY {
	
	
	//https://reqres.in/api/users?page=2
	
	@Test
	void test01()
	{
		RequestSpecification reqstSpcfication = RestAssured.given();
		reqstSpcfication.baseUri("https://reqres.in");
		reqstSpcfication.basePath("/api/users?page=2");
		
		Response response = reqstSpcfication.get();
		ResponseBody body = response.getBody();
		
		 String bdyStrng =body.asString();
		System.out.println(bdyStrng);
		
		// check the content of response
		Assert.assertEquals(bdyStrng.contains("George"), true , "check the content");
		
		System.out.println("USING JSON PATH");
		//using JSON 
		JsonPath path =response.jsonPath();
	    String fstName = path.get("data[0].first_name");
		Assert.assertEquals(fstName, "George");
		
		
	}

}
