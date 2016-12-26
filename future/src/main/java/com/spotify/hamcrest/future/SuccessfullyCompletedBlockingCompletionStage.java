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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Creates a Matcher that matches a CompletionStage that has completed with a value that matches
 * the given Matcher. If the CompletionStage has not yet completed, this matcher waits for
 * it to finish.
 */
class SuccessfullyCompletedBlockingCompletionStage<T>
    extends TypeSafeDiagnosingMatcher<CompletionStage<? extends T>> {

  private final Matcher<T> matcher;

  SuccessfullyCompletedBlockingCompletionStage(final Matcher<T> matcher) {
    this.matcher = matcher;
  }

  @Override
  protected boolean matchesSafely(final CompletionStage<? extends T> future,
                                  final Description mismatchDescription) {
    try {
      final T item = future.toCompletableFuture().get();
      if (matcher.matches(item)) {
        return true;
      } else {
        mismatchDescription.appendText("a stage that completed with a value that ");
        matcher.describeMismatch(item, mismatchDescription);
        return false;
      }
    } catch (InterruptedException e) {
      mismatchDescription.appendText("a stage that was interrupted");
      return false;
    } catch (ExecutionException e) {
      mismatchDescription
          .appendText("a stage that completed exceptionally with ")
          .appendText(Utils.getStackTraceAsString(e.getCause()));
      return false;
    }
  }

  @Override
  public void describeTo(final Description description) {
    description
        .appendText("a stage that completed with a value that ")
        .appendDescriptionOf(matcher);
  }
}
