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
 * Class is a copy of {@code java.lang.invoke.SerializedLambda}
 * to deserialize lambda into values instead of back to lambda.<br/>
 * Full class name has to be identical length to {@code java.lang.invoke.SerializedLambda}.
 *
 * @see java.lang.invoke.SerializedLambda
 */
class Lambda1 implements Serializable {
  private static final long serialVersionUID = 8025925345765570181L;
  Class<?> capturingClass;
  String functionalInterfaceClass;
  String functionalInterfaceMethodName;
  String functionalInterfaceMethodSignature;
  String implClass;
  String implMethodName;
  String implMethodSignature;
  int implMethodKind;
  String instantiatedMethodType;
  Object[] capturedArgs;
}
