package com.j2swift.translate;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.common.collect.Sets;
import com.j2swift.ast.AnnotationTypeDeclaration;
import com.j2swift.ast.BodyDeclaration;
import com.j2swift.ast.CompilationUnit;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.MethodInvocation;
import com.j2swift.ast.NormalAnnotation;
import com.j2swift.ast.SingleMemberAnnotation;
import com.j2swift.ast.Statement;
import com.j2swift.ast.SuperMethodInvocation;
import com.j2swift.ast.SuperConstructorInvocation;
import com.j2swift.ast.TreeNode.Kind;
import com.j2swift.ast.TreeUtil;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.util.BindingUtil;

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
		/*|| functionizableMethods.contains(binding) || !extraSelectors.isEmpty()*/) {
			ITypeBinding declaringClass = binding.getDeclaringClass();
			// function = makeFunction(node);
			/*
			 * for (String selector : extraSelectors) {
			 * declarationList.add(makeExtraMethodDeclaration(node, selector));
			 * }
			 */
			// declarationList.add(function);
			if (binding.isConstructor()
					&& !BindingUtil.isAbstract(declaringClass)) {
				List<Statement> statements = node.getBody().getStatements();
				boolean isNeedAddSuperInit = false;
				if (statements != null && statements.size() > 0) {
					Statement firstStatemnt = statements.get(0);
					if (firstStatemnt.getKind() != Kind.SUPER_CONSTRUCTOR_INVOCATION) {
						isNeedAddSuperInit = true;
					}
				} else {
					isNeedAddSuperInit = true;
				}
				if (isNeedAddSuperInit) {
					SuperConstructorInvocation superInitStatement = new SuperConstructorInvocation(binding);
					statements.add(superInitStatement);
				}
			}
		}
	}
}
