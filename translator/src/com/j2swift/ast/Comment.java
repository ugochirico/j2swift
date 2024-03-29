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
 * Base node type for comments.
 */
public abstract class Comment extends TreeNode {

  protected Comment(org.eclipse.jdt.core.dom.Comment jdtNode) {
    super(jdtNode);
  }

  protected Comment(Comment other) {
    super(other);
  }

  public boolean isBlockComment() {
    return false;
  }

  public boolean isDocComment() {
    return false;
  }

  public boolean isLineComment() {
    return false;
  }
}
