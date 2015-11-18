package com.j2swift.gen.type;

import org.eclipse.jdt.core.dom.Modifier;

import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.MethodDeclaration;
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
	
	@Override
	protected void printMethodDeclaration(MethodDeclaration m) {
		if (Modifier.isAbstract(m.getModifiers())) {
			return;
		}
		syncLineNumbers(m.getName()); // avoid doc-comment
		String methodBody = generateStatement(m.getBody());
		print(getMethodSignature(m) + " " + reindent(methodBody) + "\n");
		newline();
	}
}
