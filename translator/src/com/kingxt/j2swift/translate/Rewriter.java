package com.kingxt.j2swift.translate;

import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.common.collect.Lists;
import com.kingxt.j2swift.ast.Expression;
import com.kingxt.j2swift.ast.InfixExpression;
import com.kingxt.j2swift.ast.ParenthesizedExpression;
import com.kingxt.j2swift.ast.TreeUtil;
import com.kingxt.j2swift.ast.TreeVisitor;

public class Rewriter extends TreeVisitor {
	@Override
	public void endVisit(InfixExpression node) {
		InfixExpression.Operator op = node.getOperator();
		ITypeBinding type = node.getTypeBinding();
		if (typeEnv.isJavaStringType(type)
				&& op == InfixExpression.Operator.PLUS) {
			rewriteStringConcat(node);
		} else if (op == InfixExpression.Operator.CONDITIONAL_AND) {
			// Avoid logical-op-parentheses compiler warnings.
			if (node.getParent() instanceof InfixExpression) {
				InfixExpression parent = (InfixExpression) node.getParent();
				if (parent.getOperator() == InfixExpression.Operator.CONDITIONAL_OR) {
					ParenthesizedExpression.parenthesizeAndReplace(node);
				}
			}
		} else if (op == InfixExpression.Operator.AND) {
			// Avoid bitwise-op-parentheses compiler warnings.
			if (node.getParent() instanceof InfixExpression
					&& ((InfixExpression) node.getParent()).getOperator() == InfixExpression.Operator.OR) {
				ParenthesizedExpression.parenthesizeAndReplace(node);
			}
		}

		// Avoid lower precedence compiler warnings.
		if (op == InfixExpression.Operator.AND
				|| op == InfixExpression.Operator.OR) {
			for (Expression operand : node.getOperands()) {
				if (operand instanceof InfixExpression) {
					ParenthesizedExpression.parenthesizeAndReplace(operand);
				}
			}
		}
	}

	private void rewriteStringConcat(InfixExpression node) {
		// Collect all non-string operands that precede the first string
		// operand.
		// If there are multiple such operands, move them into a sub-expression.
		List<Expression> nonStringOperands = Lists.newArrayList();
		ITypeBinding nonStringExprType = null;
		for (Expression operand : node.getOperands()) {
			ITypeBinding operandType = operand.getTypeBinding();
			if (typeEnv.isJavaStringType(operandType)) {
				break;
			}
			nonStringOperands.add(operand);
			nonStringExprType = getAdditionType(nonStringExprType, operandType);
		}

		if (nonStringOperands.size() < 2) {
			return;
		}

		InfixExpression nonStringExpr = new InfixExpression(nonStringExprType,
				InfixExpression.Operator.PLUS);
		for (Expression operand : nonStringOperands) {
			nonStringExpr.getOperands().add(TreeUtil.remove(operand));
		}
		node.getOperands().add(0, nonStringExpr);
	}

	private ITypeBinding getAdditionType(ITypeBinding aType, ITypeBinding bType) {
		ITypeBinding doubleType = typeEnv.resolveJavaType("double");
		ITypeBinding boxedDoubleType = typeEnv
				.resolveJavaType("java.lang.Double");
		if (aType == doubleType || bType == doubleType
				|| aType == boxedDoubleType || bType == boxedDoubleType) {
			return doubleType;
		}
		ITypeBinding floatType = typeEnv.resolveJavaType("float");
		ITypeBinding boxedFloatType = typeEnv
				.resolveJavaType("java.lang.Float");
		if (aType == floatType || bType == floatType || aType == boxedFloatType
				|| bType == boxedFloatType) {
			return floatType;
		}
		ITypeBinding longType = typeEnv.resolveJavaType("long");
		ITypeBinding boxedLongType = typeEnv.resolveJavaType("java.lang.Long");
		if (aType == longType || bType == longType || aType == boxedLongType
				|| bType == boxedLongType) {
			return longType;
		}
		return typeEnv.resolveJavaType("int");
	}
}
