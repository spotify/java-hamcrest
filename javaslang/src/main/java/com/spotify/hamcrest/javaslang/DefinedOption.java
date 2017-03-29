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

import javaslang.control.Option;
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.hamcrest.Condition.matched;
import static org.hamcrest.Condition.notMatched;

class DefinedOption<T> extends TypeSafeDiagnosingMatcher<Option<? extends T>> {
  private final Matcher<T> matcher;

  DefinedOption(final Matcher<T> matcher) {
    this.matcher = matcher;
  }

  @Override
  protected boolean matchesSafely(final Option<? extends T> item, final Description mismatchDescription) {
    return extractItem(item, mismatchDescription)
        .matching(matcher, "was defined with value ");
  }

  private Condition<T> extractItem(final Option<? extends T> item, final Description mismatchDescription) {
    if (item.isEmpty()) {
      mismatchDescription.appendText("was empty");
      return notMatched();
    }

    return matched(item.get(), mismatchDescription);
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("Option that is defined with value that ").appendDescriptionOf(matcher);
  }
}
