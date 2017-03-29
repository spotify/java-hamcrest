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
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

class EmptyOption<T> extends TypeSafeDiagnosingMatcher<Option<T>> {
  @Override
  protected boolean matchesSafely(final Option<T> item, final Description mismatchDescription) {
    return item
        .peek(v -> mismatchDescription.appendText("was defined with value ").appendValue(v))
        .isEmpty();
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("An Option that is empty");
  }
}
