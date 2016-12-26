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

import static com.spotify.hamcrest.jackson.IsJsonText.jsonText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsJsonTextTest {

  private static final JsonNodeFactory NF = JsonNodeFactory.instance;

  @Test
  public void testType() throws Exception {
    final Matcher<JsonNode> sut = jsonText();

    assertThat(NF.textNode("foo"), is(sut));
  }

  @Test
  public void testString() throws Exception {
    final Matcher<JsonNode> sut = jsonText("foo");

    assertThat(NF.textNode("foo"), is(sut));
  }

  @Test
  public void testIsEmptyString() throws Exception {
    final Matcher<JsonNode> sut = jsonText(isEmptyString());

    assertThat(NF.textNode(""), is(sut));
  }

  @Test
  public void testIsEmptyOrNullString() throws Exception {
    final Matcher<JsonNode> sut = jsonText(isEmptyOrNullString());

    assertThat(NF.textNode(""), is(sut));
  }

  @Test
  public void testMismatchElements() throws Exception {
    final Matcher<JsonNode> sut = jsonText(is("a"));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.textNode("foo"), description);

    assertThat(description.toString(), is(
        "was a text node was \"foo\""
    ));
  }

  @Test
  public void testMismatchType() throws Exception {
    final Matcher<JsonNode> sut = jsonText(is("a"));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.booleanNode(false), description);

    assertThat(description.toString(), is(
        "was not a string node, but a boolean node"
    ));
  }

  @Test
  public void testDescription() throws Exception {
    final Matcher<JsonNode> sut = jsonText(is(anything()));

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "a text node is ANYTHING"
    ));
  }
}
