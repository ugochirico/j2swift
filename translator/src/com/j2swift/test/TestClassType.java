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
	
	public static void main(String[] args) {
		A a = new A();
		a.a("");
		B b = new B();
		b.a("");
		A c = new B(); 
		c.a(new String());
		
		float d = 123;
	}
}

class A {
	
	private int i;
	
	public A() {
		super();
		int i = 0;
	}
	
	public void a(String b) {
		System.out.println("a");
	}
}

class B extends A {
	
	public void a(Object c) {
		System.out.println("b");
	}	
}

