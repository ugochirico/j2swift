package com.j2swift.translate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.j2swift.ast.Assignment;
import com.j2swift.ast.Expression;
import com.j2swift.ast.FunctionInvocation;
import com.j2swift.ast.InfixExpression;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.SimpleName;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.ast.VariableDeclarationFragment;
import com.j2swift.types.FunctionBinding;
import com.j2swift.util.BindingUtil;

public class FinalParameterRewriter extends TreeVisitor {

	private List<SingleVariableDeclaration> parameteList = new ArrayList<SingleVariableDeclaration>();

	@Override
	public boolean visit(MethodDeclaration node) {
		parameteList.addAll(node.getParameters());
		return true;
	}
	
	@Override
	public void endVisit(InfixExpression node) {
		InfixExpression.Operator op = node.getOperator();
		Iterator<Expression> operandIter = node.getOperands().iterator();
		Expression leftOperand = operandIter.next();
		System.out.println(1);
	}
	
	@Override
	public void endVisit(Assignment node) {
		ITypeBinding typeBinding = node.getLeftHandSide().getTypeBinding();
		Expression exp = node.getLeftHandSide();
		if (exp instanceof SimpleName) {
			SimpleName simpleName = (SimpleName)exp;
			if (simpleName.getBinding() instanceof IVariableBinding) {
				setVariableBindingDeclarationModified((IVariableBinding)simpleName.getBinding());
			}
		}
	System.out.println(1);
	}
	
	private void setVariableBindingDeclarationModified(IVariableBinding variableBinding) {
		for (SingleVariableDeclaration declaration : parameteList) {
			if (declaration.getVariableBinding() == variableBinding) {
				declaration.setFinalDeclaration(false);
				break;
			}
		}
	}

}
