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

/**
 * Matches JSON Number.
 *
 * @see #jsonNumber()
 * @see #jsonNumber(NumericNode)
 */
public class IsJsonNumber extends AbstractJsonNodeMatcher<NumericNode> {

  private final Matcher<?> numberMatcher;
  private final Function<NumericNode, Object> projection;

  private IsJsonNumber(
      final Matcher<?> numberMatcher, final Function<NumericNode, Object> projection) {
    super(JsonNodeType.NUMBER);
    this.numberMatcher = Objects.requireNonNull(numberMatcher);
    this.projection = Objects.requireNonNull(projection);
  }

  /**
   * Matches a JSON Number.
   *
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonNumber() {
    // Function.identity() doesn't work since types change
    return new IsJsonNumber(is(anything()), n -> n);
  }

  /**
   * Matches a JSON Number.
   *
   * @param value the JSON Number value to be matched.
   * @return the json number matcher.
   */
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

  /**
   * Matches a JSON Int.
   *
   * @param number the int to be matched.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonInt(int number) {
    return new IsJsonNumber(is(number), NumericNode::asInt);
  }

  /**
   * Matches a JSON Int.
   *
   * @param numberMatcher matcher for an integer value from a json value.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonInt(Matcher<? super Integer> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::asInt);
  }

  /**
   * Matches a JSON Long.
   *
   * @param number the long to be matched.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonLong(long number) {
    return new IsJsonNumber(is(number), NumericNode::asLong);
  }

  /**
   * Matches a JSON Long.
   *
   * @param numberMatcher matcher for an long value from a json value.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonLong(Matcher<? super Long> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::asLong);
  }

  /**
   * Matches a JSON Big Integer.
   *
   * @param number the big integer to be matched.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonBigInteger(BigInteger number) {
    return new IsJsonNumber(is(number), NumericNode::bigIntegerValue);
  }

  /**
   * Matches a JSON Long.
   *
   * @param numberMatcher matcher for a big integer value from a json value.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonBigInteger(Matcher<? super BigInteger> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::bigIntegerValue);
  }

  /**
   * Matches a JSON Float.
   *
   * @param number the float to be matched.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonFloat(float number) {
    return new IsJsonNumber(is(number), NumericNode::floatValue);
  }

  /**
   * Matches a JSON Float.
   *
   * @param numberMatcher matcher for a float value from a json value.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonFloat(Matcher<? super Float> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::floatValue);
  }

  /**
   * Matches a JSON Double.
   *
   * @param number the double to be matched.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonDouble(double number) {
    return new IsJsonNumber(is(number), NumericNode::asDouble);
  }

  /**
   * Matches a JSON Double.
   *
   * @param numberMatcher matcher for a double value from a json value.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonDouble(Matcher<? super Double> numberMatcher) {
    return new IsJsonNumber(numberMatcher, NumericNode::asDouble);
  }

  /**
   * Matches a JSON Big Decimal.
   *
   * @param number the big decimal to be matched.
   * @return the json number matcher.
   */
  public static Matcher<JsonNode> jsonBigDecimal(BigDecimal number) {
    return new IsJsonNumber(is(number), NumericNode::decimalValue);
  }

  /**
   * Matches a Big Decimal Long.
   *
   * @param numberMatcher matcher for a big decimal value from a json value.
   * @return the json number matcher.
   */
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
