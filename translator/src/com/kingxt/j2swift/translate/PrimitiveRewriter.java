package com.kingxt.j2swift.translate;

import org.eclipse.jdt.core.dom.IVariableBinding;

import com.kingxt.j2swift.ast.Expression;
import com.kingxt.j2swift.ast.NumberLiteral;
import com.kingxt.j2swift.ast.TreeVisitor;
import com.kingxt.j2swift.ast.VariableDeclarationFragment;
import com.kingxt.j2swift.util.BindingUtil;

/**
 * PrimitiveRewriter will rewrite all primitive uninitial value to java default
 * value
 * 
 * @author kingxt
 *
 */
public class PrimitiveRewriter extends TreeVisitor {
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		IVariableBinding binding = node.getVariableBinding();
		if (BindingUtil.isPrimitive(binding)) {
			Expression initializer = node.getInitializer();
			if (initializer == null) {
				char type = binding.getType().getBinaryName().charAt(0);
				if (type == 'I') {
					NumberLiteral number = NumberLiteral.newIntLiteral(0, typeEnv); 
					node.setInitializer(number);
				}
				System.out.println(node);
			}
		}
	}
}
