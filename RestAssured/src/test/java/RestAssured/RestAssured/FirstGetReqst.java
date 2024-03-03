package RestAssured.RestAssured;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class FirstGetReqst {
	
	// URL = https://reqres.in/api/users?page=2
	
	@Test
	void test01() 
	{
	  Response res = RestAssured.get("https://reqres.in/api/users?page=2");
	  System.out.println(res.asString());
	  System.out.println("Status code ="+res.getStatusCode());
	  
	}



}
