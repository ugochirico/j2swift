package com.j2swift.test;

interface BaseInterface {

	/**
	 * @return
	 */
	String getIt();
}

interface DylanInterface extends BaseInterface {
	
	String getContactNamePinyin();

	public void doTest();

	public void doTest(int i);

	public int doTest(String s);
}

class TestDyalnBalseClass {
	public void methodTodo() {

	}

	TestDyalnBalseClass() {
		int i = 0;
	}

	TestDyalnBalseClass(int i) {

	}
}

class TestDylanOneClass extends TestDyalnBalseClass {
	int i = 0;

	public int k;
	
	public void methodTodo(int i) {
		this.k = 1;

	}

	public void methodTodo(String s, int i) {

	}

	TestDylanOneClass() {
		super();
		this.k = 2;
	}

	TestDylanOneClass(String s) {

	}

	TestDylanOneClass(String s, int i) {

	}

	public TestDylanOneClass(String s, int i, int j) {

	}
	
	public void logK() {
		System.out.println(this.k);
	}
}

class TestDylanClass extends TestDylanOneClass {
	
//	public int k;

	TestDylanClass() {
		super();
		this.k = 1;
	}

	TestDylanClass(String s) {
		// super();
	}
	
	public void testMethod() {
//		System.out.println("hello world");
	}
	
	public void testMethod(int i) {
//		System.out.println(i);
		
	}

	public String testMethod(String s) {
		return s;
	}
	
	public void logK() {
		super.logK();
		System.out.println(this.k);
	}
	
//	public static void main(String[] args) {
//		TestDylanClass c = new TestDylanClass();
//	}
}

