package com.j2swift.translate;

import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.collect.Lists;
import com.j2swift.ast.ClassInstanceCreation;
import com.j2swift.ast.Expression;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.MethodInvocation;
import com.j2swift.ast.Name;
import com.j2swift.ast.SimpleName;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.SuperConstructorInvocation;
import com.j2swift.ast.SuperMethodInvocation;
import com.j2swift.ast.ThisExpression;
import com.j2swift.ast.TreeUtil;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.types.GeneratedMethodBinding;

public class OuterReferenceFixer extends TreeVisitor {

	  private final OuterReferenceResolver outerResolver;
	  private IVariableBinding outerParam = null;

	  public OuterReferenceFixer(OuterReferenceResolver outerResolver) {
	    this.outerResolver = outerResolver;
	  }

	@Override
	  public boolean visit(MethodDeclaration node) {
	    IMethodBinding binding = node.getMethodBinding();
	    if (binding.isConstructor()) {
	      List<SingleVariableDeclaration> params = node.getParameters();
	      if (params.size() > 0) {
	        IVariableBinding firstParam = params.get(0).getVariableBinding();
	        if (firstParam.getName().equals("outer$")) {
	          outerParam = firstParam;
	        }
	      }
	    }
	    return true;
	  }

	  @Override
	  public void endVisit(MethodDeclaration node) {
	    outerParam = null;
	  }

	  @Override
	  public boolean visit(ClassInstanceCreation node) {
	    ITypeBinding newType = node.getTypeBinding().getTypeDeclaration();
	    ITypeBinding declaringClass = newType.getDeclaringClass();
	    if (Modifier.isStatic(newType.getModifiers()) || declaringClass == null) {
	      return true;
	    }

	    GeneratedMethodBinding binding =
	        new GeneratedMethodBinding(node.getMethodBinding().getMethodDeclaration());
	    node.setMethodBinding(binding);

	    List<Expression> captureArgs = node.getArguments().subList(0, 0);
	    List<ITypeBinding> captureParams = binding.getParameters().subList(0, 0);
	    if (outerResolver.needsOuterParam(newType)) {
	      captureArgs.add(getOuterArg(node, declaringClass));
	      captureParams.add(declaringClass);
	    }

	    for (IVariableBinding capturedVar : getCapturedVariables(node)) {
	      captureArgs.add(new SimpleName(capturedVar));
	      captureParams.add(capturedVar.getType());
	    }

	    assert binding.isVarargs() || node.getArguments().size() == binding.getParameterTypes().length;
	    return true;
	  }

	  private List<IVariableBinding> getCapturedVariables(ClassInstanceCreation node) {
	    ITypeBinding newType = node.getTypeBinding().getTypeDeclaration();
	    ITypeBinding owningType = TreeUtil.getOwningType(node).getTypeBinding().getTypeDeclaration();
	    // Test for the recursive construction of a local class.
	    if (owningType.isEqualTo(newType)) {
	      return outerResolver.getInnerFields(newType);
	    }
	    return outerResolver.getCapturedVars(newType);
	  }

	  private Expression getOuterArg(ClassInstanceCreation node, ITypeBinding declaringClass) {
	    Expression outerExpr = node.getExpression();
	    if (outerExpr != null) {
	      node.setExpression(null);
	      return outerExpr;
	    }
	    List<IVariableBinding> path = outerResolver.getPath(node);
	    if (path != null) {
	      return Name.newName(fixPath(path));
	    }
	    return new ThisExpression(declaringClass);
	  }

	  @Override
	  public boolean visit(MethodInvocation node) {
	    List<IVariableBinding> path = outerResolver.getPath(node);
	    if (path != null) {
	      node.setExpression(Name.newName(fixPath(path)));
	    }
	    return true;
	  }

	  @Override
	  public void endVisit(SuperMethodInvocation node) {
	    List<IVariableBinding> path = outerResolver.getPath(node);
	    if (path != null) {
	      // We substitute the qualifying type name with the outer variable name.
	      node.setQualifier(Name.newName(fixPath(path)));
	    } else {
	      node.setQualifier(null);
	    }
	  }

	  @Override
	  public boolean visit(SimpleName node) {
	    List<IVariableBinding> path = outerResolver.getPath(node);
	    if (path != null) {
	      if (path.size() == 1 && path.get(0).getConstantValue() != null) {
	        IVariableBinding var = path.get(0);
	        node.replaceWith(TreeUtil.newLiteral(var.getConstantValue(), typeEnv));
	      } else {
	        node.replaceWith(Name.newName(fixPath(path)));
	      }
	    }
	    return true;
	  }

	  @Override
	  public boolean visit(ThisExpression node) {
	    List<IVariableBinding> path = outerResolver.getPath(node);
	    if (path != null) {
	      node.replaceWith(Name.newName(fixPath(path)));
	    } else {
	      node.setQualifier(null);
	    }
	    return true;
	  }

	  @Override
	  public void endVisit(SuperConstructorInvocation node) {
	    Expression outerExpression = node.getExpression();
	    if (outerExpression == null) {
	      return;
	    }
	    node.setExpression(null);
	    ITypeBinding outerExpressionType = outerExpression.getTypeBinding();
	    GeneratedMethodBinding binding =
	        new GeneratedMethodBinding(node.getMethodBinding().getMethodDeclaration());
	    node.setMethodBinding(binding);
	    node.getArguments().add(0, outerExpression);
	    binding.addParameter(0, outerExpressionType);
	  }

	  private List<IVariableBinding> fixPath(List<IVariableBinding> path) {
	    if (path.get(0) == OuterReferenceResolver.OUTER_PARAMETER) {
	      assert outerParam != null;
	      path = Lists.newArrayList(path);
	      path.set(0, outerParam);
	    }
	    return path;
	  }
}
