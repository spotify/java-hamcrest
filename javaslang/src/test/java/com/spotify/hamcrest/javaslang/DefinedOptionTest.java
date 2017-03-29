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
import org.hamcrest.StringDescription;
import org.junit.Test;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class DefinedOptionTest {
  private DefinedOption<Integer> sut = new DefinedOption<>(is(42));
  private StringDescription description = new StringDescription();

  @Test
  public void testDescription() throws Exception {
    sut.describeTo(description);
    assertThat(description.toString(), is("Option that is defined with value that is <42>"));
  }

  @Test
  public void testMismatchFormattingWhenEmpty() throws Exception {
    sut.describeMismatch(Option.none(), description);
    assertThat(description.toString(), is("was empty"));
  }

  @Test
  public void testMismatchFormattingWithMismatchedValue() throws Exception {
    sut.describeMismatch(Option.of(0), description);
    assertThat(description.toString(), is("was defined with value was <0>"));
  }

  @Test
  public void testNoMismatchDescriptionForMatch() throws Exception {
    sut.describeMismatch(Option.of(42), description);
    assertThat(description.toString(), is(emptyString()));
  }
}
