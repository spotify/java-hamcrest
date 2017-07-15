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

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.spotify.hamcrest.util.DescriptionUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
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
    this.cls = Objects.requireNonNull(cls);
    this.methodMatchers = Objects.requireNonNull(methodMatchers);
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

  /**
   * Method uses serialization trick to extract information about lambda,
   * to give understandable name in case of mismatch.
   * @param lambda lambda to extract the name from
   * @return string describing class and method from which lambda was created,
   *     or simple {@code toString()} if that fails.
   */
  private String extractLambdaName(Object lambda) {
    try {
      // serializing lambda:
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(lambda);
      oos.flush();
      // replace class name to avoid special handling,
      // class name has to be identical, otherwise UTF8 serialization handling
      // will have to be changed too:
      byte[] hacked = new String(baos.toByteArray(), StandardCharsets.ISO_8859_1)
          .replace("java.lang.invoke.SerializedLambda",
                   "com.spotify.hamcrest.pojo.Lambda1")
          .getBytes(StandardCharsets.ISO_8859_1);

      final Lambda1 deserializedLambda = (Lambda1) new ObjectInputStream(
          new ByteArrayInputStream(hacked)).readObject();
      if (deserializedLambda.implClass != null && deserializedLambda.implMethodName != null) {
        return deserializedLambda.implClass
                   .substring(deserializedLambda.implClass.lastIndexOf('/') + 1)
               + "::"
               + deserializedLambda.implMethodName;
      }
    } catch (Exception ignore) {
      // nop
    }
    return lambda.toString();
  }

  @Override
  protected boolean matchesSafely(A item, Description mismatchDescription) {
    if (!cls.isInstance(item)) {
      mismatchDescription.appendText("not an instance of " + cls.getName());
      return false;
    }

    final Map<String, Consumer<Description>> mismatches = new LinkedHashMap<>();

    methodMatchers.forEach((valueSupplier, matcher) -> {
      try {
        Object value = valueSupplier.apply(item);
        if (!matcher.matches(value)) {
          mismatches.put(valueSupplier.toString(), d -> matcher.describeMismatch(value, d));
        }
      } catch (MethodValueSupplierException mvsex) {
        mismatches.put(valueSupplier.toString(), d -> d.appendText(mvsex.getDescription()));
      }
    });

    if (!mismatches.isEmpty()) {
      mismatchDescription.appendText(cls.getSimpleName()).appendText(" ");
      DescriptionUtils.describeNestedMismatches(
          methodMatchers.keySet()
              .stream()
              .map(Object::toString)
              .collect(Collectors.toCollection(LinkedHashSet::new)),
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

  private static class MethodValueSupplier<A> implements Function<A, Object> {

    private final String methodName;

    public MethodValueSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public Object apply(A item) {
      final Object returnValue;
      try {
        returnValue = item.getClass().getMethod(methodName).invoke(item);
        return returnValue;
      } catch (IllegalAccessException e) {
        // This only happens if the method has been removed from the class after the code was
        // compiled, so very unlikely...
        throw new MethodValueSupplierException(methodName, "was not accessible");
      } catch (InvocationTargetException e) {
        final Throwable cause = e.getCause();
        throw new MethodValueSupplierException(methodName, "threw an exception: "
                                                           + cause.getClass().getCanonicalName()
                                                           + ": "
                                                           + cause.getMessage());
      } catch (NoSuchMethodException e) {
        throw new MethodValueSupplierException(methodName, "did not exist");
      }
    }

    @Override
    public String toString() {
      return methodName;
    }
  }

  private static class NamedLambdaValueSupplier<A> implements Function<A, Object> {

    private final String lambdaName;
    private final Function<A, ?> delegateFunction;

    public NamedLambdaValueSupplier(String lambdaName, Function<A, ?> delegateFunction) {
      this.lambdaName = lambdaName;
      this.delegateFunction = delegateFunction;
    }

    @Override
    public Object apply(A input) {
      return delegateFunction.apply(input);
    }

    @Override
    public String toString() {
      return lambdaName;
    }
  }

  private static class MethodValueSupplierException extends RuntimeException {

    private final String methodName;
    private final String description;

    public MethodValueSupplierException(String methodName, String description) {
      this.methodName = methodName;
      this.description = description;
    }

    public String getMethodName() {
      return methodName;
    }

    public String getDescription() {
      return description;
    }
  }

  /**
   * Adds serialization marker to {@code Function}.
   */
  public interface ValueProvider<A, T> extends Function<A, T>, Serializable {
  }

}
