package com.j2swift.gen.type;

import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.gen.SourceBuilder;
import com.j2swift.gen.VariablesDeclarationGenerator;
import com.j2swift.util.BindingUtil;

public class ClassImplementationGenerator extends
		DefaultImplementationGenerator {

	protected ClassImplementationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}

	public static void generate(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		new ClassImplementationGenerator(builder, node).generate();
	}

	@Override
	protected void generate() {
		newline();
		printIndent();
		if (BindingUtil.isPublic(this.typeNode.getTypeBinding())) {
			print("public ");
		}
		String superClass = getSuperTypeName();
		boolean hasSuperClass = false;
		if (superClass != null) {
			printf("class %s : %s", typeName, superClass);
			hasSuperClass = true;
		} else {
			printf("class %s ", typeName);
		}
		printImplementedProtocols(hasSuperClass);

		printf(" {\n");

		VariablesDeclarationGenerator
				.generate(this.getBuilder(), this.typeNode);
		// printStaticAccessors();
		printInnerDeclarations();
		// printAnnotationImplementation();
		// printInitializeMethod();
		// printReflectionMethods();
		newline();
		printIndent();
		println("}");
	}
}
