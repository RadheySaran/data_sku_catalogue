package RestAssured.RestAssured;

public class notes {
	/* REST ASSUERED                                DAY 1
----------------                          ----------

DEPENDENCIES                       GET    - 200
-----------                        POST   - 201
1. REATASSURES                     PUT    - 200                                
2. JSON-PATH                       DELETE - 204
3. JSON
4. GSON
5. TESTNG
6. SCRIBEJAVA-APIS
7. JSON-SCHEMA-VALIDETOR
8. XML-SCHEMA-VALIDETOR


1. GIVEN - HEADER, CONTENET TYPE, BODY, SET COOKIES, AUTH, PARAMS etc......
2. WHEN  - GET, PUT, POST, DELETE
3. THEN  - VALIDTIONS(STATAUS CODE, HEADERS COOKIES, RESPONSE BODY... )
------------------------------------------------------------------------

GET REQUEST
------------
@TEST
 GIVEN()
.WHEN().GET(URL)
.THEN().STATUSCODE(200)
       .BODY("PAGE",EQUALTO(2))
       .LOG().ALL();
---------------------------------

POST REQUEST
-------------
@TEST
PUBLIC VOID POST(){
      HASHMAP DATA = NEW HASHMAP();
      DATA.PUT("NAME","RADHEY");
      DATA.PUT("JOB","QA-II");

.GIVEN()
     .CONTENT TYEP("APPLICATION/JSON")
     .BODY(DATA)
.WHEN()
     .POST("URL")
.THEN()
     .STATUSCODE(201)
     .LOG().ALL();
--------------------------------------
  IN THE ABOVE ACTION AN ID IS GENETEATED SO
WE CAN ALOSE GET THE ID IN CONSOLE LIKE:

INT ID; (DECLARE AS STATIC) 
@TEST
PUBLIC VOID POST(){
      HASHMAP DATA = NEW HASHMAP();
      DATA.PUT("NAME","RADHEY");
      DATA.PUT("JOB","QA-II");

.GIVEN()
     .CONTENT TYEP("APPLICATION/JSON")
     .BODY(DATA)
.WHEN()
     .POST("URL")
     .JASONBODY().GETINT("ID");
}
------------------------------------
AFTER GETTING THE ID WE NEED TO UPDATE THE SAME ID
SO WE PERFORM THE PUT ACTION WITH THE SAME ID

@TEST
PUBLIC VOID PUT(){

      HASHMAP DATA = NEW HASHMAP();
      DATA.PUT("NAME","RADHEY SARAN");
      DATA.PUT("JOB","QA-SDET");

.GIVEN()
     .CONTENT TYEP("APPLICATION/JSON")
     .BODY(DATA)
.WHEN()
     .POST("URL"+ID) - GETTING THE ID FROM THE POST REQUEST
.THEN()
     .STATUSCODE(200)
     .LOG().ALL();
}
--------------------------------------------------------
@TEST
PUBLC VOID DELETE(){
    
 .GIVEN()

 .WHEN()
     .DELETE("UPL"+ID)
 .THEN()
     .STATUSCODE(204);
}
-------------------------------------------------------

                                               VALIDATE
                                            -------------


                                                       DAY 2
                                                 ----------------
FOR POST REQUEST WE NEED BODY
        DIFFERENT WAY TO REATE BODY
1. HASHMAP
       HashMap data = new HashMap();
		data.put("name", "New_Standard");
		data.put("prefix", "NS");
		data.put("yearInUse", "1999");
		data.put("yearLastUpdated", "2023");
        given()
	  .log().all()
          .header("Content-Type", "application/json")
          .header("Authorization", "URL")
          .BODY(DATA)

        .when()
     .post("https://staging-admin-api.avighnasteel.in/api/v1/standard")
    // .jsonPath().getInt("id");     
	
	  .then() 	   
	  .statusCode(200)
	  .header("Content_type", "application/json")
	  .log().all();
	 
	}

2. org.json -dependencey in POM.XML
   JUST LIKE HASH MAP
          JSONObject data = new JSONObject();
		data.put("name", "New_Standard");
		data.put("prefix", "NS");
		data.put("yearInUse", "1999");
		data.put("yearLastUpdated", "2023");
        given()
	  .log().all()
          .header("Content-Type", "application/json")
          .header("Authorization", "URL")
          .BODY(DATA)

        .when()
     .post("https://staging-admin-api.avighnasteel.in/api/v1/standard")
    // .jsonPath().getInt("id");     
	
	  .then() 	   
	  .statusCode(200)
	  .header("Content_type", "application/json")
	  .log().all();
	 
	}

            
3. POJO CLASS

           public class POJO_Class {                         public class Post_Class {
	
	 String name;                                         void create(){
	 String prefix;                                        POJO_Class data = new POJO_Class();	    
	 int yearInUse;                                            data.setName("IS : 3452");
	 int yearLastUpdated;                                      data.setPrefix("IS :3461");
	 int hsnCodes;                                             data.setYearInUse(1998);
	 String bisHeading;                                    given().log().all()
	 String equivalentTags;                                       .header()
	 String status;                                               .header()
                                                               .body(data)
	 String application;                                   .when().post(url)
	 String features;                                      .then().statuscode(200).log().all();
	 String description;
	 
         public String getEquivalentTags() {
		return equivalentTags;
	}
	public void setEquivalentTags(String equivalentTags) {
		this.equivalentTags = equivalentTags;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

4. USING JSON EXTRA CLASS
         public class POST {
	                                                                               
	                                                                             CREATE A JSON FILE WITH EXTENTION - .json
	@Test                                                                                {
	void create() throws Exception                                                         "upcId": 3,
	{                                                                                      "basePrice": "75000",
	//read data from local JSON file then store in byte array                              "netWeight": "14", 
	   byte[] b = Files.readAllBytes(Paths.get("PATH OF JSON DATA IN ECLIPSE"));           "updatedBy": 2
                                                                                             }
	      //convert byte array to string
	      String bdy = new String(b);
	
      given()
	  .log().all()
     .header("Content-Type", "application/json") // Adding Content-Type header
     .header("Authorization", "eyJhbGci")
     .body(bdy)
     
     .when()
     .post("https://admin-api.avighnasteel.in/api/v1/inventory/")

	  .then() 	   
	  .statusCode(200)
	  .assertThat()
	  .log().all()
	  .extract().asString();
	 
	}
--------------------------------------------------------------
                       VERIFY
       .then() 	   
	  .statusCode(200)
          .body("name",equalTo("radhey"))
          .body("address",equalTo("delhi"))

	  .assertThat()
	  .log().all()
	  .extract().asString();
---------------------------------------------------------------------
                                                          DAY -3 
         PATH AND QUERY PARAMS
       "https://admin-api.avighnasteel.in/api/v1/inventory?PAGE=2"
        "https://admin-api.avighnasteel.in/api/v1/inventory?PAGE=2&ID=2"
       
       https://admin-api.avighnasteel.in              - DOMAIN
       api/v1/inventor                                - PATH
       PAGE=2                                         - QUERY PARAMS 

public void get(){
       given()
        .pathparems("mypath","v1")              pathparems
        .pathparems("mypath1","inventory")      pathparams
        .queyparems("page","2")                 querparams          
        .queyparems("ID","2")                   querparams

       .when()
         .get("https://admin-api.avighnasteel.in/api/{mypath}{mypath1}")
  
       .then()
        .statusCode(200)
        .log().all();

}
-----------------------------------------------------------------------------
COOKIES AND HEADERS

@TEST
 GIVEN()
.WHEN()
   .GET("www.google.com")
.THEN()
       .cookies("name(KEY)","VALUE")
       .LOG().ALL();
(COOKIES VALUES ALWAYS CHANGING SO TESE WILL FAIL)
SO FOR TAHT WE NEED TO STORE INTO VARIABLES  


@TEST
public void get cookies(){
  Response res =GIVEN()
.WHEN()
   .GET("www.google.com");

     String cookies_values =res.getCookies("name(KEY)","VALUE")
       syso("cookies are"+cookie_values)

}
HERE WE ARE FETCHING THE VALUE OF THE PARTICULAR KEY
-------------------------------------------------------
TO GET ALL THE COOKIES IN CONSOLE

@TEST
public void get cookies(){
  Response res =GIVEN()
.WHEN()
   .GET("www.google.com");
   MAP<String, String>cookies_value=res.getCookies();
syso(cookies_values.keySet());
THIS WILL GIVE ONLY KEY OF COOKIES.

    }
HERE WE FATCHING THE KEYS OF COOKIES

FOR GE ALL THE COOKIES KEY ANF  VALUES

for(String k : cookies_values.keySet())
{
  String cookies_value=res.getCookies(k)
  syso(k"    "+cookies_value);
}
-----------------------------------------------------------------------------------
HEADERS - VALUES NOT CHANGES COUNTINOUSELY SO WE AC VALIDARE THE VALUE OF HEADERS


      



































*/

}
