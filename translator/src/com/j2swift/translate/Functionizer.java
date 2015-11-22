package com.j2swift.translate;

import java.lang.reflect.Modifier;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.j2swift.ast.AnnotationTypeDeclaration;
import com.j2swift.ast.BodyDeclaration;
import com.j2swift.ast.CompilationUnit;
import com.j2swift.ast.ConstructorInvocation;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.NativeStatement;
import com.j2swift.ast.NormalAnnotation;
import com.j2swift.ast.SingleMemberAnnotation;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.Statement;
import com.j2swift.ast.SuperConstructorInvocation;
import com.j2swift.ast.TreeNode;
import com.j2swift.ast.TreeNode.Kind;
import com.j2swift.ast.TreeUtil;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.ast.TypeDeclaration;
import com.j2swift.util.BindingUtil;

/**
 * Rewrite constructor to adapter swift
 * @author kingxt
 *
 */
public class Functionizer extends TreeVisitor {

	@Override
	public boolean visit(CompilationUnit node) {
		// functionizableMethods = determineFunctionizableMethods(node);
		return true;
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return false;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		IMethodBinding binding = node.getMethodBinding();
		// FunctionDeclaration function = null;
		List<BodyDeclaration> declarationList = TreeUtil
				.asDeclarationSublist(node);
		List<String> extraSelectors = nameTable.getExtraSelectors(binding);
		if (BindingUtil.isStatic(binding) || binding.isConstructor()
		/* || Modifier.isNative(node.getModifiers()) */
		/*
		 * || functionizableMethods.contains(binding) ||
		 * !extraSelectors.isEmpty()
		 */) {
			ITypeBinding declaringClass = binding.getDeclaringClass();
			// function = makeFunction(node);
			/*
			 * for (String selector : extraSelectors) {
			 * declarationList.add(makeExtraMethodDeclaration(node, selector));
			 * }
			 */
			// declarationList.add(function);
			if (binding.isConstructor()) {
				List<Statement> statements = node.getBody().getStatements();
				boolean isNeedAddSuperInit = false;
				Statement firstStatemnt = null;
				if (statements != null && statements.size() > 0) {
					firstStatemnt = statements.get(0);
					if (firstStatemnt.getKind() != Kind.SUPER_CONSTRUCTOR_INVOCATION
							&& firstStatemnt.getKind() != Kind.CONSTRUCTOR_INVOCATION) {
						isNeedAddSuperInit = true;
					}
					if (firstStatemnt.getKind() == Kind.CONSTRUCTOR_INVOCATION) {
						node.setConvenienceConstructor(true);
					}
				} else {
					isNeedAddSuperInit = true;
				}
				if (isNeedAddSuperInit) {
					SuperConstructorInvocation superInitStatement = new SuperConstructorInvocation(
							binding);
					statements.add(superInitStatement);
				}
				TreeNode parent = node.getParent();
				if (firstStatemnt != null && firstStatemnt.getKind() == Kind.CONSTRUCTOR_INVOCATION && parent instanceof TypeDeclaration) {
					if (!isConstructorIncludeInThisClass((TypeDeclaration)parent, (ConstructorInvocation)firstStatemnt)) {
						node.setConvenienceConstructor(false);
						ConstructorInvocation invocation = (ConstructorInvocation)firstStatemnt;
						SuperConstructorInvocation superInvocation = new SuperConstructorInvocation(invocation.getMethodBinding());
//						superInvocation.setArguments(invocation.getArguments());
//						firstStatemnt.replaceWith(superInvocation);
					} 
				}
			}
		}
		
		if (Modifier.isSynchronized(binding.getModifiers())) {
			List<Statement> statements = node.getBody().getStatements();
			NativeStatement syntheticInvocation = new NativeStatement(
					"objc_sync_enter(self)\n defer { objc_sync_exit(self) }\n");
			statements.add(0, syntheticInvocation);
		}
	}
	
	private boolean isConstructorIncludeInThisClass(TypeDeclaration node, ConstructorInvocation statement) {
		IMethodBinding statementMethodBinding = statement.getMethodBinding();
		for(BodyDeclaration declar : node.getBodyDeclarations()) {
			if (declar instanceof MethodDeclaration) {
				MethodDeclaration methodDeclaration = (MethodDeclaration)declar;
				if (methodDeclaration.isConstructor()) {
					IMethodBinding methodBinding = methodDeclaration.getMethodBinding();
					if (statementMethodBinding.isEqualTo(methodBinding)) {
						int i = 0;
						boolean sameFunction = true;
						for (ITypeBinding item : methodBinding.getParameterTypes())
						{
							if (!item.isEqualTo(statement.getArguments().get(0).getTypeBinding())) {
								sameFunction = false;
								break;
							}
							i++;
						}
						return sameFunction;
					}
				}
			}
		}
		return false;
	}
}
