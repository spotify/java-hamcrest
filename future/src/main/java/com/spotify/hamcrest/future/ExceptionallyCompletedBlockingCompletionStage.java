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
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Creates a Matcher that matches a CompletionStage that has completed with an exception that
 * matches the given Matcher. If the CompletionStage has not yet completed, this matcher waits for
 * it to finish.
 */
class ExceptionallyCompletedBlockingCompletionStage
    extends TypeSafeDiagnosingMatcher<CompletionStage<?>> {

  private final Matcher<? extends Throwable> matcher;

  ExceptionallyCompletedBlockingCompletionStage(final Matcher<? extends Throwable> matcher) {
    this.matcher = Objects.requireNonNull(matcher);
  }

  @Override
  protected boolean matchesSafely(final CompletionStage<?> stage,
                                  final Description mismatchDescription) {
    try {
      final Object item = stage.toCompletableFuture().get();
      mismatchDescription
          .appendText("a stage that completed with a value that was ")
          .appendValue(item);
      return false;
    } catch (InterruptedException e) {
      mismatchDescription.appendText("a stage that was interrupted");
      return false;
    } catch (CancellationException e) {
      mismatchDescription.appendText("a stage that was cancelled");
      return false;
    } catch (ExecutionException e) {
      if (matcher.matches(e.getCause())) {
        return true;
      } else {
        mismatchDescription.appendText("a stage completed exceptionally with ");
        matcher.describeMismatch(e.getCause(), mismatchDescription);
        return false;
      }
    }
  }

  @Override
  public void describeTo(final Description description) {
    description
        .appendText("a stage completing with an exception that ")
        .appendDescriptionOf(matcher);
  }
}
