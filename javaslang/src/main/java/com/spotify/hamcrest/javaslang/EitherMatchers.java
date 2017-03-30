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

// TODO: Add documentation
// TODO: Extract matchers to classes
// TODO: Test extracted matchers
public final class EitherMatchers {
  private EitherMatchers() {
  }

  public static <L, R> Matcher<Either<L, R>> right(Matcher<R> matcher) {
    return new TypeSafeDiagnosingMatcher<Either<L, R>>() {
      @Override
      protected boolean matchesSafely(final Either<L, R> item, final Description mismatchDescription) {
        return getRight(item, mismatchDescription).matching(matcher, "was right with value ");
      }

      private Condition<R> getRight(final Either<L, R> item, final Description mismatch) {
        item.left().peek(l -> mismatch.appendText("was left with value ").appendValue(l));
        return item.fold(l -> Condition.notMatched(),
                         r -> Condition.matched(r, mismatch));
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("a right Either that ").appendDescriptionOf(matcher);
      }
    };
  }

  public static <L, R> Matcher<Either<L, R>> left(Matcher<L> matcher) {
    return new TypeSafeDiagnosingMatcher<Either<L, R>>() {
      @Override
      protected boolean matchesSafely(final Either<L, R> item, final Description mismatchDescription) {
        return getLeft(item, mismatchDescription).matching(matcher, "was left with value ");
      }

      private Condition<L> getLeft(final Either<L, R> item, final Description mismatch) {
        item.right().peek(r -> mismatch.appendText("was right with value ").appendValue(r));
        return item.fold(l -> Condition.matched(l, mismatch),
                         r -> Condition.notMatched());
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("a left Either that ").appendDescriptionOf(matcher);
      }
    };
  }
}
