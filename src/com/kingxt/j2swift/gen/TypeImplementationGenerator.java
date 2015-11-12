package com.kingxt.j2swift.gen;

import org.eclipse.jdt.core.dom.IVariableBinding;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kingxt.j2swift.ast.AbstractTypeDeclaration;
import com.kingxt.j2swift.ast.BodyDeclaration;
import com.kingxt.j2swift.ast.FunctionDeclaration;
import com.kingxt.j2swift.ast.MethodDeclaration;
import com.kingxt.j2swift.ast.NativeDeclaration;
import com.kingxt.j2swift.ast.VariableDeclarationFragment;

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
			printf("class %s { \n", typeNode.getName());
			TypeDeclarationGenerator.generate(this.getBuilder(), this.typeNode);
			printStaticAccessors();
			printInnerDeclarations();
			// printAnnotationImplementation();
			// printInitializeMethod();
			// printReflectionMethods();
			println("\n}");
		}

		printOuterDeclarations();
		// printTypeLiteralImplementation();
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
