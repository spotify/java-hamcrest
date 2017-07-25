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

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

class MethodValueSupplier<A> implements Function<A, Object> {

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
