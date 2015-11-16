package com.kingxt.j2swift.gen;

import java.util.List;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;


//import com.google.devtools.j2objc.ast.EnumConstantDeclaration;
//import com.google.devtools.j2objc.ast.EnumDeclaration;
import com.kingxt.j2swift.ast.*;

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

		if (typeBinding.isEnum()) {//enum
			syncLineNumbers(typeNode.getName());
			printf("enum %s : Int {",typeNode.getName());
			printNativeEnum();
//			VariablesDeclarationGenerator.generate(this.getBuilder(),this.typeNode);
//			printInnerDeclarations();
			printf("}");
		} else if (!typeBinding.isInterface() || needsCompanionClass()) {
			newline();
			syncLineNumbers(typeNode.getName()); // avoid doc-comment
			String superClass = getSuperTypeName();
			if (superClass != null) {
				printf("class %s : %s {", typeNode.getName(), superClass);
			} else {
				printf("class %s { \n", typeNode.getName());
			}
			VariablesDeclarationGenerator.generate(this.getBuilder(),
					this.typeNode);
			printNativeEnum();
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

	private void printNativeEnum() {
		if (!(typeNode instanceof EnumDeclaration)) {
			return;
		}

		List<EnumConstantDeclaration> constants = ((EnumDeclaration) typeNode)
				.getEnumConstants();

		// Strip enum type suffix.
		String bareTypeName = typeName.endsWith("Enum") ? typeName.substring(0,
				typeName.length() - 4) : typeName;

		// C doesn't allow empty enum declarations. Java does, so we skip the
		// C enum declaration and generate the type declaration.
		if (!constants.isEmpty()) {
			newline();
//			printf("typedef NS_ENUM(NSUInteger, %s) {\n", bareTypeName);

			// Print C enum typedef.
			indent();
			int ordinal = 0;
			for (EnumConstantDeclaration constant : constants) {
				printIndent();
				printf("case %s_%s = %d\n", bareTypeName, constant.getName()
						.getIdentifier(), ordinal++);
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
		return superclass.getName();
	}

	@Override
	protected void printNativeDeclaration(NativeDeclaration decl) {
		// TODO Auto-generated method stub

	}

}
