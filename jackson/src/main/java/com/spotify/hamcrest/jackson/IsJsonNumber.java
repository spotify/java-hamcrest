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
import com.fasterxml.jackson.databind.node.NumericNode;
import java.util.function.Function;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IsJsonNumber extends AbstractJsonNodeMatcher<NumericNode> {

  private final Matcher<?> numberMatcher;
  private final Function<NumericNode, Object> projection;

  private IsJsonNumber(Matcher<?> numberMatcher, Function<NumericNode, Object> projection) {
    super(JsonNodeType.NUMBER);
    this.numberMatcher = numberMatcher;
    this.projection = projection;
  }

  public static Matcher<JsonNode> jsonNumber() {
    // Function.identity() doesn't work since types change
    return new IsJsonNumber(anything(), n -> n);
  }

  public static Matcher<JsonNode> jsonInt(int number) {
    return new IsJsonNumber(is(number), NumericNode::asInt);
  }

  public static Matcher<JsonNode> jsonInt(Matcher<? super Integer> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::asInt);
  }

  public static Matcher<JsonNode> jsonLong(long number) {
    return new IsJsonNumber(is(number), NumericNode::asLong);
  }

  public static Matcher<JsonNode> jsonLong(Matcher<? super Long> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::asLong);
  }

  public static Matcher<JsonNode> jsonDouble(double number) {
    return new IsJsonNumber(is(number), NumericNode::asDouble);
  }

  public static Matcher<JsonNode> jsonDouble(Matcher<? super Double> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::asDouble);
  }

  @Override
  protected boolean matchesNode(NumericNode node, Description mismatchDescription) {
    final Object number = projection.apply(node);

    if (numberMatcher.matches(number)) {
      return true;
    } else {
      mismatchDescription.appendText("was a number node ");
      numberMatcher.describeMismatch(number, mismatchDescription);
      return false;
    }
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("a number node ").appendDescriptionOf(numberMatcher);
  }
}
