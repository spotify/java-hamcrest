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

import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageWillCompleteWithValue;
import static com.spotify.hamcrest.future.CompletableFutureMatchers.stageWillCompleteWithValueThat;
import static com.spotify.hamcrest.future.TestUtils.waitUntilInterrupted;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

public class SuccessfullyCompletedBlockingCompletionStageTest {

  private static final Matcher<CompletionStage<? extends Integer>> SUT =
      stageWillCompleteWithValueThat(is(1));

  @Test
  public void testDescriptionFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeTo(description);

    assertThat(description.toString(),
                      is("a stage that completed with a value that is <1>"));
  }

  @Test
  public void testMismatchFormatting() throws Exception {
    final StringDescription description = new StringDescription();
    SUT.describeMismatch(completedFuture(2), description);

    assertThat(description.toString(),
                      is("a stage that completed with a value that was <2>"));
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

  @Test
  public void testFailedMismatchFormatting() throws Exception {
    final CompletableFuture<Integer> future = new CompletableFuture<>();
    // Make the future complete exceptionally with an exception that has a cause
    future.completeExceptionally(new IOException("error", new RuntimeException("cause")));

    final StringDescription description = new StringDescription();
    SUT.describeMismatch(future, description);

    assertThat(description.toString(), startsWith(
        "a stage that completed exceptionally with java.io.IOException: error"));
  }

  @Test
  public void testInterruptActuallyInterruptsAssert() throws Exception {
    final CountDownLatch beforeAssertLatch = new CountDownLatch(1);
    final CountDownLatch afterAssertLatch = new CountDownLatch(1);
    final Thread threadToInterrupt = new Thread(() -> {
      try {
        beforeAssertLatch.countDown();
        assertThat(new CompletableFuture<>(),
            SUT);
      } finally {
        afterAssertLatch.countDown();
      }
    });
    try {
      threadToInterrupt.setDaemon(true);
      threadToInterrupt.start();

      beforeAssertLatch.await();
      TimeUnit.SECONDS.sleep(1);
      threadToInterrupt.interrupt();

      assertTrue("Failed to interrupt assertion",
          afterAssertLatch.await(2, TimeUnit.SECONDS));
    } finally {
      // Hamcrest goes into blocking method twice,
      // second interrupt should release it and allow thread to finish:
      threadToInterrupt.interrupt();
    }
  }

}
