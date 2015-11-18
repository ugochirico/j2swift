package com.j2swift.gen;

import com.j2swift.ast.*;
import com.j2swift.gen.type.ClassImplementationGenerator;
import com.j2swift.gen.type.EnumImplementationGenerator;
import com.j2swift.gen.type.InterfaceImplementationGenerator;

/**
 * Generator java implement to Swift
 * 
 * @author xutao1
 *
 */
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
		if (typeBinding.isEnum()) {// enum
			EnumImplementationGenerator.generate(this.getBuilder(),
					this.typeNode);
		} else if (typeBinding.isInterface()) {//interface
			InterfaceImplementationGenerator.generate(this.getBuilder(),
					this.typeNode);
		} else if (typeBinding.isClass()) {//class
			/* for Objc use 
			 * if (!typeBinding.isInterface() || needsCompanionClass()) {
			 * */
			ClassImplementationGenerator.generate(this.getBuilder(),
					this.typeNode);
		}
		printOuterDeclarations();
		// printTypeLiteralImplementation();
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

	@Override
	protected void printInnerEnumDeclaration(EnumDeclaration decl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printInnerTypeDeclaration(TypeDeclaration decl) {
		// TODO Auto-generated method stub

	}
}
