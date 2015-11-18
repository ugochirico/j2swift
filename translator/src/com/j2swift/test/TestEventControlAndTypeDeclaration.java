package com.j2swift.test;

enum Color {
	Red, Black
}

public class TestEventControlAndTypeDeclaration {
	public static final String testFinalField = "1";
	public String username;
	public String password;
	public int uId = 1000;
	public String info1, info2 = "1";

	public String testForStatement() {
		for (int i = 0; i < 10; i = i + 1 * 2) {
		}
		{
			int a = 4;
		}
		{
			int a = 5;
		}
		return "testForStatement";
	}
	
	public String testForStatement2() {
		String [][]a1 = new String[][]{{"1", "2"}};
 		String []a2 = new String[]{"1", "2", "3"};
		for (String item : a2) {
		}
		return "testForStatement2";
	}

	public void test2SwitchStatement() {
		int color = 1;
		int b = 4;
		switch (color) {
		case 1: {
			{
				b += 5;
			}
			b += 5;
		}
			break;
		default:
			break;
		}
	}

	public void testSwitchStatement() {
		Color color = Color.Red;
		switch (color) {
		case Red:
			break;
		case Black:
			break;

		default:
			break;
		}
	}

	public String test(String username, String password) {
		testForStatement();
		testIfStatement("22");
		testWhileStatement();

		int intShotType;
		char cShotType;
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
