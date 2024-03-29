/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.j2swift.util;

import java.util.List;

import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.google.j2objc.annotations.ReflectionSupport;
import com.j2swift.Options;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.Annotation;
import com.j2swift.ast.ArrayAccess;
import com.j2swift.ast.ArrayCreation;
import com.j2swift.ast.Assignment;
import com.j2swift.ast.CastExpression;
import com.j2swift.ast.ClassInstanceCreation;
import com.j2swift.ast.ConditionalExpression;
import com.j2swift.ast.Expression;
import com.j2swift.ast.FieldAccess;
import com.j2swift.ast.InfixExpression;
import com.j2swift.ast.MethodInvocation;
import com.j2swift.ast.PackageDeclaration;
import com.j2swift.ast.ParenthesizedExpression;
import com.j2swift.ast.PostfixExpression;
import com.j2swift.ast.PrefixExpression;
import com.j2swift.ast.TreeNode;
import com.j2swift.ast.TreeUtil;
import com.j2swift.types.GeneratedMethodBinding;
import com.j2swift.types.IOSMethodBinding;
import com.j2swift.types.Types;

/**
 * General collection of utility methods.
 * 
 * @author Keith Stanger
 */
public final class TranslationUtil {

	public static boolean needsReflection(AbstractTypeDeclaration node) {
		return needsReflection(node.getTypeBinding());
	}

	public static boolean needsReflection(PackageDeclaration node) {
		return needsReflection(getReflectionSupportLevel(getAnnotation(node,
				ReflectionSupport.class)));
	}

	public static boolean needsReflection(ITypeBinding type) {
		while (type != null) {
			ReflectionSupport.Level level = getReflectionSupportLevel(BindingUtil
					.getAnnotation(type, ReflectionSupport.class));
			if (level != null) {
				return level == ReflectionSupport.Level.FULL;
			}
			type = type.getDeclaringClass();
		}
		return !Options.stripReflection();
	}

	private static boolean needsReflection(ReflectionSupport.Level level) {
		if (level != null) {
			return level == ReflectionSupport.Level.FULL;
		} else {
			return !Options.stripReflection();
		}
	}

	public static ReflectionSupport.Level getReflectionSupportLevel(
			IAnnotationBinding reflectionSupport) {
		if (reflectionSupport == null) {
			return null;
		}
		Object level = BindingUtil.getAnnotationValue(reflectionSupport,
				"value");
		if (level instanceof IVariableBinding) {
			return ReflectionSupport.Level.valueOf(((IVariableBinding) level)
					.getName());
		}
		return null;
	}

	/**
	 * The IPackageBinding does not provide the annotations so we must iterate
	 * the annotation from the tree.
	 */
	private static IAnnotationBinding getAnnotation(PackageDeclaration node,
			Class<?> annotationClass) {
		for (Annotation annotation : node.getAnnotations()) {
			IAnnotationBinding binding = annotation.getAnnotationBinding();
			if (BindingUtil.typeEqualsClass(binding.getAnnotationType(),
					annotationClass)) {
				return binding;
			}
		}
		return null;
	}

	/**
	 * If possible give this expression an unbalanced extra retain. If a
	 * non-null result is returned, then the returned expression has an
	 * unbalanced extra retain and the passed in expression is removed from the
	 * tree and must be discarded. If null is returned then the passed in
	 * expression is left untouched. The caller must ensure the result is
	 * eventually consumed.
	 */
	public static Expression retainResult(Expression node) {
		switch (node.getKind()) {
		case ARRAY_CREATION:
			((ArrayCreation) node).setHasRetainedResult(true);
			return TreeUtil.remove(node);
		case CLASS_INSTANCE_CREATION:
			((ClassInstanceCreation) node).setHasRetainedResult(true);
			return TreeUtil.remove(node);
		case METHOD_INVOCATION:
			MethodInvocation invocation = (MethodInvocation) node;
			Expression expr = invocation.getExpression();
			IMethodBinding method = invocation.getMethodBinding();
			if (expr != null
					&& method instanceof IOSMethodBinding
					&& ((IOSMethodBinding) method).getSelector().equals(
							NameTable.AUTORELEASE_METHOD)) {
				return TreeUtil.remove(expr);
			}
			// else fall-through
		default:
			return null;
		}
	}

	public static IMethodBinding findDefaultConstructorBinding(
			ITypeBinding type, Types typeEnv) {
		// Search for a non-varargs match.
		for (IMethodBinding m : type.getDeclaredMethods()) {
			if (m.isConstructor() && m.getParameterTypes().length == 0) {
				return m;
			}
		}
		// Search for a varargs match. Choose the most specific. (JLS 15.12.2.5)
		IMethodBinding result = null;
		for (IMethodBinding m : type.getDeclaredMethods()) {
			ITypeBinding[] paramTypes = m.getParameterTypes();
			if (m.isConstructor() && m.isVarargs() && paramTypes.length == 1) {
				if (result == null
						|| paramTypes[0].isAssignmentCompatible(result
								.getParameterTypes()[0])) {
					result = m;
				}
			}
		}
		if (result != null) {
			return result;
		}
		// Sometimes there won't be a default constructor (eg. enums), so just
		// create our own binding.
		return GeneratedMethodBinding.newConstructor(type, type.getModifiers(),
				typeEnv);
	}

	public static boolean isAssigned(Expression node) {
		TreeNode parent = node.getParent();

		while (parent instanceof ParenthesizedExpression) {
			parent = parent.getParent();
		}

		if (parent instanceof PostfixExpression) {
			PostfixExpression.Operator op = ((PostfixExpression) parent)
					.getOperator();
			if (op == PostfixExpression.Operator.INCREMENT
					|| op == PostfixExpression.Operator.DECREMENT) {
				return true;
			}
		} else if (parent instanceof PrefixExpression) {
			PrefixExpression.Operator op = ((PrefixExpression) parent)
					.getOperator();
			if (op == PrefixExpression.Operator.INCREMENT
					|| op == PrefixExpression.Operator.DECREMENT
					|| op == PrefixExpression.Operator.ADDRESS_OF) {
				return true;
			}
		} else if (parent instanceof Assignment) {
			return node == ((Assignment) parent).getLeftHandSide();
		}
		return false;
	}

	/**
	 * Reterns whether the expression might have any side effects. If true, it
	 * would be unsafe to prune the given node from the tree.
	 */
	public static boolean hasSideEffect(Expression expr) {
		switch (expr.getKind()) {
		case BOOLEAN_LITERAL:
		case CHARACTER_LITERAL:
		case NULL_LITERAL:
		case NUMBER_LITERAL:
		case QUALIFIED_NAME:
		case SIMPLE_NAME:
		case STRING_LITERAL:
		case SUPER_FIELD_ACCESS:
		case THIS_EXPRESSION:
			return false;
		case CAST_EXPRESSION:
			return hasSideEffect(((CastExpression) expr).getExpression());
		case CONDITIONAL_EXPRESSION: {
			ConditionalExpression condExpr = (ConditionalExpression) expr;
			return hasSideEffect(condExpr.getExpression())
					|| hasSideEffect(condExpr.getThenExpression())
					|| hasSideEffect(condExpr.getElseExpression());
		}
		case FIELD_ACCESS:
			return hasSideEffect(((FieldAccess) expr).getExpression());
		case INFIX_EXPRESSION:
			for (Expression operand : ((InfixExpression) expr).getOperands()) {
				if (hasSideEffect(operand)) {
					return true;
				}
			}
			return false;
		case PARENTHESIZED_EXPRESSION:
			return hasSideEffect(((ParenthesizedExpression) expr)
					.getExpression());
		case PREFIX_EXPRESSION: {
			PrefixExpression preExpr = (PrefixExpression) expr;
			PrefixExpression.Operator op = preExpr.getOperator();
			return op == PrefixExpression.Operator.INCREMENT
					|| op == PrefixExpression.Operator.DECREMENT
					|| hasSideEffect(preExpr.getOperand());
		}
		default:
			return true;
		}
	}

	/**
	 * Returns the modifier for an assignment expression being converted to a
	 * function. The result will be "Array" if the lhs is an array access,
	 * "Strong" if the lhs is a field with a strong reference, and an empty
	 * string for local variables and weak fields.
	 */
	public static String getOperatorFunctionModifier(Expression expr) {
		IVariableBinding var = TreeUtil.getVariableBinding(expr);
		if (var == null) {
			assert TreeUtil.trimParentheses(expr) instanceof ArrayAccess : "Expression cannot be resolved to a variable or array access.";
			return "Array";
		}
		String modifier = "";
		if (BindingUtil.isVolatile(var)) {
			modifier += "Volatile";
		}
		if (!BindingUtil.isWeakReference(var)
				&& (var.isField() || Options.useARC())) {
			modifier += "Strong";
		}
		return modifier;
	}

	/**
	 * byte 0 short 1 char 2 int 3 long 4 float 5 double 6
	 * 
	 * @param types
	 * @return
	 */
	public static ITypeBinding getImplicitPrimitiveJavaCastType(
			List<ITypeBinding> types) {
		if (types.isEmpty()) {
			return null;
		}
		int typeTag = 0;
		ITypeBinding result = types.get(0);
		for (ITypeBinding iTypeBinding : types) {
			char type = iTypeBinding.getBinaryName().charAt(0);
			int tag = 0;
			if (type == 'I') {
				tag = 3;
			} else if (type == 'C') {
				tag = 2;
			}
			if (type == 'J') {
				tag = 4;
			}
			if (type == 'F') {
				tag = 5;
			}
			if (type == 'D') {
				tag = 6;
			}
			if (type == 'S') {
				tag = 1;
			}
			if (type == 'B') {
				tag = 0;
			}
			if (tag > typeTag) {
				tag = typeTag;
				result = iTypeBinding;
			}
		}
		return result;
	}
}
