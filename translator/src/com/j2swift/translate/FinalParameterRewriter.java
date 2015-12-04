package com.j2swift.translate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IVariableBinding;

import com.j2swift.ast.ArrayAccess;
import com.j2swift.ast.Assignment;
import com.j2swift.ast.Expression;
import com.j2swift.ast.InfixExpression;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.PostfixExpression;
import com.j2swift.ast.PrefixExpression;
import com.j2swift.ast.PrefixExpression.Operator;
import com.j2swift.ast.SimpleName;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.TreeVisitor;

public class FinalParameterRewriter extends TreeVisitor {

	private List<SingleVariableDeclaration> parameteList = new ArrayList<SingleVariableDeclaration>();

	@Override
	public boolean visit(MethodDeclaration node) {
		parameteList.addAll(node.getParameters());
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		parameteList.clear();
	}
	
	@Override
	public void endVisit(InfixExpression node) {
	
	}
	
	@Override
	public void endVisit(Assignment node) {
		Expression exp = node.getLeftHandSide();
		if (exp instanceof SimpleName) {
			SimpleName simpleName = (SimpleName)exp;
			if (simpleName.getBinding() instanceof IVariableBinding) {
				setVariableBindingDeclarationModified((IVariableBinding)simpleName.getBinding());
			}
		}
		if (exp instanceof ArrayAccess) {
			ArrayAccess arrayAccess = (ArrayAccess)exp;
			Expression array =  arrayAccess.getArray();
			if (array instanceof SimpleName) {
				SimpleName simpleName = (SimpleName)array;
				if (simpleName.getBinding() instanceof IVariableBinding) {
					setVariableBindingDeclarationModified((IVariableBinding)simpleName.getBinding());
				}
			}
		}
	}
	
	@Override
	public boolean visit(PostfixExpression node) {
		Expression exp = node.getOperand();
		if (exp instanceof SimpleName) {
			SimpleName simpleName = (SimpleName)exp;
			if (simpleName.getBinding() instanceof IVariableBinding) {
				setVariableBindingDeclarationModified((IVariableBinding)simpleName.getBinding());
			}
		}
		return false;
	}
	
	@Override
	public boolean visit(PrefixExpression node) {
		Expression exp = node.getOperand();
		if (node.getOperator() == Operator.INCREMENT || node.getOperator() == Operator.DECREMENT) {
			if (exp instanceof SimpleName) {
				SimpleName simpleName = (SimpleName)exp;
				if (simpleName.getBinding() instanceof IVariableBinding) {
					setVariableBindingDeclarationModified((IVariableBinding)simpleName.getBinding());
				}
			}
		}
		return false;
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
