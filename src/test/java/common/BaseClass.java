package common;
import  io.restassured.RestAssured.*;
import  io.restassured.matcher.RestAssuredMatchers.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import  org.hamcrest.Matchers.*;

import io.restassured.RestAssured;

public class BaseClass {

	String token;
	static Properties prop;
	static String projectPath;
	static InputStream input;
	
	
	public BaseClass() throws IOException
	{
		RestAssured.baseURI = "https://api.testbook.com";
		prop = new Properties();
		projectPath = System.getProperty("user.dir");
		input = new FileInputStream(projectPath+"/src/test/resources/config.properties");
		prop.load(input);
	}
	
	public String getToken()
	{
		token = prop.getProperty("token");
		return token;
	}
}
