package com.j2swift.test;

//interface BaseInterface {
//
//	/**
//	 * @return
//	 */
//	String getIt();
//}
//
//interface DylanInterface extends BaseInterface {
//	
//	String getContactNamePinyin();
//
//	public void doTest();
//
//	public void doTest(int i);
//
//	public int doTest(String s);
//}

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

	public void methodTodo(int i) {

	}

	public void methodTodo(String s, int i) {

	}

	TestDylanOneClass() {
		super();
		i = 1;
		int j = 2;
	}

	TestDylanOneClass(String s) {

	}

	TestDylanOneClass(String s, int i) {

	}

	public TestDylanOneClass(String s, int i, int j) {

	}
}

class TestDylanClass extends TestDylanOneClass {

	TestDylanClass() {
		super("sdf", 1);
	}

	TestDylanClass(String s) {
		// super();
	}
	
	public void testMethod() {
		System.out.println("hello world");
	}
	
	public void testMethod(int i) {
		System.out.println(i);
		
	}

	public String testMethod(String s) {
		return s;
	}
	// TestDylanClass(int i) {
	// System.out.println(123);
	// }
	//
	// TestDylanClass(String s) {
	//
	// }
	//
	// @Override
	// public void methodTodo() {
	// // TODO Auto-generated method stub
	// super.methodTodo();
	// }
	//
	// @Override
	// public void methodTodo(int i) {
	// // TODO Auto-generated method stub
	// // super.methodTodo(i);
	// }
	//
	// public void methodTodo(int i, String s) {
	//
	// }
	//
	// public static void main(String[] args) {
	// TestDylanClass c = new TestDylanClass();
	// System.out.println(c.i);
	// }
}