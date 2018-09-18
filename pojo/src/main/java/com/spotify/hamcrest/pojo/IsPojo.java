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

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

import com.google.auto.value.AutoValue;
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
import java.util.Optional;
import java.util.function.Consumer;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeDiagnosingMatcher;

@AutoValue
public abstract class IsPojo<A> extends TypeSafeDiagnosingMatcher<A> {

  IsPojo() {
    // Prevent outside instantiation.
  }

  abstract Class<A> cls();

  abstract ImmutableMap<String, MethodHandler<A, ?>> methodHandlers();

  public static <A> IsPojo<A> pojo(final Class<A> cls) {
    return builder(cls).build();
  }

  public <T> IsPojo<A> where(
      final String methodName,
      final Matcher<T> returnValueMatcher) {
    return where(
        methodName,
        self -> {
          final Method method = methodWithName(methodName, self);
          method.setAccessible(true);
          @SuppressWarnings("unchecked") final T returnValue = (T) method.invoke(self);
          return returnValue;
        },
        returnValueMatcher);
  }

  public <T> IsPojo<A> where(
      final MethodReference<A, T> methodReference,
      final Matcher<T> returnValueMatcher) {
    final SerializedLambda serializedLambda = serializeLambda(methodReference);

    ensureDirectMethodReference(serializedLambda);

    return where(
        serializedLambda.getImplMethodName(),
        methodReference,
        returnValueMatcher);
  }

  private <T> IsPojo<A> where(
      final String methodName,
      final MethodReference<A, T> valueExtractor,
      final Matcher<T> matcher) {

    return toBuilder()
        .methodHandler(methodName, MethodHandler.create(valueExtractor, matcher))
        .build();
  }

  private Method methodWithName(String methodName, A self) throws NoSuchMethodException {
    try {
      return self.getClass().getDeclaredMethod(methodName);
    } catch (NoSuchMethodException e) {
      return self.getClass().getMethod(methodName);
    }
  }

  public IsPojo<A> withProperty(String property, Matcher<?> valueMatcher) {
    return where("get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, property),
        valueMatcher);
  }

  private static <A> Builder<A> builder(final Class<A> cls) {
    return new AutoValue_IsPojo.Builder<A>().cls(cls);
  }

  abstract Builder<A> toBuilder();

  @AutoValue.Builder
  abstract static class Builder<A> {

    abstract Builder<A> cls(final Class<A> cls);

    abstract ImmutableMap.Builder<String, MethodHandler<A, ?>> methodHandlersBuilder();

    Builder<A> methodHandler(final String methodName, final MethodHandler<A, ?> handler) {
      methodHandlersBuilder().put(methodName, handler);
      return this;
    }

    abstract IsPojo<A> build();
  }

  @Override
  protected boolean matchesSafely(A item, Description mismatchDescription) {
    if (!cls().isInstance(item)) {
      mismatchDescription.appendText("not an instance of " + cls().getName());
      return false;
    }

    final Map<String, Consumer<Description>> mismatches = new LinkedHashMap<>();

    methodHandlers().forEach(
        (methodName, handler) ->
            matchMethod(item, handler).ifPresent(descriptionConsumer ->
                mismatches.put(methodName, descriptionConsumer)));

    if (!mismatches.isEmpty()) {
      mismatchDescription.appendText(cls().getSimpleName()).appendText(" ");
      DescriptionUtils.describeNestedMismatches(
          methodHandlers().keySet(),
          mismatchDescription,
          mismatches,
          IsPojo::describeMethod);
      return false;
    }

    return true;
  }


  @Override
  public void describeTo(Description description) {
    description.appendText(cls().getSimpleName()).appendText(" {\n");

    methodHandlers().forEach((methodName, handler) -> {
      final Matcher<?> matcher = handler.matcher();

      description.appendText("  ").appendText(methodName).appendText("(): ");

      Description innerDescription = new StringDescription();
      matcher.describeTo(innerDescription);

      indentDescription(description, innerDescription);
    });
    description.appendText("}");
  }

  private static <A> Optional<Consumer<Description>> matchMethod(
      final A item,
      final MethodHandler<A, ?> handler) {
    final Matcher<?> matcher = handler.matcher();
    final MethodReference<A, ?> reference = handler.reference();

    try {
      final Object value = reference.apply(item);
      if (!matcher.matches(value)) {
        return Optional.of(d -> matcher.describeMismatch(value, d));
      } else {
        return Optional.empty();
      }
    } catch (IllegalAccessException e) {
      return Optional.of(d -> d.appendText("not accessible"));
    } catch (NoSuchMethodException e) {
      return Optional.of(d -> d.appendText("did not exist"));
    } catch (InvocationTargetException e) {
      final Throwable cause = e.getCause();
      return Optional
          .of(d -> d.appendText("threw an exception: ")
              .appendText(cause.getClass().getCanonicalName())
              .appendText(": ").appendText(cause.getMessage()));
    } catch (Exception e) {
      return Optional
          .of(d -> d.appendText("threw an exception: ")
              .appendText(e.getClass().getCanonicalName())
              .appendText(": ").appendText(e.getMessage()));
    }
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
    try {
      final Class<?> implClass = Class.forName(serializedLambda.getImplClass().replace('/', '.'));
      if (stream(implClass.getMethods())
          .noneMatch(m ->
              m.getName().equals(serializedLambda.getImplMethodName())
              && !m.isSynthetic())) {
        throw new IllegalArgumentException("The supplied lambda is not a direct method reference");
      }
    } catch (final ClassNotFoundException e) {
      throw new IllegalStateException(
          "serializeLambda returned a SerializedLambda pointing to an invalid class", e);
    }
  }

  @AutoValue
  abstract static class MethodHandler<A, T> {

    abstract MethodReference<A, T> reference();

    abstract Matcher<T> matcher();

    static <A, T> MethodHandler<A, T> create(
        final MethodReference<A, T> reference,
        final Matcher<T> matcher) {
      return new AutoValue_IsPojo_MethodHandler<>(reference, matcher);
    }
  }
}
