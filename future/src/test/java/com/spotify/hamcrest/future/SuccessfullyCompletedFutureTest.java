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

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.spotify.hamcrest.future.FutureMatchers.futureCompletedWithValueThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.io.IOException;
import java.util.concurrent.Future;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class SuccessfullyCompletedFutureTest {

  private static final Matcher<Future<? extends Integer>> SUT =
      futureCompletedWithValueThat(is(1));

  @Test
  public void testDescriptionFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeTo(description);

    assertThat(description.toString(),
               is("a future that completed to a value that is <1>"));
  }

  @Test
  public void testMismatchFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeMismatch(immediateFuture(2), description);

    assertThat(description.toString(),
               is("a future that completed to a value that was <2>"));
  }

  @Test
  public void testInterruptedMismatchFormatting() throws Exception {
    final SettableFuture<Void> future = SettableFuture.create();

    try {
      // Interrupt this current thread so that future.get() will throw InterruptedException
      Thread.currentThread().interrupt();
      final StringDescription description = new StringDescription();
      SUT.describeMismatch(future, description);

      assertThat(description.toString(), is("a future that was not completed"));
    } finally {
      // This will cause the future's thread to throw InterruptedException and make it return
      future.cancel(true);
    }
  }

  @Test
  public void testCancelledMismatchFormatting() throws Exception {
    final SettableFuture<Void> future = SettableFuture.create();

    try {
      // Cancel the future
      future.cancel(true);
      final StringDescription description = new StringDescription();
      SUT.describeMismatch(future, description);

      assertThat(description.toString(), is("a future that was cancelled"));
    } finally {
      // This will cause the future's thread to throw InterruptedException and make it return
      future.cancel(true);
    }
  }

  @Test
  public void testFailedMismatchFormatting() throws Exception {
    // Make the future complete exceptionally with an exception that has a cause
    final ListenableFuture<Object> future =
        Futures.immediateFailedFuture(new IOException("error", new RuntimeException("cause")));

    final StringDescription description = new StringDescription();
    SUT.describeMismatch(future, description);

    assertThat(description.toString(),
        startsWith("a future that completed exceptionally with java.io.IOException: error"));
  }
}
