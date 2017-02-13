/*-
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

package com.spotify.hamcrest.jackson;

import static com.spotify.hamcrest.jackson.IsJsonNumber.jsonInt;
import static com.spotify.hamcrest.jackson.IsJsonObject.jsonObject;
import static com.spotify.hamcrest.jackson.IsJsonStringMatching.isJsonStringMatching;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class IsJsonStringMatchingTest {

  @Test
  public void invalidJsonDoesNotMatch() throws Exception {
    final Matcher<String> sut = isJsonStringMatching(any(JsonNode.class));

    assertThat("{", not(sut));
  }

  @Test
  public void testDescription() throws Exception {
    final Matcher<String> sut = isJsonStringMatching(jsonObject());

    final Description description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "A JSON string that matches {\n"
        + "}")
    );
  }

  @Test
  public void testNull() throws Exception {
    final Matcher<String> sut = isJsonStringMatching(any(JsonNode.class));

    assertThat(null, not(sut));
  }

  @Test
  public void validJsonMatchesAnything() throws Exception {
    final Matcher<String> sut = isJsonStringMatching(any(JsonNode.class));

    assertThat("{}", sut);
  }

  @Test
  public void validJsonMatchesAnObject() throws Exception {
    assertThat("{}", isJsonStringMatching(jsonObject()));
  }

  @Test
  public void testJsonInt() throws Exception {
    assertThat("123", isJsonStringMatching(jsonInt(123)));
  }

  @Test
  public void invalidJsonDescription() throws Exception {
    final Matcher<String> sut = isJsonStringMatching(any(JsonNode.class));

    final Description description = new StringDescription();
    sut.describeMismatch("{", description);

    assertThat(description.toString(), containsString("but the string was not valid JSON"));
  }

}
