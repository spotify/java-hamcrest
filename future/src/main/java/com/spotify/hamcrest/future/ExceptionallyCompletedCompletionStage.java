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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Creates a Matcher that matches a CompletionStage that has completed with an exception that
 * matches the given Matcher. A CompletionStage that is not yet completed will not be matched.
 */
class ExceptionallyCompletedCompletionStage extends TypeSafeDiagnosingMatcher<CompletionStage<?>> {

  private final Matcher<? extends Throwable> matcher;

  ExceptionallyCompletedCompletionStage(final Matcher<? extends Throwable> matcher) {
    this.matcher = Objects.requireNonNull(matcher);
  }

  @Override
  protected boolean matchesSafely(final CompletionStage<?> stage,
                                  final Description mismatchDescription) {
    final CompletableFuture<?> future = stage.toCompletableFuture();
    if (future.isDone()) {
      if (future.isCancelled()) {
        mismatchDescription.appendText("a stage that was cancelled");
        return false;
      } else if (future.isCompletedExceptionally()) {
        try {
          future.getNow(null);
          throw new AssertionError(
              "This should never happen because the stage completed exceptionally.");
        } catch (CompletionException e) {
          if (matcher.matches(e.getCause())) {
            return true;
          } else {
            mismatchDescription.appendText("a stage completed exceptionally with ");
            matcher.describeMismatch(e.getCause(), mismatchDescription);
            return false;
          }
        }
      } else {
        mismatchDescription
            .appendText("a stage that completed to a value that was ")
            .appendValue(future.getNow(null));
        return false;
      }
    } else {
      mismatchDescription.appendText("a stage that was not completed");
      return false;
    }
  }

  @Override
  public void describeTo(final Description description) {
    description
        .appendText("a stage that completed with an exception that ")
        .appendDescriptionOf(matcher);
  }
}
