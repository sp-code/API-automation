package coupons;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.ArrayList;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import common.BaseClass;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TbSelectCoupons {

	String token;
	static ArrayList <String>tsgId;
	ArrayList <String>pid = new ArrayList<String>();

	@BeforeClass
	public void beforeClass() throws IOException
	{
		BaseClass obj = new BaseClass(); 
		token = obj.getToken();
	}

	public static void getAllSuperGroup()
	{
		tsgId = given().
				when().
				get("api/v1/classes/tsg?isSkillCourse=false&language=English").
				then().
				log().ifError().
				extract().body().path("data.tsgId");
		
		System.out.println("List of availble Target Super group"+tsgId);
	}

	@Test(priority = 1)
	public void getProductId()
	{
		getAllSuperGroup();
		for(int i=0;i<tsgId.size();i++)
		{
			String s = tsgId.get(i);

			ArrayList <String>id = given().
					when().
					get("api/v1/tsg/{id}/classes?skip=0&classType=Both&limit=0&language=English",s).
					then().
					log().ifError().
					extract().response().path("data.products._id");

			for(int j=0;j<id.size();j++)
			{
				pid.add(id.get(j));
			} 

		}
		
		System.out.print("List of availble Products "+pid);
	}

	@Test(priority=2)
	public void checkUseLimit()
	{
		SoftAssert softassert = new SoftAssert();
		for(int i=0;i<pid.size();i++)
		{
			String id = pid.get(i);
			Response res = 	given().
					when().
					get("api/v2.1/students/coupon?coupon=SRGTN3A4O&client=android&for=select&itemType=Select&itemId={pid}&isSkillCourse=false&showAllResults=false&language=English",id).
					then().
					extract().response();

			String errorMesaage = res.path("message");

			String message = "All Coupons have been used";

			//assertThat(errorMesaage, equalTo(message));
			softassert.assertEquals(errorMesaage,message,id);
		}
		softassert.assertAll();
	}

	@Test(priority=3)
	public void checkPercentDiscountCoupon()
	{
		SoftAssert softassert = new SoftAssert();
		for(int i=0;i<pid.size();i++)
		{
			String id = pid.get(i);
			Response res = 	given().
					when().
					get("api/v2.1/students/coupon?coupon=PQHR6CPO0&client=android&for=select&itemType=Select&itemId={pid}&isSkillCourse=false&showAllResults=false",id).
					then().
					log().ifError().
					extract().response();

			int oldCost = res.path("data.products[0].oldCost");
			int cost = res.path("data.products[0].cost");
			int minCost = res.path("data.products[0].minCost");
			int discountValue = res.path("data.coupon.discountValue");

			double value = (double)discountValue/100; //10%

			value = value*oldCost;

			int newCost = (int)(oldCost-value);

			if(newCost<minCost)
			{
				newCost = minCost;
				//assertThat(newCost, equalTo(cost));
				softassert.assertEquals(newCost,cost,id);
				continue;
			}

			if(oldCost%10!=0)
			{
				//assertThat(newCost+1, equalTo(cost));	
				softassert.assertEquals(newCost+1,cost,id);
			}
			else
			{
				//assertThat(newCost, equalTo(cost));
				softassert.assertEquals(newCost,cost,id);
			}
		}	
		softassert.assertAll();
	} 

	@Test(priority=4)
	public void checkCashDiscountCoupon()
	{
		SoftAssert softassert = new SoftAssert();
		int discountValue = 0;
		for(int i=0;i<pid.size();i++)
		{
			String id = pid.get(i);
			Response res = 	given().
					when().
					get("api/v2.1/students/coupon?coupon=LDSB99SYE&client=android&for=select&itemType=Select&itemId={pid}&isSkillCourse=false&showAllResults=false&language=English",id).
					then().
					log().ifError().
					extract().response();

			int cost = res.path("data.products[0].cost");
			int oldCost = res.path("data.products[0].oldCost");
			int minCost = res.path("data.products[0].minCost");
			int newCost;

			if(cost==0)
			{
				discountValue = 0;
			}
			else
			{
				discountValue = res.path("data.coupon.discountValue"); //90
			}

			newCost = oldCost - discountValue;

			if(newCost<minCost)
			{
				newCost = minCost;
				//assertThat(newCost, equalTo(cost));
				softassert.assertEquals(newCost,cost,id);
				continue;
			}

			//assertThat(newCost, equalTo(cost)); 
			softassert.assertEquals(newCost,cost,id);
		}
		softassert.assertAll();
	}

	@Test(priority=5)
	public void fullDiscountCoupon()
	{
		SoftAssert softassert = new SoftAssert();
		for(int i=0;i<pid.size();i++)
		{
			String id = pid.get(i);
			Response res = 	given().
					when().
					get("api/v2.1/students/coupon?coupon=M65MUJ9K8&client=android&for=select&itemType=Select&itemId={pid}&isSkillCourse=false&showAllResults=false&language=English",id).
					then().
					log().ifError().
					extract().response();

			int cost = res.path("data.products[0].cost");

			int newCost = 0;
			softassert.assertEquals(newCost,cost,id);
			//assertThat(newCost, equalTo(cost)); 
		}
		softassert.assertAll();
		Reporter.log("Full discount is applicable");
	}
 
	@Test(priority=6)
	public void checkGlobalOrDynamicCoupon()
	{
		SoftAssert softassert = new SoftAssert();
		ArrayList<String> availableCoupons = new ArrayList<String>();
		for(int i=0;i<pid.size();i++)
		{
			String id = pid.get(i);
			availableCoupons = given().
					header("Authorization", "Bearer "+token).
					when().
					get("api/v2/student-coupons?prodIds={pid}&isSkillCourse=false&showAllResults=false&language=English",id).
					then().
					log().ifError().
					extract().
					response().
					path("data.coupons.code");

			//System.out.println(availableCoupons);
			
			for(int j=0;j<availableCoupons.size();j++)
			{
				String code = availableCoupons.get(j);
				Response res =given().
						header("Authorization", "Bearer "+token).
						when().
						get("api/v2.1/students/coupon?coupon={coupon_code}&client=android&for=&itemType=course&itemId={pid}&isSkillCourse=false&showAllResults=false&language=English",code,id).
						then().
						log().ifError().
						extract().
						response();

				int discountValue = res.path("data.coupon.discountValue");
				int oldCost = res.path("data.products[0].oldCost");
				int cost = res.path("data.products[0].cost");
				int minCost = res.path("data.products[0].minCost");
				// Need to add Cash coupon logic
				double value = (double)discountValue/100;
				
				value = value*oldCost;

				int newCost = (int)(oldCost-value);

				if(newCost<minCost)
				{
					newCost = minCost;
					softassert.assertEquals(newCost,cost,id);
					//assertThat(newCost, equalTo(cost));
					continue;
				}

				if(oldCost%10!=0)
				{
					softassert.assertEquals(newCost+1,cost,id);
					//assertThat(newCost+1, equalTo(cost));
				}
				else
				{
					softassert.assertEquals(newCost,cost,id);
					//assertThat(newCost, equalTo(cost));
				}

			}

		}
		softassert.assertAll();
		Reporter.log("Discount for Global & Dynamic coupon is correctly applicable"+availableCoupons);
	}
}
