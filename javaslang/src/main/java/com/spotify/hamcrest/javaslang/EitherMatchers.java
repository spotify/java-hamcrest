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

import javaslang.control.Either;
import org.hamcrest.Matcher;

public final class EitherMatchers {
  private EitherMatchers() {
  }

  /**
   * Creates a {@link Matcher} that matches an {@link Either} that has right value matching the
   * matcher.
   *
   * <p>For example:
   * <pre>assertThat(Either.right(42), right(equalTo(42))</pre>
   *
   * @param matcher The {@link Matcher} the right value should match
   * @param <R>     The type of the right value of the {@link Either} to be matched
   * @return A new {@link Matcher} that matches an {@link Either} that has right value matching the
   * matcher.
   */
  public static <R> Matcher<Either<?, R>> right(Matcher<R> matcher) {
    return new IsRightEither<>(matcher);
  }

  /**
   * Creates a {@link Matcher} that matches an {@link Either} that has left value matching the
   * matcher.
   *
   * <p>For example:
   * <pre>assertThat(Either.left(42), left(equalTo(42))</pre>
   *
   * @param matcher The {@link Matcher} the left value should match
   * @param <L>     The type of the left value of the {@link Either} to be matched
   * @return        A new {@link Matcher} that matches an {@link Either} that has left value matching the
   *                matcher.
   */
  public static <L> Matcher<Either<L, ?>> left(Matcher<L> matcher) {
    return new IsLeftEither<>(matcher);
  }
}
