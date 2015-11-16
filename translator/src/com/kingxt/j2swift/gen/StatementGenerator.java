package com.kingxt.j2swift.gen;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.kingxt.Options;
import com.kingxt.j2swift.ast.Assignment;
import com.kingxt.j2swift.ast.Block;
import com.kingxt.j2swift.ast.BooleanLiteral;
import com.kingxt.j2swift.ast.CStringLiteral;
import com.kingxt.j2swift.ast.CharacterLiteral;
import com.kingxt.j2swift.ast.Expression;
import com.kingxt.j2swift.ast.ExpressionStatement;
import com.kingxt.j2swift.ast.ForStatement;
import com.kingxt.j2swift.ast.FunctionInvocation;
import com.kingxt.j2swift.ast.IfStatement;
import com.kingxt.j2swift.ast.InfixExpression;
import com.kingxt.j2swift.ast.MethodInvocation;
import com.kingxt.j2swift.ast.NullLiteral;
import com.kingxt.j2swift.ast.NumberLiteral;
import com.kingxt.j2swift.ast.PostfixExpression;
import com.kingxt.j2swift.ast.PrefixExpression;
import com.kingxt.j2swift.ast.ReturnStatement;
import com.kingxt.j2swift.ast.SimpleName;
import com.kingxt.j2swift.ast.Statement;
import com.kingxt.j2swift.ast.StringLiteral;
import com.kingxt.j2swift.ast.SuperMethodInvocation;
import com.kingxt.j2swift.ast.TreeNode;
import com.kingxt.j2swift.ast.TreeUtil;
import com.kingxt.j2swift.ast.TreeVisitor;
import com.kingxt.j2swift.ast.VariableDeclarationFragment;
import com.kingxt.j2swift.ast.VariableDeclarationStatement;
import com.kingxt.j2swift.ast.WhileStatement;
import com.kingxt.j2swift.types.IOSTypeBinding;
import com.kingxt.j2swift.util.BindingUtil;

public class StatementGenerator extends TreeVisitor {

	private final SourceBuilder buffer;
	private final boolean useReferenceCounting;

	public static String generate(TreeNode node, int currentLine) {
		StatementGenerator generator = new StatementGenerator(node, currentLine);
		if (node == null) {
			throw new NullPointerException("cannot generate a null statement");
		}
		generator.run(node);
		return generator.getResult();
	}

	private StatementGenerator(TreeNode node, int currentLine) {
		buffer = new SourceBuilder(Options.emitLineDirectives(), currentLine);
		useReferenceCounting = !Options.useARC();
	}

	private String getResult() {
		return buffer.toString();
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		Expression expression = node.getExpression();
		ITypeBinding type = expression.getTypeBinding();
		if (!type.isPrimitive()
				&& Options.useARC()
				&& (expression instanceof MethodInvocation
						|| expression instanceof SuperMethodInvocation || expression instanceof FunctionInvocation)) {
			// Avoid clang warning that the return value is unused.
			buffer.append("(void) ");
		}
		expression.accept(this);
		buffer.append("\n");
		return false;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.getMethodBinding();
		assert binding != null;

		// Object receiving the message, or null if it's a method in this class.
		Expression receiver = node.getExpression();
		if (receiver != null) {
			receiver.setNeedUnwarpOptional(true);
		}
		if (BindingUtil.isStatic(binding)) {
			buffer.append(nameTable.getFullName(binding.getDeclaringClass()));
		} else if (receiver != null) {
			receiver.accept(this);
		}
		if (receiver != null) {
			buffer.append(".");
		}
		buffer.append(binding.getName());
		buffer.append('(');
		printMethodInvocationNameAndArgs(binding.getName(), node.getArguments());
		buffer.append(')');
		return false;
	}

	@Override
	public boolean visit(Block node) {
		buffer.append("{\n");
		printStatements(node.getStatements());
		buffer.append("}\n");
		return false;
	}

	private void printStatements(List<?> statements) {
		for (Iterator<?> it = statements.iterator(); it.hasNext();) {
			Statement s = (Statement) it.next();
			s.accept(this);
		}
	}

	@Override
	public boolean visit(ReturnStatement node) {
		buffer.append("return");
		Expression expr = node.getExpression();
		IMethodBinding methodBinding = TreeUtil.getOwningMethodBinding(node);
		if (expr != null) {
			buffer.append(' ');
			expr.accept(this);
		} else if (methodBinding != null && methodBinding.isConstructor()) {
			// A return statement without any expression is allowed in
			// constructors.
			buffer.append(" self");
		}
		buffer.append("\n");
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		buffer.append("\"" + node.getLiteralValue() + "\"");
		return false;
	}

	@Override
	public boolean visit(CStringLiteral node) {
		buffer.append("\"");
		buffer.append(node.getLiteralValue());
		buffer.append("\"");
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.getBinding();
		if (binding instanceof IVariableBinding) {
			buffer.append(nameTable
					.getVariableQualifiedName((IVariableBinding) binding));
		}else {
			if (binding instanceof ITypeBinding) {
				if (binding instanceof IOSTypeBinding) {
					buffer.append(binding.getName());
				} else {
					buffer.append(nameTable.getFullName((ITypeBinding) binding));
				}
			} else {
				buffer.append(node.getIdentifier());
			}
		}
		if (node.isNeedUnwarpOptional() && !BindingUtil.isPrimitive((IVariableBinding)node.getBinding())) {
			buffer.append("!");
		}
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		node.getLeftHandSide().accept(this);
		buffer.append(' ');
		buffer.append(node.getOperator().toString());
		buffer.append(' ');
		node.getRightHandSide().accept(this);
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (BindingUtil.isFinal(node.getVariableBinding())) {
			buffer.append("let").append(" ");
		} else {
			buffer.append("var").append(" ");
		}
		node.getName().accept(this);
		IVariableBinding binding = node.getVariableBinding();
		String swiftType = nameTable.getSpecificObjCType(binding);
		buffer.append(":").append(swiftType);
		if (node.isOptional()) {
			buffer.append("?");
		}
		Expression initializer = node.getInitializer();
		if (initializer != null) {
			buffer.append(" = ");
			initializer.accept(this);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.kingxt.j2swift.ast.TreeVisitor#visit(com.kingxt.j2swift.ast.VariableDeclarationStatement)
	 * etc method public final String a=23;
	 */
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		List<VariableDeclarationFragment> vars = node.getFragments();
		assert !vars.isEmpty();
		IVariableBinding binding = vars.get(0).getVariableBinding();
		String swiftType = nameTable.getSpecificObjCType(binding);
		// buffer.append(swiftType);
		for (Iterator<VariableDeclarationFragment> it = vars.iterator(); it
				.hasNext();) {
			VariableDeclarationFragment f = it.next();
			// buffer.append(swiftType);
			f.accept(this);
			if (it.hasNext()) {
				buffer.append("\n");
			}
		}
		buffer.append("\n");
		return false;
	}

	@Override
	public boolean visit(NullLiteral node) {
		buffer.append("nil");
		return false;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		char c = node.charValue();
		buffer.append(node.charValue());
		return false;
	}

	private void printMethodInvocationNameAndArgs(String selector,
			List<Expression> args) {
		for (int i = 0; i < args.size(); i++) {
			args.get(i).accept(this);
		}
	}

	@Override
	public boolean visit(WhileStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		buffer.append("if (");
		node.getExpression().accept(this);
		buffer.append(") ");
		node.getThenStatement().accept(this);
		if (node.getElseStatement() != null) {
			buffer.append(" else ");
			node.getElseStatement().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		buffer.append("for (");
		for (Iterator<Expression> it = node.getInitializers().iterator(); it
				.hasNext();) {
			Expression next = it.next();
			next.accept(this);
			if (it.hasNext()) {
				buffer.append(", ");
			}
		}
		buffer.append("; ");
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		buffer.append("; ");
		for (Iterator<Expression> it = node.getUpdaters().iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				buffer.append(", ");
			}
		}
		buffer.append(") ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		node.getOperand().accept(this);
		buffer.append(node.getOperator().toString());
		return false;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		buffer.append(node.getOperator().toString());
		node.getOperand().accept(this);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kingxt.j2swift.ast.TreeVisitor#visit(com.kingxt.j2swift.ast.
	 * InfixExpression)
	 * 
	 * Tranlator Java Operation as < > <= >= etc...
	 */
	@Override
	public boolean visit(InfixExpression node) {
		InfixExpression.Operator op = node.getOperator();
		List<Expression> operands = node.getOperands();
		assert operands.size() >= 2;
		if ((op.equals(InfixExpression.Operator.EQUALS) || op
				.equals(InfixExpression.Operator.NOT_EQUALS))) {
			Expression lhs = operands.get(0);
			Expression rhs = operands.get(1);
			// TODO
			if (lhs instanceof StringLiteral || rhs instanceof StringLiteral) {
				if (!(lhs instanceof StringLiteral)) {
					// In case the lhs can't call isEqual.
					lhs = operands.get(1);
					rhs = operands.get(0);
				}
				buffer.append(op.equals(InfixExpression.Operator.NOT_EQUALS) ? "!["
						: "[");
				lhs.accept(this);
				buffer.append(" isEqual:");
				rhs.accept(this);
				buffer.append("]");
				return false;
			}
		}
		String opStr = ' ' + op.toString() + ' ';
		boolean isFirst = true;
		//TODO
		boolean needUnwarpOptional = operands.size() > 1;
		for (Expression operand : operands) {
			operand.setNeedUnwarpOptional(needUnwarpOptional);
			if (!isFirst) {
				buffer.append(opStr);
			}
			isFirst = false;
			operand.accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		String token = node.getToken();
		if (token != null) {
			buffer.append(token);
			// TODO: to fix typeBingding
			// buffer.append(LiteralGenerator.fixNumberToken(token,
			// node.getTypeBinding()));
		} else {
			// buffer.append(LiteralGenerator.generate(node.getValue()));
		}
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		buffer.append(node.booleanValue() ? "true" : "false");
		return false;
	}
}
