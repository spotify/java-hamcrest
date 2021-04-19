package com.spotify.hamcrest.pojo;

import static com.spotify.hamcrest.pojo.MapMatcher.map;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableList;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class MapMatcherTest {

  @Test
  public void testMatches() {
    assertThat("A string", map(s -> s.charAt(0), equalTo('B')));
  }

  @Test
  public void testFailure() {
    assertEquals("MapMatcherTest.<lambda>(\"A string\") was \"A\"", getMatcherFailure("A string", map(s -> s.charAt(0), equalTo('s'))));
  }

  @Test
  public void testNestedFailure() {
    assertEquals("MapMatcherTest.<lambda>(\"A string\") MapMatcherTest.<lambda>(\" string\") was \"string\"",
        getMatcherFailure("A string",
            map(s -> s.substring(1),
              map(s -> s.substring(1),
        equalTo("")))));
  }

  @Test
  public void testException() {
    assertNotThat("MapMatcherTest.<lambda> threw RuntimeException with message \"message\"",
        "A string",
        map(s -> {throw new RuntimeException("message");}, equalTo("")));
  }

  @Test
  public void testMethodReferenceFailure() {
    assertNotThat("String.length(\"A string\") was <8>", "A string", map(String::length, equalTo(1)));
  }

  @Test
  public void testNestedList() {
    assertThat(
        ImmutableList.of("hello", "world"),
        map(self -> self.iterator().next(), equalTo("hello")));
  }

  @Test
  public void testNestedListFailure() {
    assertNotThat("MapMatcherTest.<lambda>(<[hello, world]>) was \"hello\"",
        ImmutableList.of("hello", "world"),
        map(self -> self.iterator().next(), equalTo("")));
  }


  private static <T> void assertNotThat(final String expectedMessage, final T actual, final Matcher<T> matcher) {
    assertEquals(expectedMessage, getMatcherFailure(actual, matcher));
  }

  private static <T> String getMatcherFailure(final T input, final Matcher<T> matcher) {
    assertFalse(matcher.matches(input));
    Description description = new StringDescription();
    matcher.describeMismatch(input, description);
    return description.toString();
  }
}