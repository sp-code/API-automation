package common;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

 public class CheckStatus {
	
	public CheckStatus() throws IOException
	{
		BaseClass bs = new BaseClass();
	}
	 
	public void code200(String bpath)
	{
		//given().
	//	baseUri(buri).
		when().get(bpath).
		then().
		assertThat().
		log().ifError().
		statusCode(200);
	}
	public void code200(String bpath,String param)
	{
		//given().
	//	baseUri(buri).
		when().get(bpath+param).
		then().
		assertThat().
		log().ifError().
		statusCode(200);
	}
}
