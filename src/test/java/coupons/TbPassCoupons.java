package coupons;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import common.BaseClass;
import common.CheckStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;


import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TbPassCoupons {

	ArrayList <String>tbPassType;
	ArrayList <String>tbPass;
	ArrayList <String>globalPasses;
	ArrayList <String>title;
	static ArrayList <String>tbPassIds;
	String token;
	CheckStatus cs;

	@BeforeClass
	public void beforeClass() throws IOException
	{
		BaseClass obj = new BaseClass(); 
		token = obj.getToken();
		cs = new CheckStatus();
		
	}
	
	@Test(priority=1)
	public void checkPercentDiscountOnMonthlyPass()
	{
		Response res = 	given().
				when().
				get("api/v2.1/students/coupon?coupon=4WI85LS5T&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English").
				then().
				log().ifError().
				extract().
				response();

		int oldCost = res.path("data.products[0].oldCost");
		int cost = res.path("data.products[0].cost");

		double value = (double)10/100;

		value = value*oldCost;

		int newCost = (int) (oldCost-value);
		
		assertThat(newCost+1, equalTo(cost));
		
		Reporter.log("Discount applied on Monthly pass");
		
	}
	
	@Test(priority=2)
	public void checkPercentDiscountCoupon()
	{
		SoftAssert softassert = new SoftAssert();
		Response res = 	given().
				when().
				get("api/v2.1/students/coupon?coupon=RUTS86C96&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English").
				then().
				log().ifError().
				extract().
				response();

		title = res.path("data.products.title");
		int discountValue = res.path("data.coupon.discountValue"); //10%

		for(int i=0;i<title.size();i++)
		{
			String s=String.valueOf(i);
			int oldCost = res.path("data.products[%s].oldCost",s);
			int cost = res.path("data.products[%s].cost",s);
			String tbTitle = res.path("data.products[%s].title",s);

			double value = (double)discountValue/100;

			value = value*oldCost;

			int newCost = (int)(oldCost-value);

			softassert.assertEquals(newCost+1, cost,tbTitle);
		}
		softassert.assertAll();
		Reporter.log("% Discount coupon is working fine");
	}
	
	@Test(priority = 3)
	public void cashDiscountCoupon()
	{
		SoftAssert softassert = new SoftAssert();
		Response res = 	given().
				when().
				get("api/v2.1/students/coupon?coupon=VX5JFM9JE&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English").
				then().
				log().ifError().
				extract().
				response();

		title = res.path("data.products.title");
		int discountValue = res.path("data.coupon.discountValue"); //30

		for(int i=0;i<title.size();i++)
		{
			String s=String.valueOf(i);
			int cost = res.path("data.products[%s].cost",s);
			int oldCost = res.path("data.products[%s].oldCost",s);
			String tbTitle = res.path("data.products[%s].title",s);
			
		
			softassert.assertEquals(oldCost-discountValue,cost,tbTitle);
		}
		softassert.assertAll();
		Reporter.log("Cash discount Coupon is working fine");
	}
	
	@Test(priority=4)
	public void fullDiscountCoupon()
	{
	 SoftAssert softassert = new SoftAssert();
		Response res =
				given().
				when().
				get("/api/v2.1/students/coupon?coupon=ITJ7JBBCV&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English").
				then().
				log().ifError().
				extract().
				response();

		for(int i=0;i<title.size();i++)
		{
			String s=String.valueOf(i);
			int cost = res.path("data.products[%s].cost",s);
			String tbTitle = res.path("data.products[%s].title",s);
			
			softassert.assertEquals(cost, 0, tbTitle);
		}
		softassert.assertAll();
		Reporter.log("100% discount is applicable on all TB passes");

	}
	
	@Test(priority=5)
	public void checkMinValue()
	{
		SoftAssert softassert = new SoftAssert();
		Response res = 	given().
				when().
				get("api/v2.1/students/coupon?coupon=C900SBCP4&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English").
				then().
				log().ifError().
				extract().response();

		title = res.path("data.products.title");

		for(int i=0;i<title.size();i++)
		{
			String s=String.valueOf(i);
			int cost = res.path("data.products[%s].cost",s);
			int minCost = res.path("data.products[%s].minCost",s);
			String tbTitle = res.path("data.products[%s].title",s);
			
			//Assert.assertEquals(cost, minCost);
			softassert.assertEquals(cost,minCost,tbTitle);
		}
		softassert.assertAll();
		Reporter.log("Min Value validated for all TB passes");
	}
	
	@Test(priority = 6)
	public void checkUseLimit()
	{
		given().
		when().
		get("api/v2.1/students/coupon?coupon=SRGTN3A4O&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English").
		then().
		assertThat().
		statusCode(400).
		body("message",equalTo("All Coupons have been used"));

		Reporter.log("Use limit is applied");
	}


	public static ArrayList<String> getTbPassIds()
	{
		tbPassIds = given().
				baseUri("https://api.testbook.com/").
				when().
				get("/api/v2.1/students/coupon?coupon=ITJ7JBBCV&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English").
				then().
				log().ifError().
				extract().
				response().path("data.products._id");

		return tbPassIds;

	}
	
	@Test(priority = 7)
	public void checkGlobalCoupon()
	{
		SoftAssert softassert = new SoftAssert();
		getTbPassIds();

		String a = "5e86159ff60d5d03fdacda95";
		String b = "61a05c67e0d64cf74ece7dba";
		String c = "6070915f0ffdf94e3a865642";

		Response res = given().
				header("Authorization","Bearer "+token).
				when().
				get("/api/v2/student-coupons?prodIds={firstPass},{secondPass},{thirdPass}", a,b,c).
				then().
				log().ifError().
				extract().
				response();

		ArrayList <String>availableCoupons = res.path("data.coupons.code");

		for(int i=0;i<availableCoupons.size();i++)
		{
			String code = availableCoupons.get(i);
			Response res2 = given().
					header("Authorization", "Bearer "+token).
					when().
					get("api/v2.1/students/coupon?coupon={code}&client=android&for=&itemType=&itemId=&isSkillCourse=false&showAllResults=false&language=English",code).
					then().
					log().ifError().
					extract().
					response();

			int discountValue = res2.path("data.coupon.discountValue");
			int cost = res2.path("data.products[0].cost");
			int oldCost = res2.path("data.products[0].oldCost");
			//Assert.assertEquals(cost, oldCost-discountValue);
			softassert.assertEquals(cost, oldCost-discountValue);
		}
		softassert.assertAll();
	}

}
