package scholarship_tests;

import static io.restassured.RestAssured.*;
import static io.restassured.config.EncoderConfig.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertNotEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import common.BaseClass;
import common.CheckStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RegisterScholarshipTest {

	static Response res;
	static ArrayList <String>scholarship;
	ArrayList <String>registeredtests = new ArrayList<String>();
	BaseClass bs;
	String token;

	@BeforeClass
	public void beforeClass() throws IOException
	{
		bs = new BaseClass();
		token = bs.getToken();
	}

	@Test(priority=1)
	public static void getScholarshipTests()
	{
		Response resScholarship = given().
				when().
				get("/api/v1/tests/scholarship").
				then().
				extract().
				response();

		res = resScholarship;

		//System.out.println(res.asPrettyString());
	}

	@Test(priority=2)
	public void getRegisteredTests()
	{
		scholarship = res.path("data.tests.id");

		registeredtests  = given().
				header("Authorization", "Bearer "+token)
				.when()
				.get("/api/v1/tests/scholarship?tsgId=&__projection=&language=English")
				.then()
				.extract()
				.response()
				.path("data.registeredTests");

		/*	if(registeredtests!=null)
		{
			scholarship.removeAll(registeredtests);
		} */

	}

	@Test(priority=3)
	public void registerTest() {

		//	{"mid":"61f7a2cb93dc775e2ef72130","mType":"liveTest"}
		String test = scholarship.get(0);
	/*	HashMap<String, String>stest = new HashMap<String, String>();
		stest.put("mid",test);
		stest.put("mType", "liveTest"); */
		System.out.println(test);

		given()
		//.contentType(ContentType.URLENC.withCharset("UTF-8"))
		//.formParam()
		.header("Authorization", "Bearer "+token).
		when().
		post("/api/v2/students/module-register?language=English").
		then().log().everything().
		statusCode(200);

		Reporter.log("Scholarship Test Registered");
	}


}
