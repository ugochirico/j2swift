package com.j2swift.gen;

import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.collect.Lists;
//import com.google.devtools.j2objc.ast.EnumConstantDeclaration;
//import com.google.devtools.j2objc.ast.EnumDeclaration;
import com.j2swift.ast.*;
import com.j2swift.util.BindingUtil;

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
		} else if (!typeBinding.isInterface() || needsCompanionClass()) {
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
			
			VariablesDeclarationGenerator.generate(this.getBuilder(),
					this.typeNode);
			printStaticAccessors();
			printInnerDeclarations();
			// printAnnotationImplementation();
			// printInitializeMethod();
			// printReflectionMethods();
			newline();
			printIndent();
			println("}");
		}
		printOuterDeclarations();
		// printTypeLiteralImplementation();
	}

	private void printImplementedProtocols(boolean hasSuperClass) {
		List<String> interfaces = getInterfaceNames();
		if (!interfaces.isEmpty()) {
			boolean isFirst = !hasSuperClass;
			if (isFirst) {
				print(":");
			}
			for (String name : interfaces) {
				if (!isFirst) {
					print(", ");
				}
				isFirst = false;
				print(name);
			}
		}
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

	private void printStaticAccessors() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printFunctionDeclaration(FunctionDeclaration decl) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void printMethodDeclaration(MethodDeclaration m) {
		if (typeBinding.isInterface() || Modifier.isAbstract(m.getModifiers())) {
			return;
		}
		syncLineNumbers(m.getName()); // avoid doc-comment
		String methodBody = generateStatement(m.getBody());
		print(getMethodSignature(m) + " " + reindent(methodBody) + "\n");
		newline();
	}

	protected String generateStatement(Statement stmt) {
		return StatementGenerator.generate(stmt, getBuilder().getCurrentLine());
	}

	private String getSuperTypeName() {
		ITypeBinding superclass = typeBinding.getSuperclass();
		if (superclass == null) {
			return null;
		}
		return nameTable.getFullName(superclass);
	}

	@Override
	protected void printNativeDeclaration(NativeDeclaration decl) {
		// TODO Auto-generated method stub

	}

	protected String generateExpression(Expression expr) {
		return StatementGenerator.generate(expr, getBuilder().getCurrentLine());
	}

	@Override
	protected void printInnerEnumDeclaration(EnumDeclaration decl) {
		indent();
		TypeImplementationGenerator.generate(this.getBuilder(), decl);
		unindent();
	}

	@Override
	protected void printInnerTypeDeclaration(TypeDeclaration decl) {
		indent();
		TypeImplementationGenerator.generate(this.getBuilder(), decl);
		unindent();
	}

	private List<String> getInterfaceNames() {
		if (typeBinding.isAnnotation()) {
			return Lists.newArrayList("JavaLangAnnotationAnnotation");
		}
		List<String> names = Lists.newArrayList();
		for (ITypeBinding intrface : typeBinding.getInterfaces()) {
			names.add(nameTable.getFullName(intrface));
		}
		if (typeBinding.isEnum()) {
			names.remove("NSCopying");
			names.add(0, "NSCopying");
		} else if (isInterfaceType()) {
			names.add("NSObject");
			names.add("JavaObject");
		}
		return names;
	}
}
