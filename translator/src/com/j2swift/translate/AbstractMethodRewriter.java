package com.j2swift.translate;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.AnnotationTypeDeclaration;
import com.j2swift.ast.Block;
import com.j2swift.ast.CompilationUnit;
import com.j2swift.ast.EnumDeclaration;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.NativeStatement;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.ast.TypeDeclaration;
import com.j2swift.util.BindingUtil;
import com.j2swift.util.TranslationUtil;

public class AbstractMethodRewriter extends TreeVisitor {
	private final CompilationUnit unit;

	public AbstractMethodRewriter(CompilationUnit unit) {
		this.unit = unit;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		if (Modifier.isAbstract(node.getModifiers())) {
			if (!TranslationUtil.needsReflection(node.getMethodBinding()
					.getDeclaringClass())) {
				unit.setHasIncompleteProtocol();
				unit.setHasIncompleteImplementation();
				return;
			}
			Block body = new Block();
			// Generate a body which throws a NSInvalidArgumentException.
			String bodyCode = "// can't call an abstract method\n"
					+ "assertionFailure(\"Cannot directly invoke the abstract method \")";
			ITypeBinding returnBinding = node.getReturnType().getTypeBinding();
			if (!BindingUtil.isVoid(returnBinding)) {
				if (returnBinding.isPrimitive()) {
					bodyCode += "\nreturn 0;"; // Never executes, but avoids a
												// gcc warning.
				} else {
					bodyCode += "\nreturn nil;";
				}
			}
			body.getStatements().add(new NativeStatement(bodyCode));
			node.setBody(body);
			node.removeModifiers(Modifier.ABSTRACT);
		}
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		visitType(node);
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		visitType(node);
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		visitType(node);
	}

	private void visitType(AbstractTypeDeclaration node) {
		ITypeBinding typeBinding = node.getTypeBinding();
		if (!Modifier.isAbstract(node.getModifiers()) && !typeBinding.isEnum()) {
			return;
		}
		// Find any interface methods that aren't defined by this abstract type
		// so
		// we can silence incomplete protocol errors.
		// Collect needed methods from this interface and all super-interfaces.
		Queue<ITypeBinding> interfaceQueue = new LinkedList<ITypeBinding>();
		Set<IMethodBinding> interfaceMethods = new LinkedHashSet<IMethodBinding>();
		interfaceQueue.addAll(Arrays.asList(typeBinding.getInterfaces()));
		ITypeBinding intrface;
		while ((intrface = interfaceQueue.poll()) != null) {
			interfaceMethods
					.addAll(Arrays.asList(intrface.getDeclaredMethods()));
			interfaceQueue.addAll(Arrays.asList(intrface.getInterfaces()));
		}

		// Check if any interface methods are missing from the implementation
		for (IMethodBinding interfaceMethod : interfaceMethods) {
			if (!isMethodImplemented(typeBinding, interfaceMethod)) {
				unit.setHasIncompleteProtocol();
			}
		}
	}

	private boolean isMethodImplemented(ITypeBinding type, IMethodBinding method) {
		if (type == null) {
			return false;
		}

		for (IMethodBinding m : type.getDeclaredMethods()) {
			if (method.isSubsignature(m)
					|| (method.getName().equals(m.getName())
							&& method.getReturnType().getErasure()
									.isEqualTo(m.getReturnType().getErasure()) && Arrays
								.equals(method.getParameterTypes(),
										m.getParameterTypes()))) {
				return true;
			}
		}

		return isMethodImplemented(type.getSuperclass(), method);
	}
}
