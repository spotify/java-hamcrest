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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsAnything.anything;

import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeType;
import tools.jackson.databind.node.StringNode;

public class IsJsonText extends AbstractJsonNodeMatcher<StringNode> {

  private final Matcher<? super String> textMatcher;

  private IsJsonText(final Matcher<? super String> textMatcher) {
    super(JsonNodeType.STRING);
    this.textMatcher = Objects.requireNonNull(textMatcher);
  }

  public static Matcher<JsonNode> jsonText() {
    return new IsJsonText(is(anything()));
  }

  public static Matcher<JsonNode> jsonText(String text) {
    return new IsJsonText(is(text));
  }

  public static Matcher<JsonNode> jsonText(Matcher<? super String> textMatcher) {
    return new IsJsonText(textMatcher);
  }

  public static Matcher<JsonNode> jsonText(final StringNode value) {
    return jsonText(value.asText());
  }

  @Override
  protected boolean matchesNode(StringNode node, Description mismatchDescription) {
    final String value = node.asText();
    if (textMatcher.matches(value)) {
      return true;
    } else {
      mismatchDescription.appendText("was a text node with value that ");
      textMatcher.describeMismatch(value, mismatchDescription);
      return false;
    }
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("a text node with value that ").appendDescriptionOf(textMatcher);
  }
}
