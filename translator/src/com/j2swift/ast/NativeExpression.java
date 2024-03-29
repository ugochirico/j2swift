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

import com.google.common.collect.Lists;

import org.eclipse.jdt.core.dom.ITypeBinding;

import java.util.List;

/**
 * Native expression node type.
 */
public class NativeExpression extends Expression {

  private String code = null;
  private ITypeBinding typeBinding = null;
  private List<ITypeBinding> importTypes = Lists.newArrayList();

  public NativeExpression(NativeExpression other) {
    super(other);
    code = other.getCode();
    typeBinding = other.getTypeBinding();
    importTypes.addAll(other.getImportTypes());
  }

  public NativeExpression(String code, ITypeBinding type) {
    this.code = code;
    this.typeBinding = type;
  }

  @Override
  public Kind getKind() {
    return Kind.NATIVE_EXPRESSION;
  }

  public String getCode() {
    return code;
  }

  @Override
  public ITypeBinding getTypeBinding() {
    return typeBinding;
  }

  public List<ITypeBinding> getImportTypes() {
    return importTypes;
  }

  @Override
  protected void acceptInner(TreeVisitor visitor) {
    visitor.visit(this);
    visitor.endVisit(this);
  }

  @Override
  public NativeExpression copy() {
    return new NativeExpression(this);
  }
}
