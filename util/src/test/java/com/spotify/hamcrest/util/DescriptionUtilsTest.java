/*-
 * -\-\-
 * hamcrest-util
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

package com.spotify.hamcrest.util;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class DescriptionUtilsTest {

  @Test
  public void testIndentDescription() throws Exception {
    StringDescription innerDescription = new StringDescription();
    innerDescription.appendText("a\nb");

    StringDescription description = new StringDescription();
    DescriptionUtils.indentDescription(description, innerDescription);

    assertThat(description.toString(), is("a\n  b\n"));
  }

  @Test
  public void testIndentDescriptionNoExtraNewline() throws Exception {
    StringDescription innerDescription = new StringDescription();
    innerDescription.appendText("a\nb\n");

    StringDescription description = new StringDescription();
    DescriptionUtils.indentDescription(description, innerDescription);

    assertThat(description.toString(), is("a\n  b\n"));
  }

  @Test
  public void describeNestedMismatchesNoEllipsisBeforeFirstValue() throws Exception {
    Set<String> allKeys = new LinkedHashSet<>(asList("first", "second", "third"));
    StringDescription description = new StringDescription();
    Map<String, Consumer<Description>> mismatchedKeys =
        ImmutableMap.of("first", desc -> desc.appendText("mismatch!"));
    BiConsumer<String, Description> describeKey = (str, desc) -> desc.appendText(str);

    DescriptionUtils.describeNestedMismatches(allKeys, description, mismatchedKeys, describeKey);

    assertThat(description.toString(), is(
        "{\n"
            + "  first: mismatch!\n"
            + "  ...\n"
            + "}"
    ));
  }

  @Test
  public void describeMismatchesNoEllipsisAfterLastValue() throws Exception {
    Set<String> allKeys = new LinkedHashSet<>(asList("first", "second", "third"));
    StringDescription description = new StringDescription();
    Map<String, Consumer<Description>> mismatchedKeys =
        ImmutableMap.of("third", desc -> desc.appendText("mismatch!"));
    BiConsumer<String, Description> describeKey = (str, desc) -> desc.appendText(str);

    DescriptionUtils.describeNestedMismatches(allKeys, description, mismatchedKeys, describeKey);

    assertThat(description.toString(), is(
        "{\n"
            + "  ...\n"
            + "  third: mismatch!\n"
            + "}"
    ));
  }

  @Test
  public void describeNestedMismatchesEllipsisBeforeAndAfterAMiddleElement() throws Exception {
    Set<String> allKeys = new LinkedHashSet<>(asList("first", "second", "third"));
    StringDescription description = new StringDescription();
    Map<String, Consumer<Description>> mismatchedKeys =
        ImmutableMap.of("second", desc -> desc.appendText("mismatch!"));
    BiConsumer<String, Description> describeKey = (str, desc) -> desc.appendText(str);

    DescriptionUtils.describeNestedMismatches(allKeys, description, mismatchedKeys, describeKey);

    assertThat(description.toString(), is(
        "{\n"
            + "  ...\n"
            + "  second: mismatch!\n"
            + "  ...\n"
            + "}"
    ));
  }

  @Test
  public void describeNestMismatchesNoEllipsisBetweenConsecutiveMismatches() throws Exception {
    Set<String> allKeys = new LinkedHashSet<>(asList("first", "second", "third", "forth"));
    StringDescription description = new StringDescription();
    Map<String, Consumer<Description>> mismatchedKeys = ImmutableMap.of(
        "second", desc -> desc.appendText("mismatch!"),
        "third", desc -> desc.appendText("mismatch!"));
    BiConsumer<String, Description> describeKey = (str, desc) -> desc.appendText(str);

    DescriptionUtils.describeNestedMismatches(allKeys, description, mismatchedKeys, describeKey);

    assertThat(description.toString(), is(
        "{\n"
            + "  ...\n"
            + "  second: mismatch!\n"
            + "  third: mismatch!\n"
            + "  ...\n"
            + "}"
    ));
  }

  @Test
  public void describeNestedMismatchesProperlyIndentsNestedMismatch() throws Exception {
    Set<String> allKeys = new LinkedHashSet<>(asList("first", "second", "third"));
    StringDescription description = new StringDescription();
    Map<String, Consumer<Description>> mismatchedKeys =
        ImmutableMap.of("second", desc -> desc.appendText("{\n  nestedKey: mismatch!\n}"));
    BiConsumer<String, Description> describeKey = (str, desc) -> desc.appendText(str);

    DescriptionUtils.describeNestedMismatches(allKeys, description, mismatchedKeys, describeKey);

    assertThat(description.toString(), is(
        "{\n"
            + "  ...\n"
            + "  second: {\n"
            + "    nestedKey: mismatch!\n"
            + "  }\n"
            + "  ...\n"
            + "}"
    ));
  }
}
