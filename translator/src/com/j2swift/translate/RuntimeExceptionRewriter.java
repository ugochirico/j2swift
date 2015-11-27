package com.j2swift.translate;

import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.ThrowStatement;
import com.j2swift.ast.TreeVisitor;

public class RuntimeExceptionRewriter extends TreeVisitor {

	@Override
	public boolean visit(final MethodDeclaration methodNode) {
		if (methodNode.getMethodBinding().getExceptionTypes().length == 0) {
			methodNode.accept(new TreeVisitor() {
				@Override
				public boolean visit(ThrowStatement node) {
					methodNode.setThrowsRuntimeExeception(true);
					return false;
				}
			});
		}
		return false;
	}
}
