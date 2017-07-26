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

import static java.util.Objects.requireNonNull;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.spotify.hamcrest.util.DescriptionUtils;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
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
    final SerializedLambda serializedLambda = serializeLambda(valueProvider);

    ensureDirectMethodReference(serializedLambda);

    final String implMethodName = serializedLambda.getImplMethodName();
    return whereWithoutTypeSafety(
        new NamedLambdaValueSupplier<>(
            implMethodName,
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

  /**
   * Method uses serialization trick to extract information about lambda,
   * to give understandable name in case of mismatch.
   *
   * @param lambda lambda to extract the name from
   * @return a serialized version of the lambda, containing useful information for introspection
   */
  private static SerializedLambda serializeLambda(final Object lambda) {
    requireNonNull(lambda);

    final Method writeReplace;
    try {
      writeReplace = AccessController.doPrivileged((PrivilegedExceptionAction<Method>) () -> {
        Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return method;
      });
    } catch (PrivilegedActionException e) {
      throw new IllegalStateException("Cannot serialize lambdas in unprivileged context", e);
    }

    try {
      return (SerializedLambda) writeReplace.invoke(lambda);
    } catch (ClassCastException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException(
          "Could not serialize as a lambda (is it a lambda?): " + lambda, e);
    }
  }

  private static void ensureDirectMethodReference(final SerializedLambda serializedLambda) {
    final Method targetMethod;
    try {
      final Class<?> implClass = Class.forName(serializedLambda.getImplClass().replace('/', '.'));
      targetMethod = findMethodByName(implClass, serializedLambda.getImplMethodName());
    } catch (NoSuchMethodException | ClassNotFoundException e) {
      throw new IllegalStateException(
          "serializeLambda returned a SerializedLambda pointing to an invalid class/method", e);
    }

    if (targetMethod.isSynthetic()) {
      throw new IllegalArgumentException("The supplied lambda is not a direct method reference");
    }
  }

  private static Method findMethodByName(final Class<?> cls, final String methodName)
      throws NoSuchMethodException {
    for (final Method method : cls.getDeclaredMethods()) {
      if (method.getName().equals(methodName)) {
        return method;
      }
    }
    throw new NoSuchMethodException("No method " + methodName + " on " + cls);
  }
}
