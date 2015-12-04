package com.j2swift.translate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

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
import com.j2swift.ast.VariableDeclarationFragment;
import com.j2swift.util.BindingUtil;

public class FinalParameterRewriter extends TreeVisitor {

	private List<SingleVariableDeclaration> parameteList = new ArrayList<SingleVariableDeclaration>();
	private List<VariableDeclarationFragment> varDeclarationList = new ArrayList<VariableDeclarationFragment>();

	@Override
	public boolean visit(MethodDeclaration node) {
		parameteList.addAll(node.getParameters());
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		for (VariableDeclarationFragment fragment : varDeclarationList) {
			fragment.setUserDefineFinal(true);
		}
		varDeclarationList.clear();
		parameteList.clear();
	}
	
	public boolean visit(VariableDeclarationFragment node) {
		if (!BindingUtil.isFinal(node.getVariableBinding())) {
			varDeclarationList.add(node);
		}
		return true;
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
				removeVarDeclaration((IVariableBinding)simpleName.getBinding());
			}
		}
		if (exp instanceof ArrayAccess) {
			ArrayAccess arrayAccess = (ArrayAccess)exp;
			Expression array =  arrayAccess.getArray();
			if (array instanceof SimpleName) {
				SimpleName simpleName = (SimpleName)array;
				if (simpleName.getBinding() instanceof IVariableBinding) {
					setVariableBindingDeclarationModified((IVariableBinding)simpleName.getBinding());
					removeVarDeclaration((IVariableBinding)simpleName.getBinding());
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
				removeVarDeclaration((IVariableBinding)simpleName.getBinding());
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
					removeVarDeclaration((IVariableBinding)simpleName.getBinding());
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
	
	private void removeVarDeclaration(IVariableBinding variableBinding) {
		for (VariableDeclarationFragment fragment : varDeclarationList) {
			if (fragment.getVariableBinding() == variableBinding) {
				varDeclarationList.remove(fragment);
				break;
			}
		}
	}

}
