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

class IsFailedTry extends TypeSafeDiagnosingMatcher<Try<?>> {
  private final Matcher<Throwable> matcher;

  public IsFailedTry(final Matcher<Throwable> matcher) {
    this.matcher = matcher;
  }

  @Override
  protected boolean matchesSafely(final Try<?> item, final Description mismatchDescription) {
    return extractException(item, mismatchDescription)
        .matching(matcher, "had cause ");
  }

  private Condition<Throwable> extractException(final Try<?> item,
                                                final Description mismatchDescription) {
    return item
        .onSuccess(value ->
            mismatchDescription.appendText("was successful with value ").appendValue(value))
        .map(v -> Condition.<Throwable>notMatched())
        .getOrElseGet(cause -> Condition.matched(cause, mismatchDescription));
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("Failed Try with cause that ").appendDescriptionOf(matcher);
  }
}
