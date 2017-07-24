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
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

final class LambdaUtils {
  /**
   * Method uses serialization trick to extract information about lambda,
   * to give understandable name in case of mismatch.
   * @param lambda lambda to extract the name from
   * @return string describing class and method from which lambda was created,
   *     or simple {@code toString()} if that fails.
   */
  public static String extractLambdaName(Object lambda) {
    requireNonNull(lambda);
    try {
      SerializedLambda serializedLambda = toSerializedLambda(lambda);
      return serializedLambda.getImplClass()
                 .substring(serializedLambda.getImplClass().lastIndexOf('/') + 1)
             + "::"
             + serializedLambda.getImplMethodName();
    } catch (Exception ignore) {
      ignore.printStackTrace();
      // nop
    }
    return lambda.toString();
  }

  private static SerializedLambda toSerializedLambda(Object lambda) throws Exception {
    Method writeReplace = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
      @Override
      public Method run() throws Exception {
        Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return method;
      }
    });
    SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);
    return serializedLambda;
  }

  private LambdaUtils() {
  }
}
