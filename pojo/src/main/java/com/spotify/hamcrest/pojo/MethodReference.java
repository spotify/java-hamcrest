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

import java.io.Serializable;

/**
 * An interface for serializable method references. It is only valid to construct instances of this
 * interface with method references such as {@code Foo::bar}.
 */
@FunctionalInterface
public interface MethodReference<A, R> extends Serializable {

  /**
   * Applies this method reference to the specified owning object.
   *
   * @param self the owning object
   * @return the method result
   */
  R apply(A self) throws Exception;
}
