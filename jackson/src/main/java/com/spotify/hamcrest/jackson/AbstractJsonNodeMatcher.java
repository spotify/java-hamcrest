/*-
 * -\-\-
 * hamcrest-jackson
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.hamcrest.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.spotify.hamcrest.util.LanguageUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public abstract class AbstractJsonNodeMatcher<A extends JsonNode>
    extends TypeSafeDiagnosingMatcher<JsonNode> {

  private final JsonNodeType type;

  protected AbstractJsonNodeMatcher(JsonNodeType type) {
    super(JsonNode.class);
    this.type = type;
  }

  @Override
  protected boolean matchesSafely(JsonNode item, Description mismatchDescription) {
    if (item.getNodeType() == type) {
      @SuppressWarnings("unchecked")
      final A node = (A) item;

      return matchesNode(node, mismatchDescription);
    } else {
      mismatchDescription
          .appendText("was not ")
          .appendText(LanguageUtils.addArticle(type.name().toLowerCase()))
          .appendText(" node, but ")
          .appendText(LanguageUtils.addArticle(item.getNodeType().name().toLowerCase()))
          .appendText(" node");
      return false;
    }
  }

  protected abstract boolean matchesNode(A node, Description mismatchDescription);
}
