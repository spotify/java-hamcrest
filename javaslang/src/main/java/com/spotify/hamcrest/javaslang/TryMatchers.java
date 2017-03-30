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
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

// TODO: Add documentation
// TODO: Extract matchers to classes
// TODO: Test extracted matchers
public final class TryMatchers {

  private TryMatchers() {
  }

  public static <T> Matcher<Try<T>> successfulTry(Matcher<T> matcher) {
    return new TypeSafeDiagnosingMatcher<Try<T>>() {
      @Override
      public void describeTo(final Description description) {
        description.appendText("Try with value that ").appendDescriptionOf(matcher);
      }

      @Override
      protected boolean matchesSafely(final Try<T> item, final Description mismatchDescription) {
        return extractItem(item, mismatchDescription).matching(matcher, "was a Try with value ");
      }

      private Condition<T> extractItem(final Try<T> item, final Description mismatchDescription) {
        if (item.isFailure()) {
          mismatchDescription
              .appendText("was failed try with exception")
              .appendValue(item.getCause());
          return Condition.notMatched();
        }

        return Condition.matched(item.get(), mismatchDescription);
      }
    };
  }

  public static Matcher<Try<?>> failedTry(Matcher<Throwable> matcher) {
    return new TypeSafeDiagnosingMatcher<Try<?>>() {
      @Override
      protected boolean matchesSafely(final Try<?> item, final Description mismatchDescription) {
        return extractException(item, mismatchDescription)
            .matching(matcher, "was a failed Try with exception ");
      }

      private Condition<Throwable> extractException(final Try<?> item,
                                                    final Description mismatchDescription) {
        if (item.isSuccess()) {
          mismatchDescription.appendText("was success with value ").appendValue(item.get());
          return Condition.notMatched();
        }
        return Condition.matched(item.getCause(), mismatchDescription);
      }

      @Override
      public void describeTo(final Description description) {

      }
    };
  }

}
