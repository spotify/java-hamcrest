/*-
 * -\-\-
 * hamcrest-future
 * --
 * Copyright (C) 2016 Spotify AB
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

package com.spotify.hamcrest.future;

import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageCompletedWithException;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageCompletedWithExceptionThat;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageCompletedWithValue;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageCompletedWithValueThat;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageWillCompleteWithException;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageWillCompleteWithExceptionThat;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageWillCompleteWithValue;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageWillCompleteWithValueThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CompletableFuture;
import org.junit.Test;

public class CompletableFutureMatchersTest {

  @Test
  public void exceptional() {
    final RuntimeException ex = new RuntimeException("oops");

    final CompletableFuture<String> cf = new CompletableFuture<>();
    cf.completeExceptionally(ex);

    assertThat(cf, stageCompletedWithException());
    assertThat(cf, stageCompletedWithExceptionThat(is(sameInstance(ex))));
    assertThat(cf, stageCompletedWithExceptionThat(isA(RuntimeException.class)));
    assertThat(cf, stageWillCompleteWithException());
    assertThat(cf, stageWillCompleteWithExceptionThat(is(sameInstance(ex))));
    assertThat(cf, stageWillCompleteWithExceptionThat(isA(RuntimeException.class)));
  }

  @Test
  public void success() {
    final CompletableFuture<String> cf = CompletableFuture.completedFuture("hi");

    assertThat(cf, not(stageCompletedWithException()));
    assertThat(cf, not(stageCompletedWithExceptionThat(isA(Throwable.class))));
    assertThat(cf, stageCompletedWithValue());
    assertThat(cf, stageCompletedWithValueThat(not(nullValue())));
    assertThat(cf, stageCompletedWithValueThat(notNullValue()));
    assertThat(cf, stageCompletedWithValueThat(equalTo("hi")));

    assertThat(cf, not(stageWillCompleteWithException()));
    assertThat(cf, not(stageWillCompleteWithExceptionThat(isA(Throwable.class))));
    assertThat(cf, stageWillCompleteWithValue());
    assertThat(cf, stageWillCompleteWithValueThat(not(nullValue())));
    assertThat(cf, stageWillCompleteWithValueThat(notNullValue()));
    assertThat(cf, stageWillCompleteWithValueThat(equalTo("hi")));
  }

  @Test
  public void completedWithValueWhenExceptional() {
    final RuntimeException ex = new RuntimeException("oops");

    final CompletableFuture<String> cf = new CompletableFuture<>();
    cf.completeExceptionally(ex);

    // ensure that the completedWithValue matcher correctly returns false from the matches() method
    // - this will fail if an exception is thrown instead
    assertThat(cf, not(stageCompletedWithValue()));
    assertThat(cf, not(stageWillCompleteWithValue()));
  }
}
