package com.kingxt.test;

public class TestBaseClass {
	public static final String testFinalField = "1";
	public String username;
	public String password;
	public int uId = 1000;
	public String info1, info2 = "1";

	public String test(String username, String password) {
		testForStatement();
		testIfStatement("22");
		testWhileStatement();

		int intShotType;
		// char cShotType;
		long longShotType;
		float floatShotType;
		double doubleShotType;
		byte byteShotType;
		short shortShotType;
		boolean booleanShotType;

		String a = "23";
		String b = null, c = "45", d = b;
		int aLength = a.length();

		a = b + c + d + "123";
		return "";
	}

	public String testForStatement() {
		for (int i = 0; i < 10; i = i + 1 * 2) {
		}
		return "testIfStatement";
	}

	public boolean testIfStatement(String condition) {
		if (condition.equals("22")) {
			return true;
		}
		return false;
	}

	public void testWhileStatement() {
		int i = 10;
		while (i > 0) {
			i--;
		}
	}

	public void testDoStatement() {
		int i = 10;
		do {
			i--;
		} while (i > 0);
	}
}
