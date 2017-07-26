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

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

final class LambdaUtils {

  private LambdaUtils() {
  }

  /**
   * Method uses serialization trick to extract information about lambda,
   * to give understandable name in case of mismatch.
   *
   * @param lambda lambda to extract the name from
   * @return a serialized version of the lambda, containing useful information for introspection
   */
  static SerializedLambda serializeLambda(final Object lambda) {
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
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(
          "Could not serialize as a lambda (is it a lambda?): " + lambda, e);
    }
  }
}
