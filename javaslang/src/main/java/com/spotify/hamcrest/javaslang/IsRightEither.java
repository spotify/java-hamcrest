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

class IsRightEither<R> extends TypeSafeDiagnosingMatcher<Either<?, R>> {
  private final Matcher<R> matcher;

  public IsRightEither(final Matcher<R> matcher) {
    this.matcher = matcher;
  }

  @Override
  protected boolean matchesSafely(final Either<?, R> item,
                                  final Description mismatchDescription) {
    return getRight(item, mismatchDescription)
        .matching(matcher, "was right with value that ");
  }

  private Condition<R> getRight(final Either<?, R> item, final Description mismatch) {
    item.left().peek(l -> mismatch.appendText("was left with value ").appendValue(l));
    return item.fold(
        l -> Condition.<R>notMatched(),
        r -> Condition.matched(r, mismatch));
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("a right Either that ").appendDescriptionOf(matcher);
  }
}
