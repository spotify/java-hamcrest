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

import static com.spotify.hamcrest.jackson.IsJsonBoolean.jsonBoolean;
import static com.spotify.hamcrest.jackson.IsJsonNull.jsonNull;
import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonInt;
import static com.spotify.hamcrest.jackson.IsJsonObject.jsonObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsJsonObjectTest {

  private static final JsonNodeFactory NF = JsonNodeFactory.instance;

  @Test
  public void testType() throws Exception {
    final Matcher<JsonNode> sut = jsonObject();

    assertThat(NF.objectNode(), is(sut));
  }

  @Test
  public void testLiteral() throws Exception {
    final Matcher<JsonNode> sut = jsonObject(NF.objectNode().put("a", 1).put("b", false));

    assertThat(NF.objectNode().put("a", 1).put("b", false), is(sut));
  }

  @Test
  public void testField() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)));

    assertThat(NF.objectNode().put("foo", 1), is(sut));
  }

  @Test
  public void testFields() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonBoolean(false)));

    assertThat(NF.objectNode().put("foo", 1).put("bar", false), is(sut));
  }

  @Test
  public void testNested() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonBoolean(false)))
        .where("baz", is(jsonObject()
                             .where("foo", is(jsonNull()))));

    assertThat(NF.objectNode().put("foo", 1).put("bar", false)
                   .set("baz", NF.objectNode().set("foo", NF.nullNode())), is(sut));
  }

  @Test
  public void lastNodeMismatchHasNoTrailingEllipsisButHasLeading() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonBoolean(false)));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.objectNode().put("foo", 1).put("bar", true), description);

    assertThat(description.toString(), is(
        "{\n"
        + "  ...\n"
        + "  \"bar\": was a boolean node with value that was <true>\n"
        + "}"
    ));
  }

  @Test
  public void firstNodeMismatchHasNoLeadingEllipsisButWithTrailing() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonBoolean(false)));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.objectNode().put("foo", 2).put("bar", false), description);

    assertThat(description.toString(), is(
        "{\n"
            + "  \"foo\": was a number node with value that was <2>\n"
            + "  ...\n"
            + "}"
    ));
  }

  @Test
  public void allMismatchesWillHaveNoTrailingOrLeadingEllipsis() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonBoolean(false)));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.objectNode().put("foo", 2).put("bar", true), description);

    assertThat(description.toString(), is(
        "{\n"
            + "  \"foo\": was a number node with value that was <2>\n"
            + "  \"bar\": was a boolean node with value that was <true>\n"
            + "}"
    ));
  }

  @Test
  public void testMismatchNested() throws Exception {
    final Matcher<JsonNode> sut = is(
        jsonObject()
            .where("foo", is(jsonInt(1)))
            .where("bar", is(jsonBoolean(true)))
            .where("baz", is(
                jsonObject()
                    .where("foo", is(jsonNull())))));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.objectNode().put("foo", 1).put("bar", true)
                             .set("baz", NF.objectNode().set("foo", NF.booleanNode(false))),
                         description);

    assertThat(description.toString(), is(
        "{\n"
            + "  ...\n"
            + "  \"baz\": {\n"
            + "    \"foo\": was not a null node, but a boolean node\n"
            + "  }\n"
            + "}"
    ));
  }

  @Test
  public void testMismatchType() throws Exception {
    final Matcher<JsonNode> sut = jsonObject().where("foo", is(jsonNull()));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(NF.booleanNode(false), description);

    assertThat(description.toString(), is(
        "was not an object node, but a boolean node"
    ));
  }

  @Test
  public void testMultipleMismatchesReportsAllMismatches() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonInt(2)))
        .where("baz", is(jsonInt(3)));

    final ObjectNode nestedMismatches = NF.objectNode()
        .put("foo", -1)
        .put("bar", 2)
        .put("baz", "was string");

    final StringDescription description = new StringDescription();
    sut.describeMismatch(nestedMismatches, description);

    assertThat(description.toString(), is(
        "{\n"
            + "  \"foo\": was a number node with value that was <-1>\n"
            + "  ...\n"
            + "  \"baz\": was not a number node, but a string node\n"
            + "}"
    ));
  }

  @Test
  public void multipleConsecutiveMismatchesHaveNoEllipsis() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonInt(2)))
        .where("baz", is(jsonInt(3)));

    final ObjectNode nestedMismatches = NF.objectNode()
        .put("foo", -1)
        .put("bar", "was string")
        .put("baz", 3);

    final StringDescription description = new StringDescription();
    sut.describeMismatch(nestedMismatches, description);

    assertThat(description.toString(), is(
        "{\n"
            + "  \"foo\": was a number node with value that was <-1>\n"
            + "  \"bar\": was not a number node, but a string node\n"
            + "  ...\n"
            + "}"
    ));
  }

  @Test
  public void nonConsecutiveMismatchesSeparatedByEllipsis() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonInt(2)))
        .where("baz", is(jsonInt(3)))
        .where("qux", is(jsonInt(4)))
        .where("quux", is(jsonInt(5)));

    final ObjectNode nestedMismatches = NF.objectNode()
        .put("foo", -1)
        .put("bar", "was string")
        .put("baz", 3)
        .put("quux", 5);

    final StringDescription description = new StringDescription();
    sut.describeMismatch(nestedMismatches, description);

    assertThat(description.toString(), is(
        "{\n"
            + "  \"foo\": was a number node with value that was <-1>\n"
            + "  \"bar\": was not a number node, but a string node\n"
            + "  ...\n"
            + "  \"qux\": was not a number node, but a missing node\n"
            + "  ...\n"
            + "}"
    ));
  }

  @Test
  public void testMultipleMismatchesWithNestingReportsAllMismatches() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonObject()
                             .where("val", jsonBoolean(true))))
        .where("bar", is(jsonInt(2)))
        .where("baz", is(jsonInt(3)));

    final ObjectNode nestedMismatches = NF.objectNode()
        .put("bar", "was string")
        .put("baz", 3);
    nestedMismatches.set("foo", NF.objectNode().put("val", false));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(nestedMismatches, description);

    assertThat(description.toString(), is(
        "{\n"
            + "  \"foo\": {\n"
            + "    \"val\": was a boolean node with value that was <false>\n"
            + "  }\n"
            + "  \"bar\": was not a number node, but a string node\n"
            + "  ...\n"
            + "}"
    ));
  }

  @Test
  public void testDescription() throws Exception {
    final Matcher<JsonNode> sut = jsonObject()
        .where("foo", is(jsonInt(1)))
        .where("bar", is(jsonBoolean(false)))
        .where("baz", is(jsonObject()
                             .where("foo", is(jsonNull()))));

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "{\n"
        + "  \"foo\": is a number node with value that is <1>\n"
        + "  \"bar\": is a boolean node with value that is <false>\n"
        + "  \"baz\": is {\n"
        + "    \"foo\": is a null node\n"
        + "  }\n"
        + "}"
    ));
  }

}
