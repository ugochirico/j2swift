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
		
		int intShotType;
//		char cShotType;
		long longShotType;
		float floatShotType;
		double doubleShotType;
		byte byteShotType;
		short shortShotType;
		boolean booleanShotType;
		
		String a = "23";
		String b = null, c = "45", d = b;
		int aLength = a.length();
		for (int i = 0; i < aLength; i = i+1*2) {
			b = c = d = String.valueOf(i);
		}
		a = b + c + d + "123";
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
