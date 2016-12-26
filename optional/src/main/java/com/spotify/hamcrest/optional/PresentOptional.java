/*-
 * -\-\-
 * hamcrest-optional
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

package com.spotify.hamcrest.optional;

import java.util.Optional;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

class PresentOptional<T> extends TypeSafeDiagnosingMatcher<Optional<? extends T>> {

  private final Matcher<T> matcher;

  PresentOptional(final Matcher<T> matcher) {
    this.matcher = matcher;
  }

  @Override
  protected boolean matchesSafely(final Optional<? extends T> item,
                                  final Description mismatchDescription) {
    if (item.isPresent()) {
      if (matcher.matches(item.get())) {
        return true;
      } else {
        mismatchDescription.appendText("was an Optional whose value ");
        matcher.describeMismatch(item.get(), mismatchDescription);
        return false;
      }
    } else {
      mismatchDescription.appendText("was not present");
      return false;
    }
  }

  @Override
  public void describeTo(final Description description) {
    description
        .appendText("an Optional with a value that ")
        .appendDescriptionOf(matcher);
  }
}
