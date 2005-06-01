/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.parse;

import org.apache.nutch.protocol.Content;

/** A parser for content generated by a {@link org.apache.nutch.protocol.Protocol}
 * implementation.  This interface is implemented by extensions.  Nutch's core
 * contains no page parsing code.
 */
public interface Parser {
  /** The name of the extension point. */
  public final static String X_POINT_ID = Parser.class.getName();

  /** Creates the parse for some content. */
  Parse getParse(Content c);
}
