package com.kingxt.j2swift.gen;

import com.kingxt.j2swift.ast.AbstractTypeDeclaration;
import com.kingxt.j2swift.ast.FunctionDeclaration;
import com.kingxt.j2swift.ast.MethodDeclaration;
import com.kingxt.j2swift.ast.NativeDeclaration;

public class TypeImplementationGenerator extends TypeGenerator {

	protected TypeImplementationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}

	public static void generate(SourceBuilder builder,
			AbstractTypeDeclaration typeNode) {
		new TypeImplementationGenerator(builder, typeNode).generate();
	}

	private void generate() {
		syncFilename(compilationUnit.getSourceFilePath());

	    if (!typeBinding.isInterface() || needsCompanionClass()) {
	      newline();
	      syncLineNumbers(typeNode.getName()); // avoid doc-comment
//	      printf("@implementation %s\n", typeName);
	      printProperties();
	      printStaticAccessors();
	      printInnerDeclarations();
//	      printAnnotationImplementation();
//	      printInitializeMethod();
//	      printReflectionMethods();
//	      println("\n@end");
	    }

	    printOuterDeclarations();
//	    printTypeLiteralImplementation();
	}

	private void printProperties() {
		// TODO Auto-generated method stub
		
	}

	private void printStaticAccessors() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void printFunctionDeclaration(FunctionDeclaration decl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void printMethodDeclaration(MethodDeclaration decl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void printNativeDeclaration(NativeDeclaration decl) {
		// TODO Auto-generated method stub
		
	}

}
