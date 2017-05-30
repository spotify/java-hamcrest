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

import static org.hamcrest.core.IsAnything.anything;

import javaslang.control.Option;
import org.hamcrest.Matcher;

public final class OptionMatchers {
  private OptionMatchers() {
  }

  /**
   * Returns a matcher matching an {@link Option} that is empty.
   *
   * @param <T> The type of the {@link Option}
   * @return A matcher matching an {@link Option} that is empty
   */
  public static <T> Matcher<Option<T>> emptyOption() {
    return new IsEmptyOption<>();
  }

  /**
   * Returns a matcher matching an {@link Option} that has a defined value.
   * @return A matcher matching an {@link Option} that has a defined value
   */
  public static Matcher<Option<?>> definedOption() {
    return new IsDefinedOption<>(anything());
  }

  /**
   * Returns a matcher matching an {@link Option} defined with value matching matcher.
   * The matcher will not match an empty {@link Option}.
   * @param matcher The matcher for the Option's value
   * @param <T> The type of the {@link Option}
   * @return A matcher matching an {{@link Option} with a value matching matcher
   */
  public static <T> Matcher<Option<? extends T>> definedOption(final Matcher<T> matcher) {
    return new IsDefinedOption<>(matcher);
  }
}
