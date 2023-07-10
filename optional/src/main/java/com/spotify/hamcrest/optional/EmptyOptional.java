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
import org.hamcrest.TypeSafeDiagnosingMatcher;

/** Matches an empty Optional. */
class EmptyOptional<T> extends TypeSafeDiagnosingMatcher<Optional<T>> {

  @Override
  protected boolean matchesSafely(final Optional<T> item, final Description mismatchDescription) {
    if (item.isPresent()) {
      mismatchDescription.appendText("was present with ").appendValue(item.get());
      return false;
    }
    return true;
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("an Optional that's empty");
  }
}
