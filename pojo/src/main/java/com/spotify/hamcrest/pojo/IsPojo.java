/*-
 * -\-\-
 * hamcrest-pojo
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

package com.spotify.hamcrest.pojo;

import static com.spotify.hamcrest.pojo.LambdaUtils.extractLambdaName;
import static java.util.Objects.requireNonNull;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.spotify.hamcrest.util.DescriptionUtils;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class IsPojo<A> extends TypeSafeDiagnosingMatcher<A> {

  private final Class<A> cls;
  private final ImmutableMap<Function<A, ?>, Matcher<?>> methodMatchers;

  private IsPojo(final Class<A> cls,
                 final ImmutableMap<Function<A, ?>, Matcher<?>> methodMatchers) {
    this.cls = requireNonNull(cls);
    this.methodMatchers = requireNonNull(methodMatchers);
  }

  public static <A> IsPojo<A> pojo(Class<A> cls) {
    return new IsPojo<>(cls, ImmutableMap.of());
  }

  public IsPojo<A> where(String methodName, Matcher<?> returnValueMatcher) {
    return whereWithoutTypeSafety(new MethodValueSupplier<>(methodName), returnValueMatcher);
  }

  public <T> IsPojo<A> where(ValueProvider<A, T> valueProvider, Matcher<T> valueMatcher) {
    return whereWithoutTypeSafety(
        new NamedLambdaValueSupplier<>(
            extractLambdaName(valueProvider),
            valueProvider),
        valueMatcher);
  }

  public IsPojo<A> withProperty(String property, Matcher<?> valueMatcher) {
    return where("get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, property),
                 valueMatcher);
  }

  private IsPojo<A> whereWithoutTypeSafety(Function<A, ?> valueSupplier, Matcher<?> valueMatcher) {
    return new IsPojo<>(cls,
                        ImmutableMap.<Function<A, ?>, Matcher<?>>builder()
                            .putAll(methodMatchers)
                            .put(valueSupplier, valueMatcher)
                            .build());
  }

  @Override
  protected boolean matchesSafely(A item, Description mismatchDescription) {
    if (!cls.isInstance(item)) {
      mismatchDescription.appendText("not an instance of " + cls.getName());
      return false;
    }

    final Map<Function<A, ?>, Consumer<Description>> mismatches = new LinkedHashMap<>();

    methodMatchers.forEach((valueSupplier, matcher) -> {
      try {
        Object value = valueSupplier.apply(item);
        if (!matcher.matches(value)) {
          mismatches.put(valueSupplier, d -> matcher.describeMismatch(value, d));
        }
      } catch (MethodValueSupplierException mvsex) {
        mismatches.put(valueSupplier, d -> d.appendText(mvsex.getDescription()));
      }
    });

    if (!mismatches.isEmpty()) {
      mismatchDescription.appendText(cls.getSimpleName()).appendText(" ");
      DescriptionUtils.describeNestedMismatches(
          methodMatchers.keySet(),
          mismatchDescription,
          mismatches,
          IsPojo::describeMethod);
      return false;
    }

    return true;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(cls.getSimpleName()).appendText(" {\n");

    methodMatchers.forEach((valueSupplier, matcher) -> {
      final String methodName = valueSupplier.toString();
      description.appendText("  ").appendText(methodName).appendText("(): ");

      Description innerDescription = new StringDescription();
      matcher.describeTo(innerDescription);

      indentDescription(description, innerDescription);
    });
    description.appendText("}");
  }


  private static void describeMethod(String name, Description description) {
    description.appendText(name).appendText("()");
  }

  private void indentDescription(Description description, Description innerDescription) {
    description
        .appendText(
            Joiner.on("\n  ").join(Splitter.on('\n').split(innerDescription.toString())))
        .appendText("\n");
  }

}
