package com.kingxt.j2swift.gen;


public class SwiftHeaderGenerator extends SwiftSourceFileGenerator {

	protected SwiftHeaderGenerator(GenerationUnit unit) {
		super(unit);
	}

	public static void generate(GenerationUnit unit) {
		new SwiftHeaderGenerator(unit).generate();
	}

	private void generate() {
		generateFileHeader();

		for (GeneratedType generatedType : getOrderedTypes()) {
			printTypeDeclaration(generatedType);
		}

		generateFileFooter();
		save(getOutputPath());
	}

	private void printTypeDeclaration(GeneratedType generatedType) {
		print(generatedType.getPublicDeclarationCode());
	}

	/**
	 * Create import statements
	 */
	private void generateFileHeader() {
		printf("import Foundation");
	}

	protected void generateFileFooter() {
		newline();
	}
}
