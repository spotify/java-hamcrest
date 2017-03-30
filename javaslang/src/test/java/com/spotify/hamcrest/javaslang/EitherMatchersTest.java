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

import javaslang.collection.List;
import javaslang.control.Either;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class EitherMatchersTest {
  @Test
  public void testRightMatcher() throws Exception {
    Matcher<Either<String, Integer>> sut = EitherMatchers.right(is(42));

    assertThat(Either.right(42), sut);
    assertThat(Either.right(0), not(sut));
    assertThat(Either.left("hi"), not(sut));
  }

  @Test
  public void testLeftMatcher() throws Exception {
    Matcher<Either<String, Integer>> sut = EitherMatchers.left(is("hi"));

    assertThat(Either.left("hi"), sut);
    assertThat(Either.left("not hi"), not(sut));
    assertThat(Either.right(42), not(sut));
  }
}
