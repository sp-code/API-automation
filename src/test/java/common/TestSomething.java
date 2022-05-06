package common;

import org.testng.SkipException;
import org.testng.annotations.Test;
import org.hamcrest.core.IsNot;

import common.CheckStatus;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertNotEquals;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;

public class TestSomething {

	@Test
	public void test()
	{
		int a =0;
		
		System.out.println(a-25);
		
	}
}
