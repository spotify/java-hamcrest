/*-
 * -\-\-
 * hamcrest-pojo
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

package com.spotify.hamcrest.pojo;

import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsAnything.anything;

import java.math.BigInteger;
import org.hamcrest.StringDescription;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class IsPojoTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testWhere() throws Exception {
    final IsPojo<SomeClass> sut =
        pojo(SomeClass.class)
            .where("foo", is(42));

    assertThat(new SomeClass(), is(sut));
  }

  @Test
  public void testWhereMultiple() throws Exception {
    final IsPojo<SomeClass> sut =
        pojo(SomeClass.class)
            .where("foo", is(42))
            .where("getBar", is("bar"));

    assertThat(new SomeClass(), is(sut));
  }

  @Test
  public void testProp() throws Exception {
    final IsPojo<SomeClass> sut =
        pojo(SomeClass.class)
            .withProperty("bar", is("bar"));

    assertThat(new SomeClass(), is(sut));
  }

  @Test
  public void testNested() throws Exception {
    final IsPojo<SomeClass> sut = pojo(SomeClass.class)
        .where("baz", is(
            pojo(SomeClass.class)
                .where("foo", is(42))
        ));

    assertThat(new SomeClass(), is(sut));
  }

  @Test
  public void testDescriptionFormatting() throws Exception {

    final IsPojo<SomeClass> sut = pojo(SomeClass.class)
        .where("baz", is(pojo(SomeClass.class).where("foo", is(42))))
        .where("foo", is(42))
        .withProperty("bar", is("bar"));

    final StringDescription description = new StringDescription();
    sut.describeTo(description);

    assertThat(description.toString(), is(
        "SomeClass {\n"
        + "  baz(): is SomeClass {\n"
        + "    foo(): is <42>\n"
        + "  }\n"
        + "  foo(): is <42>\n"
        + "  getBar(): is \"bar\"\n"
        + "}"
    ));
  }

  @Test
  public void testMismatchFormatting() throws Exception {
    final IsPojo<SomeClass> sut = pojo(SomeClass.class)
        .where("baz", is(
            pojo(SomeClass.class)
                .where("foo", is(43))
        ))
        .where("foo", is(42))
        .withProperty("bar", is("bar"));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(new SomeClass(), description);

    assertThat(description.toString(), is(
        "SomeClass {\n"
        + "  ...\n"
        + "  baz(): SomeClass {\n"
        + "    ...\n"
        + "    foo(): was <42>\n"
        + "    ...\n"
        + "  }\n"
        + "  ...\n"
        + "}"
    ));
  }

  @Test
  public void testThrowsException() throws Exception {
    final IsPojo<SomeClass> sut = pojo(SomeClass.class)
        .where("throwsException", is(anything()));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(new SomeClass(), description);

    assertThat(description.toString(), is(
        "SomeClass {\n"
        + "  ...\n"
        + "  throwsException(): threw an exception: java.lang.RuntimeException: Error!\n"
        + "  ...\n"
        + "}"
    ));
  }

  @Test
  public void testNoSuchMethod() throws Exception {
    final IsPojo<SomeClass> sut = pojo(SomeClass.class)
        .where("doesNotExist", is(anything()));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(new SomeClass(), description);

    assertThat(description.toString(), is(
        "SomeClass {\n"
        + "  ...\n"
        + "  doesNotExist(): did not exist\n"
        + "  ...\n"
        + "}"
    ));
  }

  @Test
  public void testWrongType() throws Exception {
    final IsPojo<SomeClass> sut = pojo(SomeClass.class)
        .where("doesNotExist", is(anything()));

    final StringDescription description = new StringDescription();
    sut.describeMismatch(new BigInteger("1"), description);

    assertThat(description.toString(), is(
        "not an instance of com.spotify.hamcrest.pojo.SomeClass"
    ));
  }
}
