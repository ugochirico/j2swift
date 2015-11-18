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
		
	}
	
	TestDyalnBalseClass(int i) {
		
	}
}

class TestDylanOneClass extends TestDyalnBalseClass {
	public void methodTodo(int i) {

	}

	public void methodTodo(String s, int i) {

	}
	
	TestDylanOneClass() {
		
	}
	
	TestDylanOneClass(String s) {
		
	}
}

class TestDylanClass extends TestDylanOneClass {
	
	TestDylanClass() {
		
	}
	
	TestDylanClass(int i) {
		
	}
	
	TestDylanClass(String s) {
		
	}

	@Override
	public void methodTodo() {
		// TODO Auto-generated method stub
		super.methodTodo();
	}

	@Override
	public void methodTodo(int i) {
		// TODO Auto-generated method stub
//		super.methodTodo(i);
	}

	public void methodTodo(int i, String s) {

	}
}
