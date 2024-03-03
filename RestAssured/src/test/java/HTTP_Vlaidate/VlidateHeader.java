package HTTP_Vlaidate;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class VlidateHeader {
	
	
	//  https://reqres.in/api/users/2
	
	@Test
	void test01()
	{
		RequestSpecification specif = RestAssured.given();
		
		
		specif.baseUri("https://reqres.in"); //base URI = https://reqres.in/
		specif.basePath("/api/users/2");     //base path
		
		Response response = specif.get();   //create response
	    String hdrRspons =	response.getHeader("Content-Type");
		System.out.println("content type in header = "+  hdrRspons);
		
		 // Return list of headers
		Headers hedrList = response.getHeaders();
		
		System.out.println("==========LIST OF HEADERS=========");
		
		for (Header list : hedrList) 
		{	
		 System.out.println(list.getName()+ " - "+ list.getValue());				
		}
		
		//validate content type
		Assert.assertEquals(response.getHeader("Content-Type"), "application/json; charset=utf-8");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

}
