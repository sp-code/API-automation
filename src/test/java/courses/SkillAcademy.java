package courses;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import common.BaseClass;
import io.restassured.response.Response;

public class SkillAcademy {
	
	String token;
	ArrayList <String>pid = new ArrayList<String>();

	@BeforeClass
	public void beforeClass() throws IOException
	{
		BaseClass obj = new BaseClass(); 
		token = obj.getToken();
	}
	
	
	@Test(priority=1)
	public void getProductId()
	{
		
			String s = "5f327fc414eda30d1090719e"; //targetSuperGroup - Professional Skills

			pid = given().
					when().
					get("api/v1/tsg/{id}/classes?skip=0&classType=Both&limit=0&language=English",s).
					then().
					extract().response().path("data.products._id");
	}
	
	@Test(priority=2)
	public void checkPaymentPartnerWithEmi()
	{
		SoftAssert softassert = new SoftAssert();
		for(int i=0;i<pid.size();i++)
		{
			String id = pid.get(i);
			Response res = 	given().
					when().
					get("api/v2_1/products/{pid}?facultyVideos=true&__projection=&isSkillCourse=false&language=English",id).
					then().
					extract().response();
			
			boolean isEmiAvailable = res.path("data.product.isEmiAvailable");
			boolean isJuspayTrans = res.path("data.product.isJuspayTrans");
			
			if(isEmiAvailable)
			{
				softassert.assertEquals(isJuspayTrans,isEmiAvailable,"EMI is availble but Juspay is not enabled for "+pid.get(i));
			}
		}
		softassert.assertAll();
	}
	
}
