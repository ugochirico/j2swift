package com.j2swift.test;

public class TestGeneric<T> {

	public int find(T another) { 
		return 0;	
	}
	
	public static void main(String[] args) {
		A a = new B();
		a.doTest(new Object());
	}
}

class A {
	public void doTest(Object o) {
		System.out.println("aaaaa  "+o);
	}
}

class B extends A {
	public void doTest(String s) {
		System.out.println("bbbbbb  "+s);
	}
}
