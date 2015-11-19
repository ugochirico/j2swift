package com.j2swift.gen;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.j2swift.Options;
import com.j2swift.ast.ArrayCreation;
import com.j2swift.ast.ArrayInitializer;
import com.j2swift.ast.ArrayType;
import com.j2swift.ast.Assignment;
import com.j2swift.ast.Block;
import com.j2swift.ast.BooleanLiteral;
import com.j2swift.ast.BreakStatement;
import com.j2swift.ast.CStringLiteral;
import com.j2swift.ast.CharacterLiteral;
import com.j2swift.ast.ClassInstanceCreation;
import com.j2swift.ast.DoStatement;
import com.j2swift.ast.EnhancedForStatement;
import com.j2swift.ast.Expression;
import com.j2swift.ast.ExpressionStatement;
import com.j2swift.ast.ForStatement;
import com.j2swift.ast.FunctionInvocation;
import com.j2swift.ast.IfStatement;
import com.j2swift.ast.InfixExpression;
import com.j2swift.ast.LabeledStatement;
import com.j2swift.ast.MethodInvocation;
import com.j2swift.ast.Name;
import com.j2swift.ast.NullLiteral;
import com.j2swift.ast.NumberLiteral;
import com.j2swift.ast.PostfixExpression;
import com.j2swift.ast.PrefixExpression;
import com.j2swift.ast.QualifiedName;
import com.j2swift.ast.ReturnStatement;
import com.j2swift.ast.SimpleName;
import com.j2swift.ast.SimpleType;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.Statement;
import com.j2swift.ast.StringLiteral;
import com.j2swift.ast.SuperConstructorInvocation;
import com.j2swift.ast.SuperMethodInvocation;
import com.j2swift.ast.SwitchCase;
import com.j2swift.ast.SwitchStatement;
import com.j2swift.ast.ThrowStatement;
import com.j2swift.ast.TreeNode;
import com.j2swift.ast.TreeUtil;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.ast.TypeLiteral;
import com.j2swift.ast.VariableDeclarationFragment;
import com.j2swift.ast.VariableDeclarationStatement;
import com.j2swift.ast.WhileStatement;
import com.j2swift.types.IOSTypeBinding;
import com.j2swift.util.BindingUtil;

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
	public boolean visit(SuperConstructorInvocation node) {
		IMethodBinding binding = node.getMethodBinding();
		// assert node.getQualifier() == null
		// :
		// "Qualifiers expected to be handled by SuperMethodInvocationRewriter.";
		// assert !BindingUtil.isStatic(binding) :
		// "Static invocations are rewritten by Functionizer.";
		buffer.append("super.init(");
		printMethodInvocationNameAndArgs(nameTable.getMethodSelector(binding),
				node.getArguments());
		buffer.append(")");
		buffer.append("\n");
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		buffer.append("throw ");
		node.getExpression().accept(this);
		buffer.append("\n");
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayType node) {
		ITypeBinding binding = typeEnv.mapType(node.getTypeBinding());
		if (binding instanceof IOSTypeBinding) {
			buffer.append(binding.getName());
		} else {
			node.getComponentType().accept(this);
			buffer.append("[]");
		}
		return false;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		ITypeBinding arrayType = node.getTypeBinding();
		ArrayInitializer initializer = node.getInitializer();
		if (initializer != null) {
			return newInitializedArrayInvocation(arrayType,
					initializer.getExpressions());
		} else {
			List<Expression> dimensions = node.getDimensions();
			if (dimensions.size() == 1) {
				return newSingleDimensionArrayInvocation(arrayType,
						dimensions.get(0));
			} else {
				return newMultiDimensionArrayInvocation(arrayType, dimensions);
			}
		}
	}

	private boolean newInitializedArrayInvocation(ITypeBinding arrayType,
			List<Expression> elements) {
		ITypeBinding componentType = arrayType.getComponentType();
		ITypeBinding elementType = componentType.getDimensions() == 0 ? componentType
				: null;
		while (elementType == null) {
			componentType = componentType.getComponentType();
			if (componentType.getDimensions() == 0) {
				elementType = componentType;
			}
		}
		StringBuilder result = new StringBuilder("");
		appendArrayExpression(elements, result, elementType);
		buffer.append(result.toString());
		return false;
	}

	private void appendArrayExpression(List<Expression> elements,
			StringBuilder result, ITypeBinding type) {
		result.append("[");
		int i = 0;
		for (Expression exp : elements) {
			if (exp instanceof ArrayInitializer) {
				appendArrayExpression(
						((ArrayInitializer) exp).getExpressions(), result, type);
			} else {
				if (exp instanceof StringLiteral) {
					result.append("\"").append(exp).append("\"");
				} else {
					result.append(exp);
				}
			}
			if (i != elements.size() - 1) {
				result.append(",");
			}
			i++;
		}
		result.append("]");
	}

	private boolean newMultiDimensionArrayInvocation(ITypeBinding arrayType,
			List<Expression> dimensions) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean newSingleDimensionArrayInvocation(ITypeBinding arrayType,
			Expression expression) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(Block node) {
		buffer.append("{\n");
		printStatements(node.getStatements());
		buffer.append("}");
		if (node.getParent() != null
				&& (node.getParent() instanceof Block || node.getParent() instanceof SwitchStatement)) {
			buffer.append("();");
		}
		buffer.append("\n");
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
	public boolean visit(ClassInstanceCreation node) {
		IMethodBinding binding = node.getMethodBinding();
		String constructorName = nameTable.getFullName(binding
				.getDeclaringClass());
		buffer.append(constructorName);
		buffer.append("(");
		printMethodInvocationNameAndArgs(binding.getName(), node.getArguments());
		buffer.append(")");
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
		} else {
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
		if (node.isNeedUnwarpOptional()
				&& !BindingUtil.isPrimitive((IVariableBinding) node
						.getBinding()) && !BindingUtil.isFinal(binding)) {
			buffer.append("!");
		}
		return false;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {

		if (node.isVarargs()) {
			buffer.append("...");
		}
		if (buffer.charAt(buffer.length() - 1) != '*') {
			buffer.append(" ");
		}
		node.getName().accept(this);
		buffer.append(":");
		buffer.append(nameTable.getSpecificObjCType(node.getVariableBinding()));
		for (int i = 0; i < node.getExtraDimensions(); i++) {
			buffer.append("[]");
		}
		if (node.getInitializer() != null) {
			buffer.append(" = ");
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		IBinding binding = node.getBinding();
		if (binding instanceof IVariableBinding) {
			IVariableBinding var = (IVariableBinding) binding;
			if (BindingUtil.isGlobalVar(var)) {
				buffer.append(nameTable.getVariableQualifiedName(var));
				return false;
			}
		}
		if (binding instanceof ITypeBinding) {
			buffer.append(nameTable.getFullName((ITypeBinding) binding));
			return false;
		}
		Name qualifier = node.getQualifier();
		qualifier.accept(this);
		buffer.append("->");
		node.getName().accept(this);
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		node.getLabel().accept(this);
		buffer.append(": ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(SimpleType node) {
		ITypeBinding binding = node.getTypeBinding();
		if (binding != null) {
			String name = nameTable.getFullName(binding);
			buffer.append(name);
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		ITypeBinding type = node.getType().getTypeBinding();
		if (type.isPrimitive()) {
			buffer.append(String.format("[IOSClass %sClass]", type.getName()));
		} else {
			buffer.append(nameTable.getFullName(type));
			buffer.append(".getClass()");
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
	 * 
	 * @see com.kingxt.j2swift.ast.TreeVisitor#visit(com.kingxt.j2swift.ast.
	 * VariableDeclarationStatement) etc method public final String a=23;
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
			if (i != args.size() - 1) {
				buffer.append(",");
			}
		}
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
	public boolean visit(EnhancedForStatement node) {
		buffer.append("for");
		node.getParameter().accept(this);
		buffer.append(" in ");
		node.getExpression().setNeedUnwarpOptional(true);
		node.getExpression().accept(this);
		buffer.append(" ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		buffer.append("while (");
		node.getExpression().accept(this);
		buffer.append(") ");
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		buffer.append("repeat ");
		node.getBody().accept(this);
		buffer.append(" while (");
		node.getExpression().accept(this);
		buffer.append(")\n");
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		Expression expr = node.getExpression();
		ITypeBinding exprType = expr.getTypeBinding();
		if (typeEnv.isJavaStringType(exprType)) {
			// printStringSwitchStatement(node);
			return false;
		}
		buffer.append("switch (");
		expr.setNeedUnwarpOptional(true);
		expr.accept(this);
		buffer.append(") ");
		buffer.append("{\n");
		List<Statement> stmts = node.getStatements();
		for (Statement stmt : stmts) {
			stmt.accept(this);
		}
		if (!stmts.isEmpty()
				&& stmts.get(stmts.size() - 1) instanceof SwitchCase) {
			// Last switch case doesn't have an associated statement, so add
			// an empty one.
			buffer.append(";\n");
		}
		buffer.append("}\n");
		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {
		if (node.isDefault()) {
			buffer.append("  default:\n");
		} else {
			buffer.append("  case ");
			Expression expr = node.getExpression();
			boolean isEnumConstant = expr.getTypeBinding().isEnum();
			if (isEnumConstant) {
				String typeName = nameTable.getFullName(expr.getTypeBinding());
				buffer.append(typeName).append(".");
			}
			if (isEnumConstant && expr instanceof SimpleName) {
				buffer.append(((SimpleName) expr).getIdentifier());
			} else if (isEnumConstant && expr instanceof QualifiedName) {
				buffer.append(((QualifiedName) expr).getName().getIdentifier());
			} else {
				expr.accept(this);
			}
			buffer.append(": \n");
		}
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		if (node.getLabel() != null) {
			// Objective-C doesn't have a labeled break, so use a goto.
			buffer.append("fallthrough ");
			node.getLabel().accept(this);
		} else {
			buffer.append("break");
		}
		buffer.append(";\n");
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
		// TODO
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
			buffer.append(LiteralGenerator.generate(node.getValue()));
		}
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		buffer.append(node.booleanValue() ? "true" : "false");
		return false;
	}
}
