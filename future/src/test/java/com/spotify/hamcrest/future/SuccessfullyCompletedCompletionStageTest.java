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

import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageCompletedWithValueThat;
import static com.spotify.hamcrest.future.TestUtils.waitUntilInterrupted;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class SuccessfullyCompletedCompletionStageTest {

  private static final Matcher<CompletionStage<? extends Integer>> SUT =
      stageCompletedWithValueThat(is(1));

  @Test
  public void testDescriptionFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeTo(description);

    assertThat(description.toString(), is("a stage that completed to a value that is <1>"));
  }

  @Test
  public void testMismatchFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeMismatch(completedFuture(2), description);

    assertThat(description.toString(), is("a stage that completed to a value that was <2>"));
  }

  @Test
  public void testInterruptedMismatchFormatting() throws Exception {
    final CompletableFuture<Void> future = runAsync(waitUntilInterrupted());

    try {
      // Interrupt this current thread so that future.get() will throw InterruptedException
      Thread.currentThread().interrupt();
      final StringDescription description = new StringDescription();
      SUT.describeMismatch(future, description);

      assertThat(description.toString(), is("a stage that was not done"));
    } finally {
      // Clear the interrupted flag to avoid interference between tests
      Thread.interrupted();
    }
  }

  @Test
  public void testCancelledMismatchFormatting() throws Exception {
    final CompletableFuture<Void> future = runAsync(waitUntilInterrupted());

    try {
      // Cancel the future
      future.cancel(true);
      final StringDescription description = new StringDescription();
      SUT.describeMismatch(future, description);

      assertThat(description.toString(), is("a stage that was cancelled"));
    } finally {
      // This will cause the future's thread to throw InterruptedException and make it return
      future.cancel(true);
    }
  }

  @Test
  public void testFailedMismatchFormatting() throws Exception {
    final CompletableFuture<Integer> future = new CompletableFuture<>();
    // Make the future complete exceptionally with an exception that has a cause
    future.completeExceptionally(new IOException("error", new RuntimeException("cause")));

    final StringDescription description = new StringDescription();
    SUT.describeMismatch(future, description);

    assertThat(
        description.toString(),
        startsWith("a stage that completed exceptionally with java.io.IOException: error"));
  }
}
