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

import javaslang.control.Try;
import org.hamcrest.Matcher;

public final class TryMatchers {

  private TryMatchers() {
  }

  /**
   * Creates a {@link Matcher} matching a successful {@link Try} matching the matcher.
   *
   * <p>For example:
   * <pre>assertThat(Try.success(42), successfulTry(equalTo(42))</pre>
   *
   * @param matcher A {@link Matcher} to match against the Try's value
   * @param <T> The type of the {@link Try} value
   * @return A {@link Matcher} for a successful {@link Try} matching the matcher
   */
  public static <T> Matcher<Try<T>> successfulTry(Matcher<T> matcher) {
    return new IsSuccessfulTry<>(matcher);
  }

  /**
   * Creates a {@link Matcher} matching a failed {@link Try}'s cause.
   *
   * <p>For example:
   * <pre>assertThat(Try.failure(new NullPointerException(),
   *                 failedTry(instanceOf(NullPointerException.class))</pre>
   * @param matcher A {@link Matcher} to match the {@link Try}'s cause
   * @return A {@link Matcher} for a failed {@link Try} matching the matcher
   */
  public static Matcher<Try<?>> failedTry(Matcher<Throwable> matcher) {
    return new IsFailedTry(matcher);
  }
}
