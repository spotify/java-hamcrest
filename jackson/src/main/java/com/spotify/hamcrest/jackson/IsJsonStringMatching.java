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

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Condition.matched;
import static org.hamcrest.Condition.notMatched;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matcher for matching Json strings
 *
 * <p>This is useful as an entry point to matching larger structures without needing to use Jackson
 * explicitly.
 *
 * <pre>
 *   <code>
 *     String myJson = "{\"key\": 1234}";
 *     assertThat(myJson, isJsonStringMatching(jsonObject().where("key", jsonInt(1234)));
 *   </code>
 * </pre>
 */
public final class IsJsonStringMatching extends TypeSafeDiagnosingMatcher<String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static Matcher<String> isJsonStringMatching(final Matcher<JsonNode> matcher) {
    return new IsJsonStringMatching(matcher);
  }

  private final Matcher<JsonNode> matcher;

  private IsJsonStringMatching(final Matcher<JsonNode> matcher) {
    this.matcher = requireNonNull(matcher, "matcher");
  }

  @Override
  protected boolean matchesSafely(final String string, final Description description) {
    return parseJsonNode(string, description).matching(matcher);
  }

  private Condition<JsonNode> parseJsonNode(
      final String string, final Description mismatchDescription) {
    if (string == null) {
      mismatchDescription.appendText(" but JSON string was null");
      return notMatched();
    }

    try {
      final JsonNode jsonNode = MAPPER.readTree(string);
      return matched(jsonNode, mismatchDescription);
    } catch (IOException e) {
      mismatchDescription
          .appendText(" but the string was not valid JSON ")
          .appendValue(e.getMessage());
      return notMatched();
    }
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("A JSON string that matches ").appendDescriptionOf(matcher);
  }
}
