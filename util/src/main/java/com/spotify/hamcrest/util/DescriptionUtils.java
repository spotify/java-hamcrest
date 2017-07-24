/*-
 * -\-\-
 * hamcrest-util
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

package com.spotify.hamcrest.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

public final class DescriptionUtils {

  private static final Splitter LINE_SPLITTER = Splitter.on('\n');
  private static final Joiner INDENTED_LINE_JOINER = Joiner.on("\n  ");

  private DescriptionUtils() {
    throw new IllegalAccessError("This class may not be instantiated.");
  }

  public static void indentDescription(Description description, Description innerDescription) {
    final Iterable<String> lines = LINE_SPLITTER.split(innerDescription.toString().trim());
    final String indentedLines = INDENTED_LINE_JOINER.join(lines);
    description.appendText(indentedLines).appendText("\n");
  }

  /**
   * Describes a nested mismatch, useful for nested types like Objects or Maps
   *
   * <p>This will print <b>all</b> mismatches occurring in a mismatch list, and properly
   * handle ellipsis (...). Order will also be maintained based on the order of the allKeys
   * set. To maintain input order, consider using {@link java.util.LinkedHashSet}
   * or {@link java.util.LinkedHashMap}
   *
   * <p>This will also handle proper indentation in the case of nesting.
   * Description will contain output that looks like,
   * <pre>
   *   {@code
   * {
   *   ...
   *   myKey: expected 1 but was 2
   *   myOtherKey: expected "hello" but was "world"
   *   ...
   *   nestingKey: {
   *     nestedKey: expected null
   *   }
   * }
   *   }
   * </pre>
   *
   * @param allKeys {@link Set} of all keys expecting to match
   * @param mismatchDescription The {@link Description} to write the output to
   * @param mismatchedKeys A {@link Map} of all keys mismatched. The value is a {@link Consumer}
   *                       which will write the describe the mismatch for that key
   * @param describeKey A {@link BiConsumer} used to describe the key
   */
  public static <T> void describeNestedMismatches(
      Set<T> allKeys,
      Description mismatchDescription,
      Map<T, Consumer<Description>> mismatchedKeys,
      BiConsumer<String, Description> describeKey) {
    checkArgument(!mismatchedKeys.isEmpty(), "mismatchKeys must not be empty");
    T previousMismatchKey = null;
    T previousKey = null;

    mismatchDescription.appendText("{\n");

    for (T key : allKeys) {
      if (mismatchedKeys.containsKey(key)) {
        // If this is not the first key and the previous key was not a mismatch then add ellipsis
        if (previousKey != null && !Objects.equals(previousMismatchKey, previousKey)) {
          mismatchDescription.appendText("  ...\n");
        }

        describeMismatchForKey(
            key,
            mismatchDescription,
            describeKey,
            mismatchedKeys.get(key));
        previousMismatchKey = key;
      }
      previousKey = key;
    }

    // If the last element was not a mismatch then add ellipsis
    if (!Objects.equals(previousMismatchKey, previousKey)) {
      mismatchDescription.appendText("  ...\n");
    }

    mismatchDescription.appendText("}");
  }

  private static <T> void describeMismatchForKey(T key,
                                             Description mismatchDescription,
                                             BiConsumer<String, Description> describeKey,
                                             Consumer<Description> innerAction) {

    mismatchDescription.appendText("  ");
    describeKey.accept(String.valueOf(key), mismatchDescription);
    mismatchDescription.appendText(": ");

    final Description innerDescription = new StringDescription();
    innerAction.accept(innerDescription);
    indentDescription(mismatchDescription, innerDescription);
  }
}
