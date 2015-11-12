package com.kingxt.j2swift.gen;


public class SwiftImplementationGenerator extends SwiftSourceFileGenerator {

	protected SwiftImplementationGenerator(GenerationUnit unit) {
		super(unit);
	}

	public static void generate(GenerationUnit unit) {
		new SwiftImplementationGenerator(unit).generate();
	}

	private void generate() {
	    printImports();
	    newline();
	    for (GeneratedType generatedType : getOrderedTypes()) {
	      print(generatedType.getPrivateDeclarationCode());
	    }
	    for (GeneratedType generatedType : getOrderedTypes()) {
	      print(generatedType.getImplementationCode());
	    }
	    save(getOutputPath());
	}

	/**
	 * Create import statements
	 */
	private void printImports() {
		printf("import Foundation");
	}

	protected void generateFileFooter() {
		newline();
	}
}
