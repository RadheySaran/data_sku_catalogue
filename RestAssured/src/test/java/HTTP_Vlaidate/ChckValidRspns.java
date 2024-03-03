package HTTP_Vlaidate;

import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ChckValidRspns {
	
	
	@Test
	void test01()
	{
		baseURI ="https://reqres.in/api/users/2";
		
		//Get Request Specification
		RequestSpecification request = given();
		
		//call get method
	     Response respose =	request.get();
	     int statusCode = respose.getStatusCode();
	     
	     // validate Actual code
	     Assert.assertEquals(statusCode, 200,"correct status code");
	     
	    String statusLine= respose.getStatusLine();
	    Assert.assertEquals(statusLine, "HTTP/1.1 200 OK" , "incorrect status code");
	     
	     
	     
	     
	     
	     
	     
	     
	     
	}

}

































