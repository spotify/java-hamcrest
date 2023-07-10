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
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

import java.util.concurrent.CompletionStage;
import org.hamcrest.Matcher;

/**
 * Matchers for {@link java.util.concurrent.CompletionStage} instances.
 *
 * <p>See also {@link FutureMatchers} for similar matchers against Future instances - because the
 * {@link CompletionStage} interface does not extend the Future interface, we need separate method
 * definitions.
 */
public final class CompletableFutureMatchers {

  private CompletableFutureMatchers() {}

  /**
   * Creates a {@link Matcher} that matches a {@link CompletionStage} that has completed with an
   * exception. A {@link CompletionStage} that is not yet completed will not be matched.
   */
  public static Matcher<CompletionStage<?>> stageCompletedWithException() {
    return stageCompletedWithExceptionThat(is(any(Throwable.class)));
  }

  /**
   * Creates a {@link Matcher} that matches a {@link CompletionStage} that has completed with an
   * exception that matches the given Matcher. A {@link CompletionStage} that is not yet completed
   * will not be matched.
   */
  public static Matcher<CompletionStage<?>> stageCompletedWithExceptionThat(
      final Matcher<? extends Throwable> matcher) {
    return new ExceptionallyCompletedCompletionStage(matcher);
  }

  /**
   * Creates a {@link Matcher} that matches a {@link CompletionStage} that has completed with a
   * value. A {@link CompletionStage} that is not yet completed will not be matched.
   */
  public static Matcher<CompletionStage<?>> stageCompletedWithValue() {
    return stageCompletedWithValueThat(anything());
  }

  /**
   * Creates a {@link Matcher} that matches a {@link CompletionStage} that has completed with a
   * value that matches a given Matcher. A {@link CompletionStage} that is not yet completed will
   * not be matched.
   */
  public static <T> Matcher<CompletionStage<? extends T>> stageCompletedWithValueThat(
      final Matcher<T> matcher) {
    return new SuccessfullyCompletedCompletionStage<>(matcher);
  }

  /**
   * Creates a {@link Matcher} that matches when the {@link CompletionStage} completes with a value.
   *
   * <p><strong>If the {@link CompletionStage} has not yet completed, this matcher waits for it to
   * finish.</strong>
   */
  public static Matcher<CompletionStage<?>> stageWillCompleteWithValue() {
    return stageWillCompleteWithValueThat(anything());
  }

  /**
   * Creates a {@link Matcher} that matches when the {@link CompletionStage} completes with a value
   * that matches the given Matcher.
   *
   * <p><strong>If the {@link CompletionStage} has not yet completed, this matcher waits for it to
   * finish.</strong>
   */
  public static <T> Matcher<CompletionStage<? extends T>> stageWillCompleteWithValueThat(
      final Matcher<T> matcher) {
    return new SuccessfullyCompletedBlockingCompletionStage<>(matcher);
  }

  /**
   * Creates a {@link Matcher} that matches when the {@link CompletionStage} completes with an
   * exception.
   *
   * <p><strong>If the {@link CompletionStage} has not yet completed, this matcher waits for it to
   * finish.</strong>
   */
  public static Matcher<CompletionStage<?>> stageWillCompleteWithException() {
    return stageWillCompleteWithExceptionThat(is(any(Throwable.class)));
  }

  /**
   * Creates a {@link Matcher} that matches when the {@link CompletionStage} completes with an
   * exception.
   *
   * <p><strong>If the {@link CompletionStage} has not yet completed, this matcher waits for it to
   * finish.</strong>
   */
  public static Matcher<CompletionStage<?>> stageWillCompleteWithExceptionThat(
      final Matcher<? extends Throwable> matcher) {
    return new ExceptionallyCompletedBlockingCompletionStage(matcher);
  }
}
