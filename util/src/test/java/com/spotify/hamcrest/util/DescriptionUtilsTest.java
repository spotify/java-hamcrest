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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.hamcrest.StringDescription;
import org.junit.Test;

public class DescriptionUtilsTest {

  @Test
  public void testIndentDescription() throws Exception {
    StringDescription innerDescription = new StringDescription();
    innerDescription.appendText("a\nb");

    StringDescription description = new StringDescription();
    DescriptionUtils.indentDescription(description, innerDescription);

    assertThat(description.toString(), is("a\n  b\n"));
  }

  @Test
  public void testIndentDescriptionNoExtraNewline() throws Exception {
    StringDescription innerDescription = new StringDescription();
    innerDescription.appendText("a\nb\n");

    StringDescription description = new StringDescription();
    DescriptionUtils.indentDescription(description, innerDescription);

    assertThat(description.toString(), is("a\n  b\n"));
  }
}
