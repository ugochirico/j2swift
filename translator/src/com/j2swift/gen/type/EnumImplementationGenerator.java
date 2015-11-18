package com.j2swift.gen.type;

import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.EnumConstantDeclaration;
import com.j2swift.ast.EnumDeclaration;
import com.j2swift.ast.Expression;
import com.j2swift.gen.SourceBuilder;

public class EnumImplementationGenerator extends DefaultImplementationGenerator {

	protected EnumImplementationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}

	public static void generate(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		new ClassImplementationGenerator(builder, node).generate();
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
		String bareTypeName = typeName.endsWith("Enum") ? typeName
				.substring(0, typeName.length() - 4) : typeName;
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
}
