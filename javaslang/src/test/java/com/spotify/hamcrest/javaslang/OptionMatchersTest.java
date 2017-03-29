/*
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

package com.spotify.hamcrest.javaslang;

import javaslang.control.Option;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class OptionMatchersTest {

  @Test
  public void testEmptyOption() throws Exception {
    final Matcher<Option<String>> sut = OptionMatchers.emptyOption();

    assertThat(Option.none(), sut);
    assertThat(Option.of("hi"), not(sut));
  }

  @Test
  public void testDefinedOption() throws Exception {
    final Matcher<Option<?>> sut = OptionMatchers.definedOption();

    assertThat(Option.none(), not(sut));
    assertThat(Option.of("hi"), sut);
  }

  @Test
  public void testDefinedOptionWithValue() throws Exception {
    final Matcher<Option<?>> sut = OptionMatchers.definedOption(equalTo("hi"));

    assertThat(Option.none(), not(sut));
    assertThat(Option.of("hi"), sut);
  }

  @Test
  public void testNullDefinedOption() throws Exception {
    final Option<String> nullOption = Option.some(null);

    assertThat(nullOption, is(OptionMatchers.definedOption()));
    assertThat(nullOption, is(OptionMatchers.definedOption(nullValue())));
  }

  @Test
  public void testGenerics() throws Exception {
    // This test tests types are passed through to other generic matchers
    final Option<List<String>> option = Option.of(asList("a", "b", "c"));
    assertThat(option, OptionMatchers.definedOption(contains("a", "b", "c")));
  }
}
