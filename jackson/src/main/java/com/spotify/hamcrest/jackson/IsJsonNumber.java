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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NumericNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IsJsonNumber extends AbstractJsonNodeMatcher<NumericNode> {

  private final Matcher<?> numberMatcher;
  private final Function<NumericNode, Object> projection;

  private IsJsonNumber(final Matcher<?> numberMatcher,
                       final Function<NumericNode, Object> projection) {
    super(JsonNodeType.NUMBER);
    this.numberMatcher = Objects.requireNonNull(numberMatcher);
    this.projection = Objects.requireNonNull(projection);
  }

  public static Matcher<JsonNode> jsonNumber() {
    // Function.identity() doesn't work since types change
    return new IsJsonNumber(is(anything()), n -> n);
  }

  public static Matcher<JsonNode> jsonNumber(final NumericNode value) {
    final JsonParser.NumberType numberType = value.numberType();
    switch (numberType) {
      case INT:
        return jsonInt(value.asInt());
      case LONG:
        return jsonLong(value.asLong());
      case BIG_INTEGER:
        return jsonBigInteger(value.bigIntegerValue());
      case FLOAT:
        return jsonFloat(value.floatValue());
      case DOUBLE:
        return jsonDouble(value.doubleValue());
      case BIG_DECIMAL:
        return jsonBigDecimal(value.decimalValue());
      default:
        throw new UnsupportedOperationException("Unsupported number type " + numberType);
    }
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

  public static Matcher<JsonNode> jsonBigInteger(BigInteger number) {
    return new IsJsonNumber(is(number), NumericNode::bigIntegerValue);
  }

  public static Matcher<JsonNode> jsonBigInteger(Matcher<? super BigInteger> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::bigIntegerValue);
  }

  public static Matcher<JsonNode> jsonFloat(float number) {
    return new IsJsonNumber(is(number), NumericNode::floatValue);
  }

  public static Matcher<JsonNode> jsonFloat(Matcher<? super Float> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::floatValue);
  }

  public static Matcher<JsonNode> jsonDouble(double number) {
    return new IsJsonNumber(is(number), NumericNode::asDouble);
  }

  public static Matcher<JsonNode> jsonDouble(Matcher<? super Double> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::asDouble);
  }

  public static Matcher<JsonNode> jsonBigDecimal(BigDecimal number) {
    return new IsJsonNumber(is(number), NumericNode::decimalValue);
  }

  public static Matcher<JsonNode> jsonBigDecimal(Matcher<? super BigDecimal> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::decimalValue);
  }

  @Override
  protected boolean matchesNode(NumericNode node, Description mismatchDescription) {
    final Object number = projection.apply(node);

    if (numberMatcher.matches(number)) {
      return true;
    } else {
      mismatchDescription.appendText("was a number node with value that ");
      numberMatcher.describeMismatch(number, mismatchDescription);
      return false;
    }
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("a number node with value that ").appendDescriptionOf(numberMatcher);
  }
}
