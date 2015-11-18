package com.j2swift.gen.type;

import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.EnumDeclaration;
import com.j2swift.ast.Expression;
import com.j2swift.ast.FunctionDeclaration;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.NativeDeclaration;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.Statement;
import com.j2swift.ast.TypeDeclaration;
import com.j2swift.gen.SourceBuilder;
import com.j2swift.gen.StatementGenerator;
import com.j2swift.gen.TypeGenerator;
import com.j2swift.gen.TypeImplementationGenerator;

public class DefaultImplementationGenerator extends TypeGenerator {

	protected DefaultImplementationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}

	protected void generate() {

	}

	protected void printImplementedProtocols(boolean hasSuperClass) {
		List<String> interfaces = getInterfaceNames();
		if (!interfaces.isEmpty()) {
			boolean isFirst = !hasSuperClass;
			if (isFirst) {
				print(": ");
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
			// names.add("NSObject");
			// names.add("JavaObject");
		}
		return names;
	}

	protected String generateStatement(Statement stmt) {
		return StatementGenerator.generate(stmt, getBuilder().getCurrentLine());
	}

	@Override
	protected void printMethodDeclaration(MethodDeclaration m) {
//		if (typeBinding.isInterface()) {
//			indent();
//			syncLineNumbers(m.getName()); // avoid doc-comment
//			printIndent();
//			print(getMethodSignature(m));
//			unindent();
//			newline();
//			return;
//		}
//		if (Modifier.isAbstract(m.getModifiers())) {
//			return;
//		}
//		syncLineNumbers(m.getName()); // avoid doc-comment
//		String methodBody = generateStatement(m.getBody());
//		print(getMethodSignature(m) + " " + reindent(methodBody) + "\n");
//		newline();
	}
	
	/**
	 * Create an Objective-C method signature string.
	 */
	protected String getMethodSignature(MethodDeclaration m) {
		StringBuilder sb = new StringBuilder();
		IMethodBinding binding = m.getMethodBinding();
		String prefix = Modifier.isStatic(m.getModifiers()) ? "static " : "";
		String returnType = nameTable.getObjCType(binding.getReturnType());
		String selector = binding.getName();
		if (m.isConstructor()) {
			returnType = null;
			selector = "init";
		} else if (selector.equals("hash")) {
			// Explicitly test hashCode() because of NSObject's hash return
			// value.
			returnType = "NSUInteger";
		}
		sb.append(String.format("%sfunc %s", prefix, selector));

		List<SingleVariableDeclaration> params = m.getParameters();
		if (params.isEmpty() || params.size() == 0) {
			sb.append("()");
		} else {
			for (int i = 0; i < params.size(); i++) {
				if (i == 0) {
					sb.append("(");
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
		if (!Strings.isNullOrEmpty(returnType) && !"void".equals(returnType)) {
			sb.append(" ->").append(returnType);
			ITypeBinding type = binding.getReturnType();
			if (!type.isPrimitive()) {
				sb.append("?");
			}
		}
		return sb.toString();
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

	@Override
	protected void printFunctionDeclaration(FunctionDeclaration decl) {
		// TODO Auto-generated method stub

	}
}
