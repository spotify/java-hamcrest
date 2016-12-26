/*-
 * -\-\-
 * hamcrest-future
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

package com.spotify.hamcrest.future;

import static org.hamcrest.CoreMatchers.any;

import java.util.concurrent.Future;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

public class FutureMatchers {

  private FutureMatchers() {
  }

  /**
   * Creates a Matcher that matches a Future that has completed with an exception. A
   * Future that is not yet completed will not be matched.
   */
  public static <T> Matcher<Future<T>> futureCompletedWithException() {
    return futureCompletedWithExceptionThat(any(Throwable.class));
  }

  /**
   * Creates a Matcher that matches a Future that has completed with an exception that
   * matches the given Matcher. A Future that is not yet completed will not be matched.
   */

  public static <T> Matcher<Future<T>> futureCompletedWithExceptionThat(
      final Matcher<? extends Throwable> matcher) {
    return new ExceptionallyCompletedFuture<>(matcher);
  }

  /**
   * Creates a Matcher that matches a Future that has completed with a value. A
   * Future that is not yet completed will not be matched.
   */
  public static Matcher<Future<?>> futureCompletedWithValue() {
    return futureCompletedWithValueThat(CoreMatchers.anything());
  }

  /**
   * Creates a Matcher that matches a Future that has completed with a value that matches
   * a given Matcher. A Future that is not yet completed will not be matched.
   */
  public static <T> Matcher<Future<? extends T>> futureCompletedWithValueThat(
      final Matcher<T> matcher) {
    return new SuccessfullyCompletedFuture<>(matcher);
  }

  /**
   * Creates a Matcher that matches a Future that has completed with an exception.
   *
   * <p><strong>If the Future has not yet completed, this matcher waits for it to finish.</strong>
   */
  public static <T> Matcher<Future<T>> futureWillCompleteWithException() {
    return futureWillCompleteWithExceptionThat(any(Throwable.class));
  }

  /**
   * Creates a Matcher that matches a Future that has completed with an exception that
   * matches the given Matcher.
   *
   * <p><strong>If the Future has not yet completed, this matcher waits for it to finish.</strong>
   */
  public static <T> Matcher<Future<T>> futureWillCompleteWithExceptionThat(
      final Matcher<? extends Throwable> matcher) {
    return new ExceptionallyCompletedBlockingFuture<>(matcher);
  }

  /**
   * Creates a Matcher that matches a Future that has completed with a value.
   *
   * <p><strong>If the Future has not yet completed, this matcher waits for it to finish.</strong>
   */
  public static Matcher<Future<?>> futureWillCompleteWithValue() {
    return futureWillCompleteWithValueThat(CoreMatchers.anything());
  }

  /**
   * Creates a Matcher that matches a Future that has completed with a value that matches
   * a given Matcher.
   *
   * <p><strong>If the Future has not yet completed, this matcher waits for it to finish.</strong>
   */
  public static <T> Matcher<Future<? extends T>> futureWillCompleteWithValueThat(
      final Matcher<T> matcher) {
    return new SuccessfullyCompletedBlockingFuture<>(matcher);
  }
}
