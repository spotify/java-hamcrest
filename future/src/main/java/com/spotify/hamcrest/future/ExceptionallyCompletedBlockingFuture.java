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
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

class ExceptionallyCompletedBlockingFuture<T> extends TypeSafeDiagnosingMatcher<Future<T>> {

  private final Matcher<? extends Throwable> matcher;

  /**
   * Creates a new ExceptionallyCompletedBlockingFuture where the exception that the Future finished
   * with an exception that matches the given Matcher.
   */
  ExceptionallyCompletedBlockingFuture(final Matcher<? extends Throwable> matcher) {
    this.matcher = Objects.requireNonNull(matcher);
  }

  @Override
  protected boolean matchesSafely(final Future<T> future, final Description mismatchDescription) {
    try {
      final T item = future.get();
      mismatchDescription
          .appendText("a future that completed to a value that was ")
          .appendValue(item);
      return false;
    } catch (InterruptedException e) {
      mismatchDescription.appendText("a future that was interrupted");
      return false;
    } catch (CancellationException e) {
      mismatchDescription.appendText("a future that was cancelled");
      return false;
    } catch (ExecutionException e) {
      if (matcher.matches(e.getCause())) {
        return true;
      } else {
        mismatchDescription.appendText("a future completed exceptionally with ");
        matcher.describeMismatch(e.getCause(), mismatchDescription);
        return false;
      }
    }
  }

  @Override
  public void describeTo(final Description description) {
    description
        .appendText("a future that completed with an exception that ")
        .appendDescriptionOf(matcher);
  }
}
