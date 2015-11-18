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

public class TestDylanClass implements DylanInterface {

	public TestDylanClass() {
		
	}
	
	public TestDylanClass(int i) {
		
	}
	
	@Override
	public void doTest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doTest(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public int doTest(String s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContactNamePinyin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIt() {
		// TODO Auto-generated method stub
		return null;
	}
}
