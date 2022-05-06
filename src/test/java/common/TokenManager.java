package common;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TokenManager {

	static String token;
	static Properties prop;
	static String projectPath;
	static InputStream input;
	
	public static void loginToken() throws IOException
	{
		prop = new Properties();
		projectPath = System.getProperty("user.dir");
		input = new FileInputStream(projectPath+"/src/test/resources/config.properties");
		prop.load(input);
		token = prop.getProperty("token");
		
		String logintoken;
		logintoken = given().
		baseUri("https://api.testbook.com").
		//header("Authorization","Bearer "+getToken()).
		when().
		post("/api/v1/otp/login?emailOrMobile="+prop.getProperty("mobile")+"&otp="+prop.getProperty("otp")+"&otpSMS=&aaid=b041614a-69d7-4bc8-8df0-64b478543be2").
		then().
		extract().response().path("data.token");
		System.out.println(logintoken);
		
	}
	
	
	public static void main(String args[]) throws IOException 
	{
		loginToken();
	}

}
