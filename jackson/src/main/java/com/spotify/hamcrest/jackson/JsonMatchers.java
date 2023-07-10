/*-
 * -\-\-
 * hamcrest-jackson
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import org.hamcrest.Matcher;

/**
 * Methods to instantiate different JSON Matchers.
 *
 * <p>These are helper methods that can be called directly from their implementations as well.
 */
public final class JsonMatchers {

  private JsonMatchers() {}

  public static Matcher<JsonNode> jsonArray() {
    return IsJsonArray.jsonArray();
  }

  public static Matcher<JsonNode> jsonArray(
      Matcher<? super Collection<? extends JsonNode>> elementsMatcher) {
    return IsJsonArray.jsonArray(elementsMatcher);
  }

  public static Matcher<JsonNode> jsonArray(ArrayNode value) {
    return IsJsonArray.jsonArray(value);
  }

  public static Matcher<JsonNode> jsonBoolean() {
    return IsJsonBoolean.jsonBoolean();
  }

  public static Matcher<JsonNode> jsonBoolean(boolean bool) {
    return IsJsonBoolean.jsonBoolean(bool);
  }

  public static Matcher<JsonNode> jsonBoolean(Matcher<? super Boolean> booleanMatcher) {
    return IsJsonBoolean.jsonBoolean(booleanMatcher);
  }

  public static Matcher<JsonNode> jsonBoolean(BooleanNode value) {
    return IsJsonBoolean.jsonBoolean(value);
  }

  public static Matcher<JsonNode> jsonMissing() {
    return IsJsonMissing.jsonMissing();
  }

  public static Matcher<JsonNode> jsonMissing(MissingNode value) {
    return IsJsonMissing.jsonMissing(value);
  }

  public static Matcher<JsonNode> jsonNull() {
    return IsJsonNull.jsonNull();
  }

  public static Matcher<JsonNode> jsonNull(NullNode value) {
    return IsJsonNull.jsonNull(value);
  }

  public static Matcher<JsonNode> jsonNumber() {
    return IsJsonNumber.jsonNumber();
  }

  public static Matcher<JsonNode> jsonNumber(NumericNode value) {
    return IsJsonNumber.jsonNumber(value);
  }

  public static Matcher<JsonNode> jsonInt(int number) {
    return IsJsonNumber.jsonInt(number);
  }

  public static Matcher<JsonNode> jsonInt(Matcher<? super Integer> numberMatcher) {
    return IsJsonNumber.jsonInt(numberMatcher);
  }

  public static Matcher<JsonNode> jsonLong(long number) {
    return IsJsonNumber.jsonLong(number);
  }

  public static Matcher<JsonNode> jsonLong(Matcher<? super Long> numberMatcher) {
    return IsJsonNumber.jsonLong(numberMatcher);
  }

  public static Matcher<JsonNode> jsonBigInteger(BigInteger number) {
    return IsJsonNumber.jsonBigInteger(number);
  }

  public static Matcher<JsonNode> jsonBigInteger(Matcher<? super BigInteger> numberMatcher) {
    return IsJsonNumber.jsonBigInteger(numberMatcher);
  }

  public static Matcher<JsonNode> jsonFloat(float number) {
    return IsJsonNumber.jsonFloat(number);
  }

  public static Matcher<JsonNode> jsonFloat(Matcher<? super Float> numberMatcher) {
    return IsJsonNumber.jsonFloat(numberMatcher);
  }

  public static Matcher<JsonNode> jsonDouble(double number) {
    return IsJsonNumber.jsonDouble(number);
  }

  public static Matcher<JsonNode> jsonDouble(Matcher<? super Double> numberMatcher) {
    return IsJsonNumber.jsonDouble(numberMatcher);
  }

  public static Matcher<JsonNode> jsonBigDecimal(BigDecimal number) {
    return IsJsonNumber.jsonBigDecimal(number);
  }

  public static Matcher<JsonNode> jsonBigDecimal(Matcher<? super BigDecimal> numberMatcher) {
    return IsJsonNumber.jsonBigDecimal(numberMatcher);
  }

  public static IsJsonObject jsonObject() {
    return IsJsonObject.jsonObject();
  }

  public static IsJsonObject jsonObject(final ObjectNode objectNode) {
    return IsJsonObject.jsonObject(objectNode);
  }

  public static Matcher<String> isJsonStringMatching(final Matcher<JsonNode> matcher) {
    return IsJsonStringMatching.isJsonStringMatching(matcher);
  }

  public static Matcher<JsonNode> jsonText() {
    return IsJsonText.jsonText();
  }

  public static Matcher<JsonNode> jsonText(String text) {
    return IsJsonText.jsonText(text);
  }

  public static Matcher<JsonNode> jsonText(Matcher<? super String> textMatcher) {
    return IsJsonText.jsonText(textMatcher);
  }

  public static Matcher<JsonNode> jsonText(TextNode value) {
    return IsJsonText.jsonText(value);
  }
}
