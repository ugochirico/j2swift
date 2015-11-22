package com.j2swift.translate;

import java.lang.reflect.Modifier;

//import com.j2swift.ast.AbstractTypeDeclaration;
//import com.j2swift.ast.AnonymousClassDeclaration;
//import com.google.devtools.j2objc.ast.Block;
//import com.google.devtools.j2objc.ast.BodyDeclaration;
//import com.google.devtools.j2objc.ast.ClassInstanceCreation;
//import com.google.devtools.j2objc.ast.EnumConstantDeclaration;
//import com.google.devtools.j2objc.ast.Expression;
//import com.google.devtools.j2objc.ast.MethodDeclaration;
//import com.google.devtools.j2objc.ast.SimpleName;
//import com.google.devtools.j2objc.ast.SingleVariableDeclaration;
//import com.google.devtools.j2objc.ast.SuperConstructorInvocation;
//import com.google.devtools.j2objc.ast.TreeNode;
//import com.google.devtools.j2objc.ast.TreeUtil;
//import com.google.devtools.j2objc.ast.Type;
//import com.google.devtools.j2objc.ast.TypeDeclaration;
//import com.google.devtools.j2objc.translate.IMethodBinding;
//import com.google.devtools.j2objc.translate.ITypeBinding;
//import com.google.devtools.j2objc.types.GeneratedMethodBinding;
//import com.google.devtools.j2objc.types.GeneratedVariableBinding;
import com.j2swift.ast.TreeVisitor;

public class AnonymousClassConverter extends TreeVisitor {
//	 /**
//	   * Convert the anonymous class into an inner class.  Fields are added for
//	   * final variables that are referenced, and a constructor is added.
//	   *
//	   * Note: endVisit is used for a depth-first traversal, to make it easier
//	   * to scan their containing nodes for references.
//	   */
//	  @Override
//	  public void endVisit(AnonymousClassDeclaration node) {
//	    ITypeBinding typeBinding = node.getTypeBinding();
//	    ITypeBinding outerType = typeBinding.getDeclaringClass();
//	    TreeNode parent = node.getParent();
//	    ClassInstanceCreation newInvocation = null;
//	    EnumConstantDeclaration enumConstant = null;
//	    Expression outerExpression = null;
//	    IMethodBinding constructorBinding = null;
//	    if (parent instanceof ClassInstanceCreation) {
//	      newInvocation = (ClassInstanceCreation) parent;
//	      outerExpression = newInvocation.getExpression();
//	      newInvocation.setExpression(null);
//	      constructorBinding = newInvocation.getMethodBinding();
//	    } else if (parent instanceof EnumConstantDeclaration) {
//	      enumConstant = (EnumConstantDeclaration) parent;
//	      constructorBinding = enumConstant.getMethodBinding();
//	    } else {
//	      throw new AssertionError(
//	          "unknown anonymous class declaration parent: " + parent.getClass().getName());
//	    }
//
//	    // Create a type declaration for this anonymous class.
//	    TypeDeclaration typeDecl = new TypeDeclaration(typeBinding);
//	    typeDecl.setSourceRange(node.getStartPosition(), node.getLength());
//
//	    for (BodyDeclaration decl : node.getBodyDeclarations()) {
//	      typeDecl.getBodyDeclarations().add(decl.copy());
//	    }
//
//	    // Add a default constructor.
//	    GeneratedMethodBinding defaultConstructor =
//	        addDefaultConstructor(typeDecl, constructorBinding, outerExpression);
//	    if (newInvocation != null) {
//	      newInvocation.setMethodBinding(defaultConstructor);
//	      if (outerExpression != null) {
//	        newInvocation.getArguments().add(0, outerExpression);
//	      }
//	    } else {
//	      enumConstant.setMethodBinding(defaultConstructor);
//	    }
//
//	    // If invocation, replace anonymous class invocation with the new constructor.
//	    if (newInvocation != null) {
//	      newInvocation.setAnonymousClassDeclaration(null);
//	      newInvocation.setType(Type.newType(typeBinding));
//	      IMethodBinding oldBinding = newInvocation.getMethodBinding();
//	      if (oldBinding != null) {
//	        GeneratedMethodBinding invocationBinding = new GeneratedMethodBinding(oldBinding);
//	        invocationBinding.setDeclaringClass(typeBinding);
//	        newInvocation.setMethodBinding(invocationBinding);
//	      }
//	    } else {
//	      enumConstant.setAnonymousClassDeclaration(null);
//	    }
//
//	    // Add type declaration to enclosing type.
//	    if (outerType.isAnonymous()) {
//	      AnonymousClassDeclaration outerDecl =
//	          TreeUtil.getNearestAncestorWithType(AnonymousClassDeclaration.class, parent);
//	      outerDecl.getBodyDeclarations().add(typeDecl);
//	    } else {
//	      AbstractTypeDeclaration outerDecl = TreeUtil.getOwningType(parent);
//	      outerDecl.getBodyDeclarations().add(typeDecl);
//	    }
//	    typeDecl.setKey(node.getKey());
//	    super.endVisit(node);
//	  }
//
//	  private GeneratedMethodBinding addDefaultConstructor(
//	      TypeDeclaration node, IMethodBinding constructorBinding, Expression outerExpression) {
//	    ITypeBinding clazz = node.getTypeBinding();
//	    GeneratedMethodBinding binding = new GeneratedMethodBinding(constructorBinding);
//	    MethodDeclaration constructor = new MethodDeclaration(binding);
//	    constructor.setBody(new Block());
//
//	    IMethodBinding superCallBinding = findSuperConstructorBinding(constructorBinding);
//	    SuperConstructorInvocation superCall = new SuperConstructorInvocation(superCallBinding);
//
//	    // If there is an outer expression (eg myFoo.new Foo() {};), then this must
//	    // be passed to the super class as its outer reference.
//	    if (outerExpression != null) {
//	      ITypeBinding outerExpressionType = outerExpression.getTypeBinding();
//	      GeneratedVariableBinding outerExpressionParam = new GeneratedVariableBinding(
//	          "superOuter$", Modifier.FINAL, outerExpressionType, false, true, clazz, binding);
//	      constructor.getParameters().add(0, new SingleVariableDeclaration(outerExpressionParam));
//	      binding.addParameter(0, outerExpressionType);
//	      superCall.setExpression(new SimpleName(outerExpressionParam));
//	    }
//
//	    // The invocation arguments must become parameters of the generated
//	    // constructor and passed to the super call.
//	    int argCount = 0;
//	    for (ITypeBinding argType : constructorBinding.getParameterTypes()) {
//	      GeneratedVariableBinding argBinding = new GeneratedVariableBinding(
//	          "arg$" + argCount++, 0, argType, false, true, clazz, binding);
//	      constructor.getParameters().add(new SingleVariableDeclaration(argBinding));
//	      superCall.getArguments().add(new SimpleName(argBinding));
//	    }
//	    assert superCall.getArguments().size() == superCallBinding.getParameterTypes().length
//	        || superCallBinding.isVarargs()
//	            && superCall.getArguments().size() >= superCallBinding.getParameterTypes().length - 1;
//
//	    constructor.getBody().getStatements().add(superCall);
//
//	    node.getBodyDeclarations().add(constructor);
//	    assert constructor.getParameters().size() == binding.getParameterTypes().length;
//
//	    return binding;
//	  }
//
//	  private IMethodBinding findSuperConstructorBinding(IMethodBinding constructorBinding) {
//	    ITypeBinding superClass = constructorBinding.getDeclaringClass().getSuperclass();
//	    for (IMethodBinding m : superClass.getDeclaredMethods()) {
//	      if (m.isConstructor() && constructorBinding.isSubsignature(m)) {
//	        return m;
//	      }
//	    }
//	    throw new AssertionError("could not find constructor");
//	  }
}
