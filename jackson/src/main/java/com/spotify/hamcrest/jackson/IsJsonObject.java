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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.spotify.hamcrest.util.DescriptionUtils;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class IsJsonObject extends AbstractJsonNodeMatcher<ObjectNode> {

  private final ImmutableMap<String, Matcher<? super JsonNode>> entryMatchers;

  private IsJsonObject(final ImmutableMap<String, Matcher<? super JsonNode>> entryMatchers) {
    super(JsonNodeType.OBJECT);
    this.entryMatchers = Objects.requireNonNull(entryMatchers);
  }

  public static IsJsonObject jsonObject() {
    return new IsJsonObject(ImmutableMap.of());
  }

  public IsJsonObject where(String key, Matcher<? super JsonNode> valueMatcher) {
    return new IsJsonObject(
        ImmutableMap.<String, Matcher<? super JsonNode>>builder()
            .putAll(entryMatchers)
            .put(key, valueMatcher)
            .build());
  }

  @Override
  protected boolean matchesNode(ObjectNode node, Description mismatchDescription) {
    for (Map.Entry<String, Matcher<? super JsonNode>> entryMatcher : entryMatchers.entrySet()) {
      final String key = entryMatcher.getKey();
      final Matcher<? super JsonNode> valueMatcher = entryMatcher.getValue();

      final JsonNode value = node.path(key);

      if (!valueMatcher.matches(value)) {
        describeKey(key, mismatchDescription, d -> valueMatcher.describeMismatch(value, d));
        return false;
      }
    }
    return true;
  }

  @Override
  public void describeTo(Description description) {

    description.appendText("{\n");
    for (Map.Entry<String, Matcher<? super JsonNode>> entryMatcher : entryMatchers.entrySet()) {
      final String key = entryMatcher.getKey();
      final Matcher<? super JsonNode> valueMatcher = entryMatcher.getValue();

      description
          .appendText("  ")
          .appendText(jsonEscapeString(key))
          .appendText(": ");

      final Description innerDescription = new StringDescription();
      valueMatcher.describeTo(innerDescription);
      DescriptionUtils.indentDescription(description, innerDescription);
    }
    description.appendText("}");
  }

  static void describeKey(String key, Description mismatchDescription,
                          Consumer<Description> innerAction) {
    mismatchDescription
        .appendText("{\n  ...\n  ")
        .appendText(jsonEscapeString(key))
        .appendText(": ");

    final Description innerDescription = new StringDescription();
    innerAction.accept(innerDescription);
    DescriptionUtils.indentDescription(mismatchDescription, innerDescription);

    mismatchDescription.appendText("  ...\n}");
  }

  static String jsonEscapeString(String string) {
    return JsonNodeFactory.instance.textNode(string).toString();
  }
}
