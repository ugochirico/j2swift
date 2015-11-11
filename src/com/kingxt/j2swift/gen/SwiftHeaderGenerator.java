package com.kingxt.j2swift.gen;


public class SwiftHeaderGenerator extends AbstractSourceGenerator {

	protected SwiftHeaderGenerator(SourceBuilder builder, GenerationUnit unit) {
		super(builder);
	}

	public SwiftHeaderGenerator(GenerationUnit unit) {
		super(new SourceBuilder(false));
	}

	public static void generate(GenerationUnit unit) {
		new SwiftHeaderGenerator(unit).generate();
	}

	private void generate() {
		// TODO Auto-generated method stub
		
	}

}
