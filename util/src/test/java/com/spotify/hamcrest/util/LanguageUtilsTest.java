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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LanguageUtilsTest {

  @Test
  public void testAddArticleEmpty() throws Exception {
    assertThat(LanguageUtils.addArticle(""), is(""));
  }

  @Test
  public void testAddArticleVowel() throws Exception {
    assertThat(LanguageUtils.addArticle("attitude"), is("an attitude"));
    assertThat(LanguageUtils.addArticle("ear"), is("an ear"));
    assertThat(LanguageUtils.addArticle("igloo"), is("an igloo"));
    assertThat(LanguageUtils.addArticle("oar"), is("an oar"));
  }

  @Test
  public void testAddArticleConsonant() throws Exception {
    assertThat(LanguageUtils.addArticle("tower"), is("a tower"));
    assertThat(LanguageUtils.addArticle("sibling"), is("a sibling"));
    assertThat(LanguageUtils.addArticle("rotary dish"), is("a rotary dish"));
    assertThat(LanguageUtils.addArticle("user"), is("a user"));
  }
}
