package com.j2swift.translate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.common.collect.Lists;
import com.google.j2objc.annotations.WeakOuter;
import com.j2swift.ast.AbstractTypeDeclaration;
import com.j2swift.ast.AnnotationTypeDeclaration;
import com.j2swift.ast.Assignment;
import com.j2swift.ast.Block;
import com.j2swift.ast.BodyDeclaration;
import com.j2swift.ast.CompilationUnit;
import com.j2swift.ast.ConstructorInvocation;
import com.j2swift.ast.EnumDeclaration;
import com.j2swift.ast.Expression;
import com.j2swift.ast.ExpressionStatement;
import com.j2swift.ast.FieldDeclaration;
import com.j2swift.ast.MethodDeclaration;
import com.j2swift.ast.Name;
import com.j2swift.ast.SimpleName;
import com.j2swift.ast.SingleVariableDeclaration;
import com.j2swift.ast.Statement;
import com.j2swift.ast.SuperConstructorInvocation;
import com.j2swift.ast.TreeNode;
import com.j2swift.ast.TreeUtil;
import com.j2swift.ast.TreeVisitor;
import com.j2swift.ast.TypeDeclaration;
import com.j2swift.ast.TypeDeclarationStatement;
import com.j2swift.types.GeneratedMethodBinding;
import com.j2swift.types.GeneratedVariableBinding;
import com.j2swift.util.BindingUtil;
import com.j2swift.util.ErrorUtil;
import com.j2swift.util.TranslationUtil;

public class InnerClassExtractor extends TreeVisitor {
	private final OuterReferenceResolver outerResolver;
	  private final List<AbstractTypeDeclaration> unitTypes;
	  // Helps keep types in the order they are visited.
	  private ArrayList<Integer> typeOrderStack = Lists.newArrayList();

	  public InnerClassExtractor(OuterReferenceResolver outerResolver, CompilationUnit unit) {
	    this.outerResolver = outerResolver;
	    unitTypes = unit.getTypes();
	  }

	  @Override
	  public boolean visit(TypeDeclaration node) {
	    return handleType();
	  }

	  @Override
	  public void endVisit(TypeDeclaration node) {
	    endHandleType(node);
	  }

	  @Override
	  public boolean visit(EnumDeclaration node) {
	    return handleType();
	  }

	  @Override
	  public void endVisit(EnumDeclaration node) {
	    endHandleType(node);
	  }

	  @Override
	  public boolean visit(AnnotationTypeDeclaration node) {
	    return handleType();
	  }

	  @Override
	  public void endVisit(AnnotationTypeDeclaration node) {
	    endHandleType(node);
	  }

	  private boolean handleType() {
	    typeOrderStack.add(unitTypes.size());
	    return true;
	  }

	  private void endHandleType(AbstractTypeDeclaration node) {
	    int insertIdx = typeOrderStack.remove(typeOrderStack.size() - 1);
	    TreeNode parentNode = node.getParent();
	    if (!(parentNode instanceof CompilationUnit)) {
	      // Remove this type declaration from its current location.
	      node.remove();
	      if (parentNode instanceof TypeDeclarationStatement) {
	        parentNode.remove();
	      }

	      ITypeBinding type = node.getTypeBinding();
	      if (!type.isInterface() && !type.isAnnotation() && !Modifier.isStatic(type.getModifiers())) {
	        addOuterFields(node);
	        updateConstructors(node);
	      }

	      // Make this node non-private, if necessary, and add it to the unit's type
	      // list.
	      node.removeModifiers(Modifier.PRIVATE);
	      unitTypes.add(insertIdx, node);

	      // Check for erroneous WeakOuter annotation on static inner class.
	      if (BindingUtil.isStatic(type) && BindingUtil.hasAnnotation(type, WeakOuter.class)) {
	        ErrorUtil.warning("static class " + type.getQualifiedName() + " has WeakOuter annotation");
	      }
	    }
	  }

	  private void addOuterFields(AbstractTypeDeclaration node) {
	    List<BodyDeclaration> members = node.getBodyDeclarations();
	    ITypeBinding clazz = node.getTypeBinding();
	    assert clazz.getDeclaringClass() != null;

	    IVariableBinding outerFieldBinding = outerResolver.getOuterField(clazz);
	    if (outerFieldBinding != null) {
	      members.add(0, new FieldDeclaration(outerFieldBinding, null));
	    }

	    List<IVariableBinding> innerFields = outerResolver.getInnerFields(clazz);
	    for (IVariableBinding field : innerFields) {
	      node.getBodyDeclarations().add(new FieldDeclaration(field, null));
	    }
	  }

	  private void updateConstructors(AbstractTypeDeclaration node) {
	    // Insert new parameters for each constructor in class.
	    boolean needsConstructor = true;
	    for (MethodDeclaration method : TreeUtil.getMethodDeclarations(node)) {
	      if (method.isConstructor()) {
	        needsConstructor = false;
	        addOuterParameters(node, method);
	      }
	    }

	    if (needsConstructor) {
	      GeneratedMethodBinding binding =
	          GeneratedMethodBinding.newConstructor(node.getTypeBinding(), 0, typeEnv);
	      MethodDeclaration constructor = new MethodDeclaration(binding);
	      constructor.setBody(new Block());
	      addOuterParameters(node, constructor);
	      node.getBodyDeclarations().add(constructor);
	    }
	  }

	  protected void addOuterParameters(
	      AbstractTypeDeclaration typeNode, MethodDeclaration constructor) {
	    ITypeBinding type = typeNode.getTypeBinding();
	    ITypeBinding outerType = type.getDeclaringClass();
	    IVariableBinding outerParamBinding = null;

	    GeneratedMethodBinding constructorBinding =
	        new GeneratedMethodBinding(constructor.getMethodBinding().getMethodDeclaration());
	    constructor.setMethodBinding(constructorBinding);

	    // Adds the outer and captured parameters to the declaration.
	    List<SingleVariableDeclaration> captureDecls = constructor.getParameters().subList(0, 0);
	    List<ITypeBinding> captureTypes = constructorBinding.getParameters().subList(0, 0);
	    if (outerResolver.needsOuterParam(type)) {
	      GeneratedVariableBinding paramBinding = new GeneratedVariableBinding(
	          "outer$", Modifier.FINAL, outerType, false, true, type, constructorBinding);
	      captureDecls.add(new SingleVariableDeclaration(paramBinding));
	      captureTypes.add(outerType);
	      outerParamBinding = paramBinding;
	    }
	    List<IVariableBinding> innerFields = outerResolver.getInnerFields(type);
	    List<IVariableBinding> captureParams = Lists.newArrayListWithCapacity(innerFields.size());
	    int captureCount = 0;
	    for (IVariableBinding innerField : innerFields) {
	      GeneratedVariableBinding paramBinding = new GeneratedVariableBinding(
	          "capture$" + captureCount++, Modifier.FINAL, innerField.getType(), false, true, type,
	          constructorBinding);
	      captureDecls.add(new SingleVariableDeclaration(paramBinding));
	      captureTypes.add(innerField.getType());
	      captureParams.add(paramBinding);
	    }

	    ConstructorInvocation thisCall = null;
	    SuperConstructorInvocation superCall = null;

	    List<Statement> statements = constructor.getBody().getStatements();
	    for (Statement stmt : statements) {
	      if (stmt instanceof ConstructorInvocation) {
	        thisCall = (ConstructorInvocation) stmt;
	        break;
	      } else if (stmt instanceof SuperConstructorInvocation) {
	        superCall = (SuperConstructorInvocation) stmt;
	        break;
	      }
	    }

	    if (thisCall != null) {
	      GeneratedMethodBinding newThisBinding =
	          new GeneratedMethodBinding(thisCall.getMethodBinding().getMethodDeclaration());
	      thisCall.setMethodBinding(newThisBinding);
	      List<Expression> args = thisCall.getArguments().subList(0, 0);
	      List<ITypeBinding> params = newThisBinding.getParameters().subList(0, 0);
	      if (outerParamBinding != null) {
	        args.add(new SimpleName(outerParamBinding));
	        params.add(outerParamBinding.getType());
	      }
	      for (IVariableBinding captureParam : captureParams) {
	        args.add(new SimpleName(captureParam));
	        params.add(captureParam.getType());
	      }
	    } else {
	      ITypeBinding superType = type.getSuperclass().getTypeDeclaration();
	      if (superCall == null) {
	        superCall = new SuperConstructorInvocation(
	            TranslationUtil.findDefaultConstructorBinding(superType, typeEnv));
	        statements.add(0, superCall);
	      }
	      passOuterParamToSuper(typeNode, superCall, superType, outerParamBinding);
	      IVariableBinding outerField = outerResolver.getOuterField(type);
	      int idx = 0;
	      if (outerField != null) {
	        assert outerParamBinding != null;
	        statements.add(idx++, new ExpressionStatement(
	            new Assignment(new SimpleName(outerField), new SimpleName(outerParamBinding))));
	      }
	      for (int i = 0; i < innerFields.size(); i++) {
	        statements.add(idx++, new ExpressionStatement(new Assignment(
	            new SimpleName(innerFields.get(i)), new SimpleName(captureParams.get(i)))));
	      }
	    }
	    assert constructor.getParameters().size()
	        == constructor.getMethodBinding().getParameterTypes().length;
	  }

	  private void passOuterParamToSuper(
	      AbstractTypeDeclaration typeNode, SuperConstructorInvocation superCall,
	      ITypeBinding superType, IVariableBinding outerParamBinding) {
	    if (!BindingUtil.hasOuterContext(superType) || superCall.getExpression() != null) {
	      return;
	    }
	    assert outerParamBinding != null;
	    GeneratedMethodBinding superCallBinding =
	        new GeneratedMethodBinding(superCall.getMethodBinding().getMethodDeclaration());
	    superCall.setMethodBinding(superCallBinding);

	    List<IVariableBinding> path = outerResolver.getPath(typeNode);
	    assert path != null && path.size() > 0;
	    path = Lists.newArrayList(path);
	    path.set(0, outerParamBinding);
	    Name superOuterArg = Name.newName(path);

	    superCall.getArguments().add(0, superOuterArg);
	    superCallBinding.addParameter(0, superType.getDeclaringClass());
	  }
}
