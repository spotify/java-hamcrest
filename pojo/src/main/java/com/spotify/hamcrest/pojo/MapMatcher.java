package com.spotify.hamcrest.pojo;

import java.lang.invoke.SerializedLambda;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class MapMatcher {
  public static <A, T> Matcher<A> map(final MethodReference<A, T> function, final Matcher<T> returnValueMatcher) {
    final String functionName = getFunctionDescription(function);

    return new FunctionMatcher<>(function, functionName, returnValueMatcher);
  }

  private static String getFunctionDescription(final MethodReference<?, ?> function) {
    try {
      final SerializedLambda lambda = IsPojo.serializeLambda(function);
      return prettyClass(lambda) + "." + prettyMethod(lambda);
    } catch (final Exception e) {
      return "function";
    }
  }

  private static String prettyMethod(final SerializedLambda lambda) {
    final String methodName = lambda.getImplMethodName();
    if (methodName.startsWith("lambda$")) {
      return "<lambda>";
    }
    return methodName;
  }

  private static String prettyClass(final SerializedLambda lambda) {
    final String className = lambda.getImplClass();
    final int i = className.lastIndexOf('/');
    if (i >= 0) {
      return className.substring(i + 1);
    }
    return className;
  }

  private static class FunctionMatcher<A, T> extends TypeSafeDiagnosingMatcher<A> {

    private final MethodReference<A, T> function;
    private final String functionName;
    private final Matcher<T> returnValueMatcher;

    FunctionMatcher(final MethodReference<A, T> function, final String functionName, final Matcher<T> returnValueMatcher) {
      this.function = function;
      this.functionName = functionName;
      this.returnValueMatcher = returnValueMatcher;
    }

    @Override
    protected boolean matchesSafely(final A value, final Description description) {
      try {
        final T returnValue;
        returnValue = function.apply(value);
        boolean returnMatch = returnValueMatcher.matches(returnValue);
        if (!returnMatch) {
          description.appendText(functionName).appendText("(").appendValue(value).appendText(") ");
          returnValueMatcher.describeMismatch(returnValue, description);
        }
        return returnMatch;
      } catch (final Exception e) {
        description.appendText(functionName)
            .appendText(" threw ").appendText(e.getClass().getSimpleName())
            .appendText(" with message ").appendValue(e.getMessage());
        return false;
      }
    }

    @Override
    public void describeTo(final Description description) {
      description.appendText(functionName).appendText("(value) = ");
      description.appendDescriptionOf(this.returnValueMatcher);
    }
  }
}
