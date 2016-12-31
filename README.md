# Spotify Hamcrest

[![Build Status](https://travis-ci.org/spotify/java-hamcrest.svg?branch=master)](https://travis-ci.org/spotify/java-hamcrest)
[![codecov](https://codecov.io/gh/spotify/java-hamcrest/branch/master/graph/badge.svg)](https://codecov.io/gh/spotify/java-hamcrest)
[![Maven Central](https://img.shields.io/maven-central/v/com.spotify/hamcrest.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.spotify%22%20hamcrest*)
[![License](https://img.shields.io/github/license/spotify/java-hamcrest.svg)](LICENSE)

This is a collection of libraries extending the Hamcrest matching
library with useful matchers. We consider this library in beta but use it
in many of our internal projects.

* [Download](#download)
* [Getting started](#getting-started)
  * [POJO matchers](#pojo-matchers)
  * [JSON matchers](#json-matchers)
  * [java.util.Optional matchers](#javautiloptional-matchers)
  * [Future matchers](#future-matchers)
    * [raw `Future` matchers](#raw-future-matchers)
    * [Java 8's `CompletableFuture` matchers](#java-8s-completablefuture-matchers)
* [Prerequisites](#prerequisites)
* [Code of conduct](#code-of-conduct)

## Download

Download the latest JAR or grab [via Maven][maven-search].

```xml
<dependency>
  <groupId>com.spotify</groupId>
  <artifactId>hamcrest</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Getting Started

### POJO matchers [![Javadocs](http://www.javadoc.io/badge/com.spotify/hamcrest-pojo.svg?color=blue)](http://www.javadoc.io/doc/com.spotify/hamcrest-pojo)

Many applications at Spotify are very data heavy.  They might be
aggregation services that combine a lot of data structures into even
more complicated data structures.  And the basic data structures are
usually complicated to begin with.

The POJO matcher library lets you describe the structure of a POJO in
a fluent style and then match against that structure.  It's optimized
for very complicated objects with a lot of properties.  When a
mismatch occurs, the library tries to minimally describe the mismatch.

Here is an example from an actual test case in one of our projects:

```java
final List<User> users;
try (Stream<User> userStream = sut.fetchAllUsers()) {
  users = userStream.collect(Collectors.toList());
}

assertThat(users, contains(
    pojo(User.class)
        .where("address", is(
            pojo(Address.class)
                .withProperty("street", is("Main Street"))
                .withProperty("country", is("US"))
        ))
        .where("product", is(
            pojo(Product.class)
                .withProperty("id", is(1))
                .withProperty("name", is("premium"))
                .withProperty("metadata", is("{\"foo\": [\"bar\", \"baz\"]}"))
                .withProperty("creationDate", is(Timestamp.from(Instant.EPOCH)))
                .withProperty("isTest", is(false))
        ))
));
```

An actual failure of this test case happened a while ago, and the
output looks like this:

```
Expected:
iterable containing [User {
  address(): is Address {
    getStreet(): is "Main Street"
    getCountry(): is "US"
  }
  product(): is Product {
    getId(): is <1>
    getName(): is "premium"
    getMetadata(): is "{\"foo\": [\"bar\", \"baz\"]}"
    getCreationDate(): is <1970-01-01 01:00:00.0>
    getIsTest(): is <false>
  }
}]

but:
item 0: User {
  ...
  product(): Product {
    ...
    getMetadata(): was "{\"foo\": \"bar\"}"
    ...
  }
  ...
}
```

### JSON matchers [![Javadocs](http://www.javadoc.io/badge/com.spotify/hamcrest-jackson.svg?color=blue)](http://www.javadoc.io/doc/com.spotify/hamcrest-jackson)

To include into your Maven build, add this dependency:

```xml
  <dependency>
      <groupId>com.spotify</groupId>
      <artifactId>hamcrest-jackson</artifactId>
      <version>THEVERSIONYOUWANT</version>
      <scope>test</scope>
  </dependency>
```

Similar to the POJO matchers, the JSON matchers let you describe a
JSON structure and match against it.

```java
assertThat(json, is(
     jsonObject()
         .where("foo", is(jsonInt(1)))
         .where("bar", is(jsonBoolean(true)))
         .where("baz", is(
             jsonObject()
                 .where("foo", is(jsonNull()))))));
```

A failing test would look like:

```
Expected:
{
  "foo": is a number node is <1>
  "bar": is a boolean node is <false>
  "baz": is {
    "foo": is a null node
  }
}

but:
{
  ...
  "baz": {
    ...
    "foo": was not a null node, but a boolean node
    ...
  }
  ...
}
```

### java.util.Optional matchers [![Javadocs](http://www.javadoc.io/badge/com.spotify/hamcrest-optional.svg?color=blue)](http://www.javadoc.io/doc/com.spotify/hamcrest-optional)

`com.spotify:hamcrest-optional` provides matchers for the Java 8
Optional type so you don't have to unpack the Optional in your tests.

```java
final Optional<String> response = methodUnderTest();
assertThat(response, hasValue(equalTo("foo"));

final Optional<Collection<Foo>> col = anotherMethod();
assertThat(response, hasValue(containsInAnyOrder(...)));

// or if you only care that the Optional is non-empty:
assertThat(response, hasValue());

// or if you expect an empty Optional:
assertThat(response, isEmpty());
```

### Future matchers [![Javadocs](http://www.javadoc.io/badge/com.spotify/hamcrest-future.svg?color=blue)](http://www.javadoc.io/doc/com.spotify/hamcrest-future)

Similar to the Optional matchers, the CompletionStage /
CompletableFuture matchers in `com.spotify:hamcrest-future` allow
you to assert against the value or completion state of a
CompletionStage without having to unpack it in your test code or
handle the checked exceptions of `Future.get()` (or using
`Futures.getUnchecked(future)`).

There are four dimensions you can choose from when using Future
matchers:

  * raw `Future` vs Java 8's `CompletableFuture`
  * blocking vs non-blocking
      * blocking: the matcher will wait, perhaps indefinitely, for the
        future to complete
      * non-blocking: the matcher will not match if the future is not
        yet completed
  * completed successfully vs completed with an exception
  * match anything vs pass in another matcher

So there are a total of 16 methods you can call:

#### raw `Future` matchers

Use `com.spotify.hamcrest.future.FutureMatchers`:

|             | blocking                                | non-blocking |
| ----------- | --------------------------------------- | ------------ |
| successful  | futureWillCompleteWithValue\[That]()     | futureCompletedWithValue\[That]() |
| exceptional | futureWillCompleteWithException\[That]() | futureCompletedWithException\[That]() |

#### Java 8's `CompletableFuture` matchers

Use `com.spotify.hamcrest.future.CompletableFutureMatchers`:

|             | blocking                               | non-blocking |
| ----------- | -------------------------------------- | ------------ |
| successful  | stageWillCompleteWithValue\[That]()     | stageCompletedWithValue\[That]() |
| exceptional | stageWillCompleteWithException\[That]() | stageCompletedWithException\[That]() |

Note that to test that a CompletionStage completed with a certain
value or exception use `..That(..)`.

```java
CompletionStage<List<Foo>> f = someMethod();
assertThat(f, stageCompletedWithValueThat(contains(...));

CompletionStage<Foo> c = methodThatShouldFail();
assertThat(c, stageCompletedWithExceptionThat(isA(FooException.class)));
```

If you want the matcher to block until the CompletionStage is
completed, use `stageWillCompleteWithValueThat(..)`:

```java
// warning: might block forever if the stage never completes!
CompletionStage<List<Foo>> f = someMethod();
assertThat(f, stageWillCompleteWithValueThat(is(equalTo(...)));
```

Be careful when using this matcher as it might block forever if the
stage never completes! Consider restructuring your tests so that the
completions returned from the method/class being tested are
immediately completed (e.g. using MoreExecutors.directExecutor, etc).


## Prerequisities

Any platform that has the following

* Java 8+
* Maven 3 (for compiling)


## Code of conduct

This project adheres to the [Open Code of Conduct][code-of-conduct]. By participating, you are
expected to honor this code.

  [code-of-conduct]: https://github.com/spotify/code-of-conduct/blob/master/code-of-conduct.md
  [maven-search]: https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.spotify%22%20hamcrest*
