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

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class OptionalMatchersTest {

  @Test
  public void testPresent() {
    assertThat(Optional.of("x"), optionalWithValue());

    assertThat(Optional.of("x"), OptionalMatchers.optionalWithValue("x"));

    assertThat(Optional.of("x"), OptionalMatchers.optionalWithValue(equalTo("x")));
    assertThat(Optional.of("x"), OptionalMatchers.optionalWithValue(not(equalTo("a"))));

    assertThat(Optional.empty(), is(emptyOptional()));
  }

  /**
   * Ensure that OptionalMatchers.optionalWithValue(matcher) can be used with Matchers of other
   * generic types. This test is really verified at compile-time and not run-time.
   */
  @Test
  public void testGenerics() {
    final Optional<List<Integer>> opt = Optional.of(Arrays.asList(1, 2, 3, 4));
    assertThat(opt, OptionalMatchers.optionalWithValue(hasSize(4)));
  }
}
