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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.Assert.assertThat;

import javaslang.control.Try;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsFailedTryTest {
  StringDescription description = new StringDescription();
  IsFailedTry sut = new IsFailedTry(is(instanceOf(NullPointerException.class)));

  @Test
  public void testDescription() throws Exception {
    sut.describeTo(description);
    assertThat(description.toString(),
        is("Failed Try with cause that is an instance of java.lang.NullPointerException"));
  }

  @Test
  public void testMismatchDescriptionSuccessfulTry() throws Exception {
    sut.describeMismatch(Try.success(42), description);
    assertThat(description.toString(), is("was successful with value <42>"));
  }

  @Test
  public void testMismatchDescriptionDifferentException() throws Exception {
    sut.describeMismatch(Try.failure(new IllegalArgumentException()), description);
    assertThat(description.toString(), is(
        "had cause <java.lang.IllegalArgumentException> is a java.lang.IllegalArgumentException"));
  }

  @Test
  public void testNoMismatchDescriptionIsMatches() throws Exception {
    sut.describeMismatch(Try.failure(new NullPointerException()), description);
    assertThat(description.toString(), is(emptyString()));
  }
}
