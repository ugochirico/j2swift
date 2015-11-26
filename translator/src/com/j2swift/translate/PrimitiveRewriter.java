package com.j2swift.translate;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.j2swift.ast.BooleanLiteral;
import com.j2swift.ast.Expression;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.MethodInvocation;
import com.j2swift.ast.NumberLiteral;
import com.j2swift.ast.ReturnStatement;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.ast.VariableDeclarationFragment;
import com.j2swift.util.BindingUtil;

/**
 * PrimitiveRewriter will rewrite all primitive uninitial value to java default
 * value
 * 
 * @author kingxt
 *
 */
public class PrimitiveRewriter extends TreeVisitor {
	
	ITypeBinding methodReturnTypeBinding; 
			
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		IVariableBinding binding = node.getVariableBinding();
		if (BindingUtil.isPrimitive(binding) && !BindingUtil.isFinal(binding)) {
			Expression initializer = node.getInitializer();
			if (initializer == null) {
				char type = binding.getType().getBinaryName().charAt(0);
				System.out.println("shot type is = " + type);
				if (type == 'I' || type == 'S' || type == 'B') {
					NumberLiteral number = NumberLiteral.newIntLiteral(0, typeEnv); 
					node.setInitializer(number);
				} else if (type == 'C') {
					NumberLiteral number = new NumberLiteral(0, typeEnv); 
					node.setInitializer(number);
				} if (type == 'J') {
					NumberLiteral number = new NumberLiteral(new Long(0), typeEnv);
					node.setInitializer(number);
				} if (type == 'F') {
					NumberLiteral number = new NumberLiteral(new Float(0.0), typeEnv);
					node.setInitializer(number);
				} if (type == 'D') {
					NumberLiteral number = new NumberLiteral(new Double(0.0), typeEnv); 
					node.setInitializer(number);
				} if (type == 'Z') {
					BooleanLiteral number = new BooleanLiteral(false, typeEnv); 
					node.setInitializer(number);
				}
				System.out.println(node);
			}
		}
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		if (node.getReturnType() != null){
			methodReturnTypeBinding = node.getReturnType().getTypeBinding();
		} else {
			methodReturnTypeBinding = null;
		}
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		ITypeBinding returnTypeBinding = node.getExpression().getTypeBinding();
		if (returnTypeBinding != null) {
			if (returnTypeBinding.isEqualTo(methodReturnTypeBinding)) {
				//Need cast
			}
		}
		return false;
	}
}
