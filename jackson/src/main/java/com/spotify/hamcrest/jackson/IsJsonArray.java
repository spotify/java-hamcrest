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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsAnything.anything;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IsJsonArray extends AbstractJsonNodeMatcher<ArrayNode> {

  private final Matcher<? super Collection<JsonNode>> elementsMatcher;

  private IsJsonArray(Matcher<? super Collection<JsonNode>> elementsMatcher) {
    super(JsonNodeType.ARRAY);
    this.elementsMatcher = Objects.requireNonNull(elementsMatcher);
  }

  public static Matcher<JsonNode> jsonArray() {
    return new IsJsonArray(is(anything()));
  }

  public static Matcher<JsonNode> jsonArray(
      Matcher<? super Collection<? extends JsonNode>> elementsMatcher) {
    return new IsJsonArray(elementsMatcher);
  }

  public static Matcher<JsonNode> jsonArray(final ArrayNode value) {
    return jsonArray(is(ImmutableList.copyOf(value)));
  }

  @Override
  protected boolean matchesNode(ArrayNode node, Description mismatchDescription) {
    final ImmutableList<JsonNode> elements = ImmutableList.copyOf(node);
    if (elementsMatcher.matches(elements)) {
      return true;
    } else {
      mismatchDescription.appendText("was an array node whose elements ");
      elementsMatcher.describeMismatch(elements, mismatchDescription);
      return false;
    }
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("an array node whose elements ").appendDescriptionOf(elementsMatcher);
  }
}
