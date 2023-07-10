/*-
 * -\-\-
 * hamcrest-future
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

package com.spotify.hamcrest.future;

import java.io.PrintWriter;
import java.io.StringWriter;

class Utils {

  private Utils() {}

  /**
   * Returns a string containing the result of {@link Throwable#toString() toString()}, followed by
   * the full, recursive stack trace of {@code throwable}. Note that you probably should not be
   * parsing the resulting string; if you need programmatic access to the stack frames, you can call
   * {@link Throwable#getStackTrace()}.
   *
   * <p>We copy/pasted this from Guava because we don't want to depend on their whole library. This
   * class is duplicated in this repo's other packages because we want to keep this class
   * package-private.
   */
  static String getStackTraceAsString(final Throwable throwable) {
    final StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
}
