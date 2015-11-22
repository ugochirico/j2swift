package com.j2swift.test;

interface BaseInterface {

	/**
	 * @return
	 */
	String getIt();
}

abstract class TestBaseClass {
	public abstract void testMethod();
	public abstract String testMehodString();
	protected abstract int testMethodInt();
}


class TestDylanClass extends TestBaseClass  {
	static final long serialVersionUID = -7034897190745766939L;
	
	private int i;
	
	TestDylanClass() {
		i = 10;
	}

	
	
	void logK() {
		BaseInterface b = new BaseInterface() {
			
			@Override
			public String getIt() {
				System.out.println(i);
				i = 11;
				return null;
			}
		};
		
		i = 12;
		
		b.getIt();
//		System.out.println(i);
	}
	
	void doTest(String s) {
		s = "qweqwe";
//		System.out.println(s);
	}
	
	public static void main(String[] args) {
		TestDylanClass c = new TestDylanClass();
		c.logK();
		String s = "123";
		c.doTest(s);
//		System.out.println(s);
	}



	@Override
	public void testMethod() {
		// TODO Auto-generated method stub
	}



	@Override
	public String testMehodString() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected int testMethodInt() {
		// TODO Auto-generated method stub
		return 0;
	}
}

