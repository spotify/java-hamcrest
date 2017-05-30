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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import javaslang.control.Try;
import org.hamcrest.Matcher;
import org.junit.Test;

public class TryMatchersTest {

  @Test
  public void testSuccessfulTry() throws Exception {
    final Matcher<Try<Integer>> sut = TryMatchers.successfulTry(is(42));

    assertThat(Try.success(42), sut);
    assertThat(Try.success(0), not(sut));
    assertThat(Try.failure(new Exception()), not(sut));
  }

  @Test
  public void testFailedTry() throws Exception {
    final Matcher<Try<?>> sut = TryMatchers.failedTry(instanceOf(NullPointerException.class));

    assertThat(Try.success(42), not(sut));
    assertThat(Try.failure(new Exception()), not(sut));
    assertThat(Try.failure(new NullPointerException()), is(sut));
  }
}
