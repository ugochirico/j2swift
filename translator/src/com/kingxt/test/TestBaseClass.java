package com.kingxt.test;

public class TestBaseClass 
{
	public static final String testFinalField = "1";
	public String username;
	public String password;
	public int uId = 1000;
	public String info1, info2 = "1";
	
	public String test(String username, String password) {
		if (username.equals("123")) {
			return "123";
		}
		test1("my name");
		test2();
		
		String a = "23";
		String b, c, d;
		int aLength = a.length();
		return "";
	}

	public void test1(String username) {
	}
	
	public void test2() {
	}
	
//	func test2(a:String) {
//		
//	}
//	
//	test(a:"123");
}
