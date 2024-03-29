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

/**
 * Return statement node type.
 */
public class ReturnStatement extends Statement {

  private ChildLink<Expression> expression = ChildLink.create(Expression.class, this);

  public ReturnStatement(org.eclipse.jdt.core.dom.ReturnStatement jdtNode) {
    super(jdtNode);
    expression.set((Expression) TreeConverter.convert(jdtNode.getExpression()));
  }

  public ReturnStatement(ReturnStatement other) {
    super(other);
    expression.copyFrom(other.getExpression());
  }

  public ReturnStatement(Expression expression) {
    this.expression.set(expression);
  }

  @Override
  public Kind getKind() {
    return Kind.RETURN_STATEMENT;
  }

  public Expression getExpression() {
    return expression.get();
  }

  public void setExpression(Expression newExpression) {
    expression.set(newExpression);
  }

  @Override
  protected void acceptInner(TreeVisitor visitor) {
    if (visitor.visit(this)) {
      expression.accept(visitor);
    }
    visitor.endVisit(this);
  }

  @Override
  public ReturnStatement copy() {
    return new ReturnStatement(this);
  }
}
