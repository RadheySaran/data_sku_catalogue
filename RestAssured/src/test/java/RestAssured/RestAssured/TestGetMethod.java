package RestAssured.RestAssured;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TestGetMethod {
	
	
	@Test
	void test02()
	{
		Response res = RestAssured.get("https://reqres.in/api/users?page=2");
		System.out.println(res.asString());
		System.out.println("STATUS CODE =" + res.statusCode());
		System.out.println("RESPONSE BODEY =" + res.getBody().asString());
		System.out.println("RESPONSE Time =" + res.getTime());
		//response header store in KEY and VELUE (key s here)
		System.out.println("RESPONSE HEADER =" + res.getHeader("Content-Type"));
		
		
		
		// Validate Status Code =200
		
		int status = res.getStatusCode();
		int actual =200;
	//	Assert.assertEquals(status, actual);
		Assert.assertEquals(200, status);
	}

}
