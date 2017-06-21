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

import static com.spotify.hamcrest.jackson.IsJsonBoolean.jsonBoolean;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsJsonBooleanTest {

  private static final JsonNodeFactory NF = JsonNodeFactory.instance;

  @Test
  public void testType() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean();

    assertThat(NF.booleanNode(false), is(sut));
  }

  @Test
  public void testLiteral() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean(NF.booleanNode(false));

    assertThat(NF.booleanNode(false), is(sut));
  }

  @Test
  public void testMatchValue() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean(false);

    assertThat(NF.booleanNode(false), is(sut));
  }

  @Test
  public void testMatchMatcher() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean(is(false));

    assertThat(NF.booleanNode(false), is(sut));
  }

  @Test
  public void testMismatchValue() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean(false);

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.booleanNode(true), description);

    assertThat(description.toString(), is(
        "was a boolean node with value that was <true>"
    ));
  }

  @Test
  public void testMismatchType() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean(false);

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.textNode("goat"), description);

    assertThat(description.toString(), is(
        "was not a boolean node, but a string node"
    ));
  }

  @Test
  public void testDescription() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean(false);

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "a boolean node with value that is <false>"
    ));
  }

  @Test
  public void testDescriptionForEmptyConstructor() throws Exception {
    final Matcher<JsonNode> sut = jsonBoolean();

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "a boolean node with value that is ANYTHING"
    ));
  }
}
