package com.j2swift.gen;

import org.eclipse.jdt.core.dom.IVariableBinding;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.BodyDeclaration;
import com.j2swift.ast.EnumDeclaration;
import com.j2swift.ast.Expression;
import com.j2swift.ast.FieldDeclaration;
import com.j2swift.ast.FunctionDeclaration;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.NativeDeclaration;
import com.j2swift.ast.TypeDeclaration;
import com.j2swift.ast.VariableDeclarationFragment;
import com.j2swift.util.BindingUtil;

/**
 * Generator all declaration variable
 * 
 * @author xutao1
 *
 */
public class VariablesDeclarationGenerator extends TypeGenerator {

	protected VariablesDeclarationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}

	public static void generate(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		new VariablesDeclarationGenerator(builder, node).generate();
	}

	private void generate() {
		printVariablesDeclation();
	}

	private static final Predicate<VariableDeclarationFragment> PROPERTIES = new Predicate<VariableDeclarationFragment>() {
		public boolean apply(VariableDeclarationFragment fragment) {
			IVariableBinding varBinding = fragment.getVariableBinding();
			return varBinding.isField();
		}
	};

	private void printVariablesDeclation() {
		if (isInterfaceType()) {
			return;
		}
		boolean hasInstanceFields = !Iterables.isEmpty(getInstanceFields());
		boolean hasStaticFields = !Iterables.isEmpty(getStaticFields());
		boolean hasInnerDeclarations = !Iterables
				.isEmpty(getInnerDeclarations());
		if (hasInstanceFields) {
			printInstanceFields();
		}
		if (hasStaticFields) {
			printStaticFields();
		}
		if (hasInstanceFields || hasStaticFields || hasInnerDeclarations) {
			newline();
			Iterable<BodyDeclaration> privateDecls = getInnerDeclarations();
			printDeclarations(privateDecls);
		}
	}

	protected void printInstanceFields() {
		indent();
		Iterable<VariableDeclarationFragment> fields = getInstanceFields();
		for (VariableDeclarationFragment fragment : fields) {
			printFieldFullDeclaration(fragment);
		}
		newline();
		unindent();
	}

	protected void printStaticFields() {
		indent();
		Iterable<VariableDeclarationFragment> fields = getStaticFields();
		for (VariableDeclarationFragment fragment : fields) {
			printStaticFieldFullDeclaration(fragment);
		}
		newline();
		unindent();
	}

	private void printFieldFullDeclaration(VariableDeclarationFragment fragment) {
		IVariableBinding varBinding = fragment.getVariableBinding();
		FieldDeclaration declaration = (FieldDeclaration) fragment.getParent();
		JavadocGenerator
				.printDocComment(getBuilder(), declaration.getJavadoc());
		printIndent();
		if (BindingUtil.isWeakReference(varBinding)
				&& !BindingUtil.isVolatile(varBinding)) {
			// We must add this even without -use-arc because the header may
			// be
			// included by a file compiled with ARC.
			print("weak ");
		}
		if (BindingUtil.isPrivate(varBinding)) {
			print("private ");
		} else if (BindingUtil.isPublic(varBinding)) {
			print("public ");
		} else {
			// default
		}

		if (BindingUtil.isFinal(varBinding)) {
			print("let ");
		} else {
			print("var ");
		}
		print(nameTable.getVariableShortName(varBinding));

		print(':');
		String swiftType = getDeclarationType(varBinding);
		print(swiftType);
		if (variableShouldBeOptional(fragment.getVariableBinding().getType())) {
			print("?");
		}

		Expression initializer = fragment.getInitializer();
		if (initializer != null) {
			String value = generateExpression(initializer);
			printf(" = %s", value);
		}
		println("");
	}

	private void printStaticFieldFullDeclaration(
			VariableDeclarationFragment fragment) {
		IVariableBinding var = fragment.getVariableBinding();
		String declType = getDeclarationType(var);
		String name = nameTable.getVariableShortName(var);
		String accessDeclaration;
		String staticStr = "static ";
		String finalStr;
		if (BindingUtil.isPrivate(var)) {
			accessDeclaration = "private ";
		} else if (BindingUtil.isPublic(var)) {
			accessDeclaration = "public ";
		} else {
			accessDeclaration = "";
		}

		if (BindingUtil.isFinal(var)) {
			finalStr = "let ";
		} else {
			finalStr = "var ";
		}
		printStaticFieldDeclaration(fragment, String.format("%s%s%s%s:%s",
				accessDeclaration, staticStr, finalStr, name, declType));
	}

	private void printStaticFieldDeclaration(
			VariableDeclarationFragment fragment, String baseDeclaration) {
		Expression initializer = fragment.getInitializer();
		printIndent();
		print("" + baseDeclaration);
		if (variableShouldBeOptional(fragment.getVariableBinding().getType())) {
			print("?");
		}
		if (initializer != null) {
			print(" = " + generateExpression(initializer));
		}
		newline();
	}

	@Override
	protected void printFunctionDeclaration(FunctionDeclaration decl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printMethodDeclaration(MethodDeclaration decl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printNativeDeclaration(NativeDeclaration decl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printInnerEnumDeclaration(EnumDeclaration decl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printInnerTypeDeclaration(TypeDeclaration decl) {
		// TODO Auto-generated method stub

	}
}
