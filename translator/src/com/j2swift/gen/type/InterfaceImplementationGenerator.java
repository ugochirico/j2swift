package com.j2swift.gen.type;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.base.Strings;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.MethodDeclaration;
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
		String className = typeName;
		printf("protocol %s ", className);
		printImplementedProtocols(false);

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
		indent();
		syncLineNumbers(m.getName()); // avoid doc-comment
		printIndent();
		print(getMethodSignature(m));
		unindent();
		newline();
		return;
	}
	
	@Override
	protected String getMethodSignature(MethodDeclaration m) {
		StringBuilder sb = new StringBuilder();
		IMethodBinding binding = m.getMethodBinding();
		// static
		if (Modifier.isStatic(m.getModifiers())) {
			sb.append("static ");
		}
		String returnType = nameTable.getObjCType(binding.getReturnType());
		String selector = binding.getName();

		sb.append("func ");

		if (selector.equals("hash")) {
			// Explicitly test hashCode() because of NSObject's hash return
			// value.
			returnType = "NSUInteger";
		}
		sb.append(selector);
		sb.append(printMethodParameter(m));
		if (binding.getExceptionTypes().length > 0) {
			sb.append(" throws");
		}
		if (!Strings.isNullOrEmpty(returnType) && !"void".equals(returnType)) {
			sb.append(" ->").append(returnType);
			ITypeBinding type = binding.getReturnType();
			if (!type.isPrimitive()) {
				sb.append("?");
			}
		}
		return sb.toString();
	}

}
