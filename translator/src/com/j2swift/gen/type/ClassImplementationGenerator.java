package com.j2swift.gen.type;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.base.Strings;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.SingleVariableDeclaration;
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
		String className = getClassName();
		if (superClass != null) {
			printf("class %s : %s", className, superClass);
			hasSuperClass = true;
		} else {
			printf("class %s ", className);
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
		if (m.isConstructor()) {
			if (params.isEmpty() || params.size() == 0) {// for
															// "override init()"
				sb.append("override ");
			} else if (isConstructorOvrrided(this.typeBinding, m)) {
				sb.append("override ");
			}
		} else if (isMehothodOvrrided(this.typeBinding, m)) {
			sb.append("override ");
		}
		if (m.isConvenienceConstructor()) {
			sb.append("convenience ");
		}
		String returnType = nameTable.getObjCType(binding.getReturnType());
		String selector = binding.getName();
		if (m.isConstructor()) {
			returnType = null;
			selector = "init";
		} else {
			sb.append("func ");
		}

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

	@Override
	protected void printMethodDeclaration(MethodDeclaration m) {
		if (Modifier.isAbstract(m.getModifiers())) {
			return;
		}
		syncLineNumbers(m.getName()); // avoid doc-comment
		String methodBody = generateStatement(m.getBody());
		indent();
		printIndent();
		print(getMethodSignature(m) + reindent(methodBody) + "\n");
		unindent();
		newline();
	}

	private boolean isMehothodOvrrided(ITypeBinding binding, MethodDeclaration m) {
		ITypeBinding superClass = binding.getSuperclass();
		if (superClass == null) {
			return false;
		}
		// String... par
		String[] paramArray;
		paramArray = new String[] {};
		List<SingleVariableDeclaration> params = m.getParameters();
		if (params == null || params.size() <= 0) {

		} else {
			List<String> typeNames = new ArrayList<String>();
			for (int i = 0; i < params.size(); i++) {
				IVariableBinding var = params.get(i).getVariableBinding();
				String typeName = var.getType().getQualifiedName();
				typeNames.add(typeName);
			}
			paramArray = typeNames.toArray(new String[typeNames.size()]);
		}
		IMethodBinding overrideMethod = BindingUtil.findDeclaredMethod(
				superClass, m.getMethodBinding().getName(), paramArray);
		if (overrideMethod != null) {
			return true;
		}
		if (nameTable.getFullName(superClass).equals("JavaObject")) {
			return false;
		} else {
			return isMehothodOvrrided(superClass, m);
		}
	}

	private boolean isConstructorOvrrided(ITypeBinding binding,
			MethodDeclaration m) {
		ITypeBinding superClass = binding.getSuperclass();
		if (superClass == null) {
			return false;
		}
		List<SingleVariableDeclaration> params = m.getParameters();
		outer: for (IMethodBinding method : superClass.getDeclaredMethods()) {
			if (method.isConstructor()) {
				ITypeBinding[] foundParamTypes = method.getParameterTypes();
				if (foundParamTypes.length == params.size()) {
					for (int i = 0; i < foundParamTypes.length; i++) {
						String foundParam = foundParamTypes[i]
								.getQualifiedName();
						String paramName = params.get(i).getVariableBinding()
								.getType().getQualifiedName();
						if (!foundParam.equals(paramName)) {
							continue outer;
						}
					}
					return true;
				}
			}
		}
		if (nameTable.getFullName(superClass).equals("JavaObject")) {
			return false;
		} else {
			return isMehothodOvrrided(superClass, m);
		}
	}
}
