package com.j2swift.test;

public class TestClassType {
	
	public TestClassType() {
		
	}
	
	public TestClassType(String a) {
		
	}

	public void test() {
		Class clazz = TestClassType.class;
		TestClassType test = new TestClassType("123");
		test.getClass();
	}
}
