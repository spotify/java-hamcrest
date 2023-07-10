/*-
 * -\-\-
 * hamcrest-optional
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

package com.spotify.hamcrest.optional;

import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class PresentOptionalTest {

  private static final Matcher<Optional<? extends Integer>> SUT = optionalWithValue(is(1));

  @Test
  public void testMismatchFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeMismatch(Optional.empty(), description);

    assertThat(description.toString(), is("was not present"));
  }

  @Test
  public void testValueMismatchFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeMismatch(Optional.of(2), description);

    assertThat(description.toString(), is("was an Optional whose value was <2>"));
  }

  @Test
  public void testDescriptionFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeTo(description);

    assertThat(description.toString(), is("an Optional with a value that is <1>"));
  }
}
