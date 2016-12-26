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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import org.hamcrest.Description;

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
}
