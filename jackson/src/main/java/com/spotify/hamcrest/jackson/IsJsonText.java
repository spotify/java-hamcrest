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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IsJsonText extends AbstractJsonNodeMatcher<TextNode> {

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

  @Override
  protected boolean matchesNode(TextNode node, Description mismatchDescription) {
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
