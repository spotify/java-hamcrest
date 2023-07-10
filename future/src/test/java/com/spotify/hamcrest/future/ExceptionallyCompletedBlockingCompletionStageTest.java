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

import static com.spotify.hamcrest.future.TestUtils.waitUntilInterrupted;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class ExceptionallyCompletedBlockingCompletionStageTest {

  private static final Matcher<CompletionStage<?>> SUT =
      CompletableFutureMatchers.stageWillCompleteWithExceptionThat(isA(RuntimeException.class));

  @Test
  public void testDescriptionFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeTo(description);

    assertThat(
        description.toString(),
        is(
            "a stage completing with an exception "
                + "that is an instance of java.lang.RuntimeException"));
  }

  @Test
  public void testMismatchFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeMismatch(completedFuture(2), description);

    assertThat(description.toString(), is("a stage that completed with a value that was <2>"));
  }

  @Test
  public void testCancelledMismatchFormatting() throws Exception {
    final CompletableFuture<Void> future = runAsync(waitUntilInterrupted());
    future.cancel(true);
    final StringDescription description = new StringDescription();
    SUT.describeMismatch(future, description);

    assertThat(description.toString(), is("a stage that was cancelled"));
  }

  @Test
  public void testInterruptedMismatchFormatting() throws Exception {
    final CompletableFuture<Void> future = runAsync(waitUntilInterrupted());

    try {
      // Interrupt this current thread so that future.get() will throw InterruptedException
      Thread.currentThread().interrupt();
      final StringDescription description = new StringDescription();
      SUT.describeMismatch(future, description);

      assertThat(description.toString(), is("a stage that was interrupted"));
    } finally {
      // Clear the interrupted flag to avoid interference between tests
      Thread.interrupted();
    }
  }
}
