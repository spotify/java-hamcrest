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

import static com.spotify.hamcrest.jackson.IsJsonArray.jsonArray;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonInt;
import static com.spotify.hamcrest.jackson.IsJsonText.jsonText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsJsonArrayTest {

  private static final JsonNodeFactory NF = JsonNodeFactory.instance;

  @Test
  public void testType() throws Exception {
    final Matcher<JsonNode> sut = jsonArray();

    assertThat(NF.arrayNode(), is(sut));
  }

  @Test
  public void testEmptyIterable() throws Exception {
    final Matcher<JsonNode> sut = jsonArray(emptyIterable());

    assertThat(NF.arrayNode(), is(sut));
  }

  @Test
  public void testEmpty() throws Exception {
    final Matcher<JsonNode> sut = jsonArray(empty());

    assertThat(NF.arrayNode(), is(sut));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testContains() throws Exception {
    final Matcher<JsonNode> sut = jsonArray(contains(jsonText("a"), jsonInt(1)));

    assertThat(NF.arrayNode().add("a").add(1), is(sut));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testContainsInAnyOrder() throws Exception {
    final Matcher<JsonNode> sut = jsonArray(containsInAnyOrder(jsonText("a"), jsonInt(1)));

    assertThat(NF.arrayNode().add(1).add("a"), is(sut));
  }

  @Test
  public void testMismatchElements() throws Exception {
    final Matcher<JsonNode> sut = jsonArray(contains(jsonText("a")));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.arrayNode().add(1), description);

    assertThat(description.toString(), is(
        "was an array node whose elements item 0: was not a string node, but a number node"
    ));
  }

  @Test
  public void testMismatchType() throws Exception {
    final Matcher<JsonNode> sut = jsonArray(contains(jsonText("a")));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.booleanNode(false), description);

    assertThat(description.toString(), is(
        "was not an array node, but a boolean node"
    ));
  }

  @Test
  public void testDescription() throws Exception {
    final Matcher<JsonNode> sut = jsonArray(is(anything()));

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "an array node whose elements is ANYTHING"
    ));
  }

  @Test
  public void testDescriptionForEmptyConstructor() throws Exception {
    final Matcher<JsonNode> sut = jsonArray();

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "an array node whose elements is ANYTHING"
    ));
  }
}
