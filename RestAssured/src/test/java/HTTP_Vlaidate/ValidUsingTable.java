package HTTP_Vlaidate;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class ValidUsingTable {
	
	
	@Test
	void test01()
	{
		RestAssured.baseURI ="https://reqres.in/api/users/2";
		
		//Get Request Specification
		RequestSpecification request = RestAssured.given();
		
		//call get method
	     Response respose =	request.get();
	     ValidatableResponse valid = respose.then();
	     
	     //ststusCode
	     valid.statusCode(200);
	     
	     //statusLine
	     valid.statusLine("HTTP/1.1 200 OK");
	    
	     
	     
	     
	     
	     
	     
	     
	     
	}


}
