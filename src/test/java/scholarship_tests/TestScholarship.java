package scholarship_tests;
import org.hamcrest.core.IsNot;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import common.BaseClass;
import common.CheckStatus;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertNotEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;

public class TestScholarship {

	static Response res;
	CheckStatus cs;
	BaseClass bs;
	
	@BeforeClass
	public void beforeClass() throws IOException
	{
		cs = new CheckStatus();
		bs = new BaseClass();
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
	}
	
	@Test(priority=2)
	public void scholarshipTestAvailable()
	{	
		ArrayList <String>test = res.path("data.tests");
		if(test==null)
		{
			Reporter.log("Scholarship tests are not available"); 
		}
	}
		
	@Test(priority=3)
	public void scholarshipTestStatusCode()
	{
		cs.code200("/api/v1/tests/scholarship?tsgId");
		ArrayList <String>id = res.path("data.tests.scholarshipId");
		for(int i=0;i<id.size();i++)
		{
			String scholarship_id = id.get(i);
			cs.code200("/api/v1/scholarship/",scholarship_id);
		}
	}
	
	@Test(priority=4)
	public void scholarshipTestSuccess()
	{
		given().
		when().
		get("/api/v1/tests/scholarship").
		then().
		assertThat().
		body("success", equalTo(true));
	}

	@Test(priority=5)
	public void CheckSlugIsNotNull()
	{	
		ArrayList<String> slugs = res.path("data.tests.slug");
		
		ArrayList <String>scholarshipTest = res.path("data.tests");
		
		for(int i=0;i<scholarshipTest.size();i++)
		{
			String slugcheck = slugs.get(i);
			assertNotEquals(slugcheck,null);
			assertNotEquals(slugcheck,"");
		}	
	}
}
