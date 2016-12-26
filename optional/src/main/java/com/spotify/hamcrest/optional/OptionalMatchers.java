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

import static org.hamcrest.CoreMatchers.anything;

import java.util.Optional;
import org.hamcrest.Matcher;

public final class OptionalMatchers {

  private OptionalMatchers() {
  }

  /**
   * Creates a Matcher that matches empty Optionals.
   */
  public static <T> Matcher<Optional<T>> emptyOptional() {
    return new EmptyOptional<>();
  }

  /**
   * Creates a Matcher that matches any Optional with a value.
   */
  public static Matcher<Optional<?>> optionalWithValue() {
    return optionalWithValue(anything());
  }

  /**
   * Creates a Matcher that matches an Optional with a value that matches the given Matcher.
   */
  public static <T> Matcher<Optional<? extends T>> optionalWithValue(final Matcher<T> matcher) {
    return new PresentOptional<>(matcher);
  }

}

