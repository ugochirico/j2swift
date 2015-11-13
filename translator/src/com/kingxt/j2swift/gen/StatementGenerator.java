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
import com.kingxt.j2swift.ast.CStringLiteral;
import com.kingxt.j2swift.ast.CharacterLiteral;
import com.kingxt.j2swift.ast.Expression;
import com.kingxt.j2swift.ast.ExpressionStatement;
import com.kingxt.j2swift.ast.FunctionInvocation;
import com.kingxt.j2swift.ast.IfStatement;
import com.kingxt.j2swift.ast.MethodInvocation;
import com.kingxt.j2swift.ast.ReturnStatement;
import com.kingxt.j2swift.ast.SimpleName;
import com.kingxt.j2swift.ast.Statement;
import com.kingxt.j2swift.ast.StringLiteral;
import com.kingxt.j2swift.ast.SuperMethodInvocation;
import com.kingxt.j2swift.ast.TreeNode;
import com.kingxt.j2swift.ast.TreeUtil;
import com.kingxt.j2swift.ast.TreeVisitor;
import com.kingxt.j2swift.ast.VariableDeclarationFragment;
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

		if (BindingUtil.isStatic(binding)) {
			buffer.append(nameTable.getFullName(binding.getDeclaringClass()));
		} else if (receiver != null) {
			receiver.accept(this);
		}
		if (receiver != null) {
			buffer.append("!"); // TODO
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
			return false;
		}
		if (binding instanceof ITypeBinding) {
			if (binding instanceof IOSTypeBinding) {
				buffer.append(binding.getName());
			} else {
				buffer.append(nameTable.getFullName((ITypeBinding) binding));
			}
		} else {
			buffer.append(node.getIdentifier());
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
		node.getName().accept(this);
		Expression initializer = node.getInitializer();
		if (initializer != null) {
			buffer.append(" = ");
			initializer.accept(this);
		}
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
	public boolean visit(CharacterLiteral node) {
		char c = node.charValue();
		buffer.append(node.charValue());
		return false;
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
	public boolean visit(com.kingxt.j2swift.ast.NumberLiteral node) {
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
}
