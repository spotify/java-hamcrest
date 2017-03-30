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

import javaslang.control.Either;
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

class IsLeftEither<L> extends TypeSafeDiagnosingMatcher<Either<L, ?>> {
  private final Matcher<L> matcher;

  public IsLeftEither(final Matcher<L> matcher) {
    this.matcher = matcher;
  }

  @Override
  protected boolean matchesSafely(final Either<L, ?> item,
                                  final Description mismatchDescription) {
    return getLeft(item, mismatchDescription)
        .matching(matcher, "was left with value that ");
  }

  private Condition<L> getLeft(final Either<L, ?> item, final Description mismatch) {
    item.right().peek(r -> mismatch.appendText("was right with value ").appendValue(r));
    return item.fold(
        l -> Condition.matched(l, mismatch),
        r -> Condition.notMatched());
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("a left Either that ").appendDescriptionOf(matcher);
  }
}
