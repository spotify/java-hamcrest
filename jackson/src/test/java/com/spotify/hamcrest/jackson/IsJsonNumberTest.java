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

import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonBigDecimal;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonBigInteger;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonDouble;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonFloat;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonInt;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonLong;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonNumber;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsJsonNumberTest {

  private static final JsonNodeFactory NF = JsonNodeFactory.instance;

  @Test
  public void testType() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber();

    assertThat(NF.numberNode(1), is(sut));
  }

  @Test
  public void testLiteralInt() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber(NF.numberNode(1));

    assertThat(NF.numberNode(1), is(sut));
  }

  @Test
  public void testLiteralLong() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber(NF.numberNode(1L));

    assertThat(NF.numberNode(1L), is(sut));
  }

  @Test
  public void testLiteralBigInteger() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber(BigIntegerNode.valueOf(BigInteger.ONE));

    assertThat(NF.numberNode(BigInteger.ONE), is(sut));
  }

  @Test
  public void testLiteralFloat() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber(NF.numberNode(1f));

    assertThat(NF.numberNode(1f), is(sut));
  }

  @Test
  public void testLiteralDouble() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber(NF.numberNode(1d));

    assertThat(NF.numberNode(1d), is(sut));
  }

  @Test
  public void testLiteralBigDecimal() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber(DecimalNode.valueOf(BigDecimal.ONE));

    assertThat(NF.numberNode(BigDecimal.ONE), is(sut));
  }

  @Test
  public void testMatchIntValue() throws Exception {
    final Matcher<JsonNode> sut = jsonInt(1);

    assertThat(NF.numberNode(1), is(sut));
  }

  @Test
  public void testMatchIntMatcher() throws Exception {
    final Matcher<JsonNode> sut = jsonInt(is(1));

    assertThat(NF.numberNode(1), is(sut));
  }

  @Test
  public void testMatchLongValue() throws Exception {
    final Matcher<JsonNode> sut = jsonLong(1L);

    assertThat(NF.numberNode(1), is(sut));
  }

  @Test
  public void testMatchLongMatcher() throws Exception {
    final Matcher<JsonNode> sut = jsonLong(is(1L));

    assertThat(NF.numberNode(1), is(sut));
  }

  @Test
  public void testMatchBigIntegerValue() throws Exception {
    final Matcher<JsonNode> sut = jsonBigInteger(BigInteger.ONE);

    assertThat(NF.numberNode(BigInteger.ONE), is(sut));
  }

  @Test
  public void testMatchBigIntegerMatcher() throws Exception {
    final Matcher<JsonNode> sut = jsonBigInteger(is(BigInteger.ONE));

    assertThat(NF.numberNode(BigInteger.ONE), is(sut));
  }

  @Test
  public void testMatchFloatValue() throws Exception {
    final Matcher<JsonNode> sut = jsonFloat(1f);

    assertThat(NF.numberNode(1f), is(sut));
  }

  @Test
  public void testMatchFloatMatcher() throws Exception {
    final Matcher<JsonNode> sut = jsonFloat(is(1f));

    assertThat(NF.numberNode(1f), is(sut));
  }

  @Test
  public void testMatchDoubleValue() throws Exception {
    final Matcher<JsonNode> sut = jsonDouble(1d);

    assertThat(NF.numberNode(1d), is(sut));
  }

  @Test
  public void testMatchDoubleMatcher() throws Exception {
    final Matcher<JsonNode> sut = jsonDouble(is(1d));

    assertThat(NF.numberNode(1d), is(sut));
  }

  @Test
  public void testMatchBigDecimalValue() throws Exception {
    final Matcher<JsonNode> sut = jsonBigDecimal(BigDecimal.ONE);

    assertThat(NF.numberNode(BigDecimal.ONE), is(sut));
  }

  @Test
  public void testMatchBigDecimalMatcher() throws Exception {
    final Matcher<JsonNode> sut = jsonBigDecimal(is(BigDecimal.ONE));

    assertThat(NF.numberNode(BigDecimal.ONE), is(sut));
  }

  @Test
  public void testMismatchValue() throws Exception {
    final Matcher<JsonNode> sut = jsonInt(1);

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.numberNode(2), description);

    assertThat(description.toString(), is("was a number node with value that was <2>"));
  }

  @Test
  public void testMismatchType() throws Exception {
    final Matcher<JsonNode> sut = jsonInt(1);

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.textNode("goat"), description);

    assertThat(description.toString(), is("was not a number node, but a string node"));
  }

  @Test
  public void testDescription() throws Exception {
    final Matcher<JsonNode> sut = jsonInt(1);

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is("a number node with value that is <1>"));
  }

  @Test
  public void testDescriptionForEmptyConstructor() throws Exception {
    final Matcher<JsonNode> sut = jsonNumber();

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is("a number node with value that is ANYTHING"));
  }
}
