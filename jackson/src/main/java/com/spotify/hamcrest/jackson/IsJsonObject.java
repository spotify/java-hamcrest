/*-
 * -\-\-
 * hamcrest-jackson
 * --
 * Copyright (C) 2017 Spotify AB
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
import com.spotify.hamcrest.util.DescriptionUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class IsJsonObject extends AbstractJsonNodeMatcher<ObjectNode> {

  private final LinkedHashMap<String, Matcher<? super JsonNode>> entryMatchers;

  private IsJsonObject(final LinkedHashMap<String, Matcher<? super JsonNode>> entryMatchers) {
    super(JsonNodeType.OBJECT);
    this.entryMatchers = Objects.requireNonNull(entryMatchers);
  }

  public static IsJsonObject jsonObject() {
    return new IsJsonObject(new LinkedHashMap<>());
  }

  public IsJsonObject where(String key, Matcher<? super JsonNode> valueMatcher) {
    final LinkedHashMap<String, Matcher<? super JsonNode>> newMap
        = new LinkedHashMap<>(entryMatchers);
    newMap.put(key, valueMatcher);
    return new IsJsonObject(newMap);
  }

  @Override
  protected boolean matchesNode(ObjectNode node, Description mismatchDescription) {
    LinkedHashMap<String, Consumer<Description>> mismatchedKeys = new LinkedHashMap<>();
    for (Map.Entry<String, Matcher<? super JsonNode>> entryMatcher : entryMatchers.entrySet()) {
      final String key = entryMatcher.getKey();
      final Matcher<? super JsonNode> valueMatcher = entryMatcher.getValue();

      final JsonNode value = node.path(key);

      if (!valueMatcher.matches(value)) {
        mismatchedKeys.put(key, d -> valueMatcher.describeMismatch(value, d));
      }
    }

    if (!mismatchedKeys.isEmpty()) {
      DescriptionUtils.describeNestedMismatches(
          entryMatchers.keySet(),
          mismatchDescription,
          mismatchedKeys,
          IsJsonObject::describeKey);
      return false;
    }
    return true;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("{\n");
    for (Map.Entry<String, Matcher<? super JsonNode>> entryMatcher : entryMatchers.entrySet()) {
      final String key = entryMatcher.getKey();
      final Matcher<? super JsonNode> valueMatcher = entryMatcher.getValue();

      description.appendText("  ");
      describeKey(key, description);
      description.appendText(": ");

      final Description innerDescription = new StringDescription();
      valueMatcher.describeTo(innerDescription);
      DescriptionUtils.indentDescription(description, innerDescription);
    }
    description.appendText("}");
  }

  private static void describeKey(final String key, final Description mismatchDescription) {
    mismatchDescription.appendText(jsonEscapeString(key));
  }

  private static String jsonEscapeString(String string) {
    return JsonNodeFactory.instance.textNode(string).toString();
  }
}
