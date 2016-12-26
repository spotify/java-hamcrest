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

import static com.spotify.hamcrest.future.Utils.getStackTraceAsString;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Creates a Matcher that matches a CompletionStage that has completed with a value that matches
 * a given Matcher. A CompletionStage that is not yet completed will not be matched.
 */
class SuccessfullyCompletedCompletionStage<T>
    extends TypeSafeDiagnosingMatcher<CompletionStage<? extends T>> {

  private final Matcher<T> matcher;

  SuccessfullyCompletedCompletionStage(final Matcher<T> matcher) {
    this.matcher = matcher;
  }

  @Override
  protected boolean matchesSafely(final CompletionStage<? extends T> stage,
                                  final Description mismatchDescription) {
    final CompletableFuture<? extends T> future = stage.toCompletableFuture();
    if (future.isDone()) {
      if (future.isCancelled()) {
        mismatchDescription.appendText("a stage that was cancelled");
        return false;
      } else if (future.isCompletedExceptionally()) {
        try {
          future.getNow(null);
          throw new AssertionError(
              "This should never happen because the future has completed exceptionally.");
        } catch (CompletionException e) {
          mismatchDescription
              .appendText("a stage that completed exceptionally with ")
              .appendText(getStackTraceAsString(e.getCause()));
        }
        return false;
      } else {
        final T item = future.getNow(null);
        if (matcher.matches(item)) {
          return true;
        } else {
          mismatchDescription.appendText("a stage that completed to a value that ");
          matcher.describeMismatch(item, mismatchDescription);
          return false;
        }
      }
    } else {
      mismatchDescription.appendText("a stage that was not done");
      return false;
    }
  }

  @Override
  public void describeTo(final Description description) {
    description
        .appendText("a stage that completed to a value that ")
        .appendDescriptionOf(matcher);
  }
}
