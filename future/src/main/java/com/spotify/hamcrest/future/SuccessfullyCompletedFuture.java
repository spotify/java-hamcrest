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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

class SuccessfullyCompletedFuture<T> extends TypeSafeDiagnosingMatcher<Future<? extends T>> {

  private final Matcher<T> matcher;

  /**
   * Creates a new SuccessfullyCompletedFuture that matches a completed future where the value
   * matches the given matcher.
   */
  SuccessfullyCompletedFuture(final Matcher<T> matcher) {
    this.matcher = Objects.requireNonNull(matcher);
  }

  @Override
  protected boolean matchesSafely(
      final Future<? extends T> future, final Description mismatchDescription) {
    if (future.isDone()) {
      if (future.isCancelled()) {
        mismatchDescription.appendText("a future that was cancelled");
        return false;
      } else {
        try {
          final T item = future.get();
          if (matcher.matches(item)) {
            return true;
          } else {
            mismatchDescription.appendText("a future that completed to a value that ");
            matcher.describeMismatch(item, mismatchDescription);
            return false;
          }
        } catch (InterruptedException e) {
          throw new AssertionError("This should never happen because the future is completed.");
        } catch (ExecutionException e) {
          mismatchDescription
              .appendText("a future that completed exceptionally with ")
              .appendText(Utils.getStackTraceAsString(e.getCause()));
          return false;
        }
      }
    } else {
      mismatchDescription.appendText("a future that was not completed");
      return false;
    }
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("a future that completed to a value that ").appendDescriptionOf(matcher);
  }
}
