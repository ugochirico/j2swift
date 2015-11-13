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

package com.kingxt.j2swift.ast;

/**
 * Base node type for all statements.
 */
public abstract class Statement extends TreeNode {

  protected Statement() {}

  protected Statement(org.eclipse.jdt.core.dom.Statement jdtNode) {
    super(jdtNode);
  }

  protected Statement(Statement other) {
    super(other);
  }

  @Override
  public abstract Statement copy();
}