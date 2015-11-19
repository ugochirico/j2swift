package com.j2swift.gen.type;

import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.base.Strings;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.EnumConstantDeclaration;
import com.j2swift.ast.EnumDeclaration;
import com.j2swift.ast.Expression;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.gen.SourceBuilder;

public class EnumImplementationGenerator extends DefaultImplementationGenerator {

	protected EnumImplementationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}

	public static void generate(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		new EnumImplementationGenerator(builder, node).generate();
	}

	@Override
	protected void generate() {
		printIndent();
		String enumExtendName = "";
		IMethodBinding[] declaredMethods = typeBinding.getDeclaredMethods();
		for (IMethodBinding declaredMethod : declaredMethods) {
			if (declaredMethod.isConstructor()) {
				ITypeBinding[] type = declaredMethod.getParameterTypes();
				if (type.length == 1) {
					enumExtendName = ": " + type[0].getName();
				}
			}
		}
		String bareTypeName = typeName.endsWith("Enum") ? typeName.substring(0,
				typeName.length() - 4) : typeName;
		printf("enum %s %s {", bareTypeName, enumExtendName);
		printNativeEnum();
		printIndent();
		printf("}");
		newline();
	}

	private void printNativeEnum() {
		if (!(typeNode instanceof EnumDeclaration)) {
			return;
		}

		List<EnumConstantDeclaration> constants = ((EnumDeclaration) typeNode)
				.getEnumConstants();

		if (!constants.isEmpty()) {
			newline();
			// Print C enum typedef.
			indent();
			for (EnumConstantDeclaration constant : constants) {
				printIndent();
				List<Expression> expressions = constant.getArguments();
				if (expressions != null && expressions.size() >= 1) {
					Expression e = expressions.get(0);
					printf("case %s = %s\n", constant.getName(),
							generateExpression(e));
				} else {
					printf("case %s\n", constant.getName());
				}
				// printf("case %s_%s = %d\n", bareTypeName, constant.getName()
				// .getIdentifier(), 1);
			}
			unindent();
		}
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

	protected String getMethodSignature(MethodDeclaration m) {
		StringBuilder sb = new StringBuilder();
		IMethodBinding binding = m.getMethodBinding();

		// public
		if (Modifier.isPublic(m.getModifiers())) {
			sb.append("public ");
		}
		// static
		if (Modifier.isStatic(m.getModifiers())) {
			sb.append("static ");
		}

		List<SingleVariableDeclaration> params = m.getParameters();

		String returnType = nameTable.getObjCType(binding.getReturnType());
		String selector = binding.getName();

		sb.append("func ");

		if (selector.equals("hash")) {
			// Explicitly test hashCode() because of NSObject's hash return
			// value.
			returnType = "NSUInteger";
		}
		sb.append(selector);

		if (params.isEmpty() || params.size() == 0) {
			sb.append("()");
		} else {
			for (int i = 0; i < params.size(); i++) {
				if (i == 0) {
					sb.append("(_ ");
				}
				if (i != 0) {
					sb.append(", _ ");
				}
				IVariableBinding var = params.get(i).getVariableBinding();
				String typeName = nameTable.getSpecificObjCType(var.getType());
				sb.append(String.format("%s:%s?",
						nameTable.getVariableShortName(var), typeName));
				if (i == params.size() - 1) {
					sb.append(")");
				}
			}
		}
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
