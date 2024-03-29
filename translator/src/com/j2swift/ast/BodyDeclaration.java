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

package com.j2swift.ast;

import org.eclipse.jdt.core.dom.IBinding;

import java.util.List;

/**
 * Base class for all declarations that may appear in the body of a type declaration.
 */
public abstract class BodyDeclaration extends TreeNode {

  private int modifiers = 0;
  // True if this node can be declared in the implementation and not the header.
  private boolean hasPrivateDeclaration = false;
  protected ChildLink<Javadoc> javadoc = ChildLink.create(Javadoc.class, this);
  protected ChildList<Annotation> annotations = ChildList.create(Annotation.class, this);

  public BodyDeclaration() {}

  public BodyDeclaration(org.eclipse.jdt.core.dom.BodyDeclaration jdtNode) {
    super(jdtNode);
    modifiers = jdtNode.getModifiers();
    javadoc.set((Javadoc) TreeConverter.convert(jdtNode.getJavadoc()));
    for (Object modifier : jdtNode.modifiers()) {
      if (modifier instanceof org.eclipse.jdt.core.dom.Annotation) {
        annotations.add((Annotation) TreeConverter.convert(modifier));
      }
    }
  }

  public BodyDeclaration(BodyDeclaration other) {
    super(other);
    modifiers = other.getModifiers();
    hasPrivateDeclaration = other.hasPrivateDeclaration();
    javadoc.copyFrom(other.getJavadoc());
    annotations.copyFrom(other.getAnnotations());
  }

  public BodyDeclaration(IBinding binding) {
    modifiers = binding.getModifiers();
  }

  public int getModifiers() {
    return modifiers;
  }

  public void setModifiers(int newModifiers) {
    modifiers = newModifiers;
  }

  public void addModifiers(int modifiersToAdd) {
    modifiers |= modifiersToAdd;
  }

  public void removeModifiers(int modifiersToRemove) {
    modifiers &= ~modifiersToRemove;
  }

  public boolean hasPrivateDeclaration() {
    return hasPrivateDeclaration;
  }

  public void setHasPrivateDeclaration(boolean value) {
    hasPrivateDeclaration = value;
  }

  public Javadoc getJavadoc() {
    return javadoc.get();
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  @Override
  public abstract BodyDeclaration copy();
}
