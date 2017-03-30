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

class IsSuccessfulTry<T> extends TypeSafeDiagnosingMatcher<Try<T>> {
  private final Matcher<T> matcher;

  public IsSuccessfulTry(final Matcher<T> matcher) {
    this.matcher = matcher;
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("Try with value that ").appendDescriptionOf(matcher);
  }

  @Override
  protected boolean matchesSafely(final Try<T> item, final Description mismatchDescription) {
    return extractItem(item, mismatchDescription).matching(matcher, "was a Try with value that ");
  }

  private Condition<T> extractItem(final Try<T> item, final Description mismatchDescription) {
    if (item.isFailure()) {
      mismatchDescription
          .appendText("was failed try with exception ")
          .appendValue(item.getCause());
      return Condition.notMatched();
    }

    return Condition.matched(item.get(), mismatchDescription);
  }
}
