package com.kingxt.j2swift.gen;

import org.eclipse.jdt.core.dom.IVariableBinding;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kingxt.j2swift.ast.AbstractTypeDeclaration;
import com.kingxt.j2swift.ast.BodyDeclaration;
import com.kingxt.j2swift.ast.Expression;
import com.kingxt.j2swift.ast.FieldDeclaration;
import com.kingxt.j2swift.ast.FunctionDeclaration;
import com.kingxt.j2swift.ast.MethodDeclaration;
import com.kingxt.j2swift.ast.NativeDeclaration;
import com.kingxt.j2swift.ast.VariableDeclarationFragment;
import com.kingxt.j2swift.util.BindingUtil;

public class TypeDeclarationGenerator extends TypeGenerator {

	protected TypeDeclarationGenerator(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		super(builder, node);
	}

	public static void generate(SourceBuilder builder,
			AbstractTypeDeclaration node) {
		new TypeDeclarationGenerator(builder, node).generate();
	}

	private void generate() {
		printClassExtension();

	}

	private static final Predicate<VariableDeclarationFragment> PROPERTIES = new Predicate<VariableDeclarationFragment>() {
		public boolean apply(VariableDeclarationFragment fragment) {
			IVariableBinding varBinding = fragment.getVariableBinding();
			return varBinding.isField();
		}
	};

	private void printClassExtension() {
		if (isInterfaceType()) {
			return;
		}
		boolean hasPrivateFields = !Iterables.isEmpty(getInstanceFields());
		Iterable<BodyDeclaration> privateDecls = getInnerDeclarations();
		if (!Iterables.isEmpty(privateDecls) || hasPrivateFields) {
			newline();
			printInstanceVariables();
			printDeclarations(privateDecls);
		}
	}

	/**
	 * Prints the list of instance variables in a type.
	 */
	protected void printInstanceVariables() {
		Iterable<VariableDeclarationFragment> fields = getInstanceFields();
		if (Iterables.isEmpty(fields)) {
			newline();
			return;
		}

		indent();
		for (VariableDeclarationFragment fragment : fields) {
			IVariableBinding varBinding = fragment.getVariableBinding();
			FieldDeclaration declaration = (FieldDeclaration) fragment
					.getParent();
			JavadocGenerator.printDocComment(getBuilder(),
					declaration.getJavadoc());
			printIndent();
			if (BindingUtil.isWeakReference(varBinding)
					&& !BindingUtil.isVolatile(varBinding)) {
				// We must add this even without -use-arc because the header may
				// be
				// included by a file compiled with ARC.
				print("weak ");
			}
			print("var ");
			print(nameTable.getVariableShortName(varBinding));

			print(':');
			String swiftType = getDeclarationType(varBinding);
			print(swiftType + "?");
			
			Expression initializer = fragment.getInitializer();
			if (initializer != null) {
				String value = generateExpression(initializer);
				printf(" = %s",value);
			}
			println("");
		}
		unindent();
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
	
	protected String generateExpression(Expression expr) {
		return StatementGenerator.generate(expr, getBuilder().getCurrentLine());
	}
}
