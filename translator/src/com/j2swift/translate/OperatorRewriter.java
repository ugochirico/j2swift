package com.j2swift.translate;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;

import com.j2swift.ast.Assignment;
import com.j2swift.ast.Expression;
import com.j2swift.ast.FunctionInvocation;
import com.j2swift.ast.InfixExpression;
import com.j2swift.ast.SimpleName;
import com.j2swift.ast.TreeNode;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.types.FunctionBinding;
import com.j2swift.types.GeneratedVariableBinding;

public class OperatorRewriter extends TreeVisitor {
	@Override
	public void endVisit(InfixExpression node) {
		InfixExpression.Operator op = node.getOperator();
		ITypeBinding nodeType = node.getTypeBinding();
		String funcName = getInfixFunction(op, nodeType);
		if (funcName != null) {
			Iterator<Expression> operandIter = node.getOperands().iterator();
			Expression leftOperand = operandIter.next();
			operandIter.remove();

			// This takes extended operands into consideration. If a node has
			// three operands, o1 o2 o3,
			// the function invocations should be like f(f(o1, o2), o3), given
			// that the infix operators
			// translated here are all left-associative.
			while (operandIter.hasNext()) {
				Expression rightOperand = operandIter.next();
				operandIter.remove();
				FunctionBinding binding = new FunctionBinding(funcName,
						nodeType, null);
				binding.addParameters(leftOperand.getTypeBinding(),
						rightOperand.getTypeBinding());
				FunctionInvocation invocation = new FunctionInvocation(binding,
						nodeType);
				List<Expression> args = invocation.getArguments();
				args.add(leftOperand);
				args.add(rightOperand);
				leftOperand = invocation;
			}

			node.replaceWith(leftOperand);
		} else if (op == InfixExpression.Operator.PLUS
				&& typeEnv.isStringType(nodeType)
				&& !isStringAppend(node.getParent())) {
			rewriteStringConcatenation(node);
		}
	}

	private void rewriteStringConcatenation(InfixExpression node) {
		StringBuilder replaceStringContact = new StringBuilder("\"");
		for (Expression expression : node.getOperands()) {
			if (expression.getConstantValue() != null) {
				replaceStringContact.append(expression.toString());
			} else {
				replaceStringContact.append("\\(").append(expression.toString()).append(")");
			}
		}
		replaceStringContact.append("\"");
		SimpleName simpleName = new SimpleName(replaceStringContact.toString());
		simpleName.setBinding(new GeneratedVariableBinding(
				replaceStringContact.toString(), 0, typeEnv.getNSString(), false, true, null, null));
		node.replaceWith(simpleName);
	}

	private static String getInfixFunction(InfixExpression.Operator op,
			ITypeBinding nodeType) {
//		switch (op) {
//		case REMAINDER:
//			if (BindingUtil.isFloatingPoint(nodeType)) {
//				return nodeType.getName().equals("float") ? "fmodf" : "fmod";
//			}
//			return null;
//		case LEFT_SHIFT:
//			return "JreLShift" + intOrLong(nodeType);
//		case RIGHT_SHIFT_SIGNED:
//			return "JreRShift" + intOrLong(nodeType);
//		case RIGHT_SHIFT_UNSIGNED:
//			return "JreURShift" + intOrLong(nodeType);
//		default:
//			return null;
//		}
		return null;
	}

	private boolean isStringAppend(TreeNode node) {
		if (!(node instanceof Assignment)) {
			return false;
		}
		Assignment assignment = (Assignment) node;
		return assignment.getOperator() == Assignment.Operator.PLUS_ASSIGN
				&& typeEnv.resolveJavaType("java.lang.String")
						.isAssignmentCompatible(
								assignment.getLeftHandSide().getTypeBinding());
	}

	private static String intOrLong(ITypeBinding type) {
		switch (type.getBinaryName().charAt(0)) {
		case 'I':
			return "32";
		case 'J':
			return "64";
		default:
			throw new AssertionError(
					"Type expected to be int or long but was: "
							+ type.getName());
		}
	}
}
