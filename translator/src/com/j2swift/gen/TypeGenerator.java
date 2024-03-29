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

package com.j2swift.gen;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.j2swift.Options;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.BodyDeclaration;
import com.j2swift.ast.CompilationUnit;
import com.j2swift.ast.EnumDeclaration;
import com.j2swift.ast.Expression;
import com.j2swift.ast.FieldDeclaration;
import com.j2swift.ast.FunctionDeclaration;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.NativeDeclaration;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.TreeUtil;
import com.j2swift.ast.TypeDeclaration;
import com.j2swift.ast.VariableDeclarationFragment;
import com.j2swift.types.Types;
import com.j2swift.util.BindingUtil;
import com.j2swift.util.NameTable;
import com.j2swift.util.TranslationUtil;

/**
 * The base class for TypeDeclarationGenerator and TypeImplementationGenerator,
 * providing common routines.
 * 
 * @author Tom Ball, Keith Stanger
 */
public abstract class TypeGenerator extends AbstractSourceGenerator {

	// Convenient fields for use by subclasses.
	protected final AbstractTypeDeclaration typeNode;
	protected final ITypeBinding typeBinding;
	protected final CompilationUnit compilationUnit;
	protected final Types typeEnv;
	protected final NameTable nameTable;
	protected final String typeName;
	protected final boolean typeNeedsReflection;

	private final List<BodyDeclaration> declarations;

	protected TypeGenerator(SourceBuilder builder, AbstractTypeDeclaration node) {
		super(builder);
		typeNode = node;
		typeBinding = node.getTypeBinding();
		compilationUnit = TreeUtil.getCompilationUnit(node);
		typeEnv = compilationUnit.getTypeEnv();
		nameTable = compilationUnit.getNameTable();
		typeName = nameTable.getFullName(typeBinding);
		typeNeedsReflection = TranslationUtil.needsReflection(typeBinding);
		declarations = filterDeclarations(node.getBodyDeclarations());
	}

	protected boolean shouldPrintDeclaration(BodyDeclaration decl) {
		return true;
	}

	private List<BodyDeclaration> filterDeclarations(
			Iterable<BodyDeclaration> declarations) {
		List<BodyDeclaration> filteredDecls = Lists.newArrayList();
		for (BodyDeclaration decl : declarations) {
			if (shouldPrintDeclaration(decl)) {
				filteredDecls.add(decl);
			}
		}
		return filteredDecls;
	}

	private static final Predicate<VariableDeclarationFragment> IS_STATIC_FIELD = new Predicate<VariableDeclarationFragment>() {
		public boolean apply(VariableDeclarationFragment frag) {
			return BindingUtil.isStatic(frag.getVariableBinding());
		}
	};

	private static final Predicate<VariableDeclarationFragment> IS_INSTANCE_FIELD = new Predicate<VariableDeclarationFragment>() {
		public boolean apply(VariableDeclarationFragment frag) {
			return BindingUtil.isInstanceVar(frag.getVariableBinding());
		}
	};

	private static final Predicate<VariableDeclarationFragment> IS_PRIMITIVE_CONSTANT = new Predicate<VariableDeclarationFragment>() {
		public boolean apply(VariableDeclarationFragment frag) {
			return BindingUtil.isPrimitiveConstant(frag.getVariableBinding());
		}
	};

	private static final Predicate<BodyDeclaration> IS_OUTER_DECL = new Predicate<BodyDeclaration>() {
		public boolean apply(BodyDeclaration decl) {
			return decl instanceof FunctionDeclaration;
		}
	};

	private static final Predicate<BodyDeclaration> IS_INNER_DECL = new Predicate<BodyDeclaration>() {
		public boolean apply(BodyDeclaration decl) {
			switch (decl.getKind()) {
			case METHOD_DECLARATION:
			case NATIVE_DECLARATION:
			case ENUM_DECLARATION:
			case TYPE_DECLARATION:
				return true;
			default:
				return false;
			}
		}
	};

	protected abstract void printFunctionDeclaration(FunctionDeclaration decl);

	protected abstract void printMethodDeclaration(MethodDeclaration decl);

	protected abstract void printNativeDeclaration(NativeDeclaration decl);

	protected abstract void printInnerEnumDeclaration(EnumDeclaration decl);
	
	protected abstract void printInnerTypeDeclaration(TypeDeclaration decl);

	private void printDeclaration(BodyDeclaration declaration) {
		switch (declaration.getKind()) {
		case FUNCTION_DECLARATION:
			printFunctionDeclaration((FunctionDeclaration) declaration);
			return;
		case METHOD_DECLARATION:
			printMethodDeclaration((MethodDeclaration) declaration);
			return;
		case NATIVE_DECLARATION:
			printNativeDeclaration((NativeDeclaration) declaration);
			return;
		case ENUM_DECLARATION:
			printInnerEnumDeclaration((EnumDeclaration) declaration);
			return;
		case TYPE_DECLARATION:
			printInnerTypeDeclaration((TypeDeclaration) declaration);
			return;
		default:
			break;
		}
	}

	protected void printDeclarations(
			Iterable<? extends BodyDeclaration> declarations) {
		for (BodyDeclaration declaration : declarations) {
			printDeclaration(declaration);
		}
	}

	protected boolean isInterfaceType() {
		return typeBinding.isInterface();
	}

	protected Iterable<VariableDeclarationFragment> getInstanceFields() {
		return getInstanceFields(declarations);
	}

	protected Iterable<VariableDeclarationFragment> getAllInstanceFields() {
		return getInstanceFields(typeNode.getBodyDeclarations());
	}

	private Iterable<VariableDeclarationFragment> getInstanceFields(
			List<BodyDeclaration> decls) {
		if (isInterfaceType()) {
			return Collections.emptyList();
		}
		return Iterables.filter(TreeUtil.asFragments(Iterables.filter(decls,
				FieldDeclaration.class)), IS_INSTANCE_FIELD);
	}

	protected Iterable<VariableDeclarationFragment> getStaticFields() {
		return Iterables.filter(TreeUtil.asFragments(Iterables.filter(
				declarations, FieldDeclaration.class)), IS_STATIC_FIELD);
	}

	protected Iterable<VariableDeclarationFragment> getPrimitiveConstants() {
		return Iterables.filter(TreeUtil.asFragments(Iterables.filter(
				declarations, FieldDeclaration.class)), IS_PRIMITIVE_CONSTANT);
	}

	protected Iterable<BodyDeclaration> getInnerDeclarations() {
		return Iterables.filter(declarations, IS_INNER_DECL);
	}

	protected Iterable<BodyDeclaration> getOuterDeclarations() {
		return Iterables.filter(declarations, IS_OUTER_DECL);
	}

	protected void printInnerDeclarations() {
		printDeclarations(getInnerDeclarations());
	}

	protected void printOuterDeclarations() {
		printDeclarations(getOuterDeclarations());
	}

	private boolean hasStaticAccessorMethods() {
		if (!Options.staticAccessorMethods()) {
			return false;
		}
		for (VariableDeclarationFragment fragment : TreeUtil
				.getAllFields(typeNode)) {
			if (BindingUtil.isStatic(fragment.getVariableBinding())
					&& !((FieldDeclaration) fragment.getParent())
							.hasPrivateDeclaration()) {
				return true;
			}
		}
		return false;
	}

	protected boolean needsPublicCompanionClass() {
		return !typeNode.hasPrivateDeclaration()
				&& (hasInitializeMethod()
						|| BindingUtil.isRuntimeAnnotation(typeBinding) || hasStaticAccessorMethods());
	}

	protected boolean needsCompanionClass() {
		return needsPublicCompanionClass() || typeNeedsReflection;
	}

	protected boolean hasInitializeMethod() {
		return !typeNode.getClassInitStatements().isEmpty();
	}

	protected String getDeclarationType(IVariableBinding var) {
		ITypeBinding type = var.getType();
		if (BindingUtil.isVolatile(var)) {
			return "volatile_" + NameTable.getPrimitiveObjCType(type);
		} else {
			return nameTable.getSpecificObjCType(type);
		}
	}

	protected String getFunctionSignature(FunctionDeclaration function) {
		StringBuilder sb = new StringBuilder();
		String returnType = nameTable.getObjCType(function.getReturnType()
				.getTypeBinding());
		returnType += returnType.endsWith("*") ? "" : " ";
		sb.append(returnType).append(function.getName()).append('(');
		for (Iterator<SingleVariableDeclaration> iter = function
				.getParameters().iterator(); iter.hasNext();) {
			IVariableBinding var = iter.next().getVariableBinding();
			String paramType = nameTable.getSpecificObjCType(var.getType());
			paramType += (paramType.endsWith("*") ? "" : " ");
			sb.append(paramType + nameTable.getVariableShortName(var));
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Create an Objective-C constructor from a list of annotation member
	 * declarations.
	 */
	protected String getAnnotationConstructorSignature(ITypeBinding annotation) {
		StringBuffer sb = new StringBuffer();
		sb.append("- (instancetype)init");
		IMethodBinding[] members = BindingUtil
				.getSortedAnnotationMembers(annotation);
		for (int i = 0; i < members.length; i++) {
			if (i == 0) {
				sb.append("With");
			} else {
				sb.append(" with");
			}
			IMethodBinding member = members[i];
			String name = NameTable.getAnnotationPropertyName(member);
			sb.append(NameTable.capitalize(name));
			sb.append(":(");
			sb.append(nameTable.getSpecificObjCType(member.getReturnType()));
			sb.append(')');
			sb.append(name);
			sb.append("__");
		}
		return sb.toString();
	}
	
	protected String getSuperTypeName() {
		ITypeBinding superclass = typeBinding.getSuperclass();
		if (superclass == null) {
			return null;
		}
		return nameTable.getFullName(superclass);
	}

	protected String generateExpression(Expression expr) {
		return StatementGenerator.generate(expr, getBuilder().getCurrentLine());
	}
	
	protected boolean variableShouldBeOptional(ITypeBinding typeBinding) {
		return BindingUtil.variableShouldBeOptional(typeBinding);
	}
}
