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

public final class LanguageUtils {

  private LanguageUtils() {
    throw new IllegalAccessError("This class may not be instantiated.");
  }

  public static String addArticle(String word) {
    if (word.isEmpty()) {
      return "";
    }
    switch (word.charAt(0)) {
      case 'a':
      case 'e':
      case 'i':
      case 'o':
        return "an " + word;
      default:
        return "a " + word;
    }
  }
}
