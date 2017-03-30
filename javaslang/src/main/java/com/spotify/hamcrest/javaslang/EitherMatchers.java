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
import org.hamcrest.Matcher;

// TODO: Add documentation
public final class EitherMatchers {
  private EitherMatchers() {
  }

  public static <R> Matcher<Either<?, R>> right(Matcher<R> matcher) {
    return new IsRightEither<>(matcher);
  }

  public static <L> Matcher<Either<L, ?>> left(Matcher<L> matcher) {
    return new IsLeftEither<>(matcher);
  }

}
