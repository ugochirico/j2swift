package com.j2swift.test;

public class TestClassType {

	private final int abc;
	
	private final  Integer abcd[];
	
	private final Object obj = null;
	
    public static final char MIN_VALUE = '\u0000';
	
	private static final String c;
	
	static {
		c = "";
	}
	
	public TestClassType() {
		System.out.println(123);
		abc = 3;
		abcd = null;
	}
	
	public void abc(String a) {
				String b = a;
		a ="123";
	}
	public static void main(String[] args) {
		System.out.println("1 = " + Integer.valueOf(MIN_VALUE));
		long a = 5;
		float b = 5.1f;
		System.out.println((float)a == b);
	}
	

	public RuntimeException test1() {
		return null;
	}
	
	public void test2() {
		throw test1();
	}
}
