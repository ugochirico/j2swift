package com.j2swift.test;

public class TestClassType {
	
	public TestClassType() {
		
	}
	
	public TestClassType(String a) throws RuntimeException {
		throw new RuntimeException("");
	}

	public void test() {
		Class clazz = TestClassType.class;
		TestClassType test = new TestClassType("123");
		test.getClass();
	}
}
