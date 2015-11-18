package com.j2swift.gen.type;

import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.gen.SourceBuilder;
import com.j2swift.gen.VariablesDeclarationGenerator;
import com.j2swift.util.BindingUtil;

public class InterfaceImplementationGenerator extends
		DefaultImplementationGenerator {

	protected InterfaceImplementationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}
	
	public static void generate(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		new InterfaceImplementationGenerator(builder, node).generate();
	}
	
	@Override
	protected void generate() {
		newline();
		printIndent();
		if (BindingUtil.isPublic(this.typeNode.getTypeBinding())) {
			print("public ");
		}
		printf("protocol %s ", typeName);
		printImplementedProtocols(false);

		printf(" {\n");

		VariablesDeclarationGenerator.generate(this.getBuilder(),
				this.typeNode);
//		printStaticAccessors();
		printInnerDeclarations();
		// printAnnotationImplementation();
		// printInitializeMethod();
		// printReflectionMethods();
		newline();
		printIndent();
		println("}");
	} 
}
