<p align="center">
    <img src="https://github.com/chriskiehl/Deoplice/raw/master/images/deoplice-logo.JPG"/>
</p>

>to a great depth

## Overview

Deoplice is java library that automatically generates an API for transforming your immutable POJOs. It's *symbiotic* with [Lombok](https://projectlombok.org/) and picks up where [`@Value`](https://www.projectlombok.org/features/Value) and [`@With`](https://projectlombok.org/features/With) leave off. Say goodbye to tedious boilerplate when updating complex nested objects! With Deoplice, complicated, multi-level updates become trivial _one liners_. 

>Note! Deoplice is currently in alpha and may have some bugs lurking around. The author found annotation processing unexpectedly tricky, under-documented, and full of sharp edges. If you encounter any problems, pop over to the issues and let me know.  

## How does it work?

Just like Lombok, Deoplice works off annotations. To generate an API for your classes, all you've got to do is add the `@Updatable` annotation to any class that's also annotated with Lombok's `@With`. 

Here's a tiny set of data classes we'll use throughout the docs:

```java
import deoplice.annotation.Updatable;

@With
@Value
@Updatable   // ← Check it out! All we have to do! ^_^ 
class PurchaseOrder {
    String number; 
    Approval approval; 
    Integer version; 
}
@With
@Value
class Approval {
    ApprovalStatus status;
    Array<Comment> comments; 
    Confirmation confirmation;
}
@With
@Value
class Confirmation {
    UserAlias alias; 
    LocalDateTime updatedOn; 
}
```

With the annotation in place, at build time, Deoplice will parse your class and recursively generate a full suite of immutable setters and update functions for every field it finds. It means you express complex data transformations like this: 

```java
PurchaseOrder updatedOrder = setConfirmationUpdatedOn(LocalDateTime.now()) 
    .andThen(setApprovalStatus(COMPLETED)) 
    .andThen(updateVersion(x -> x + 1))
    .apply(order)
```

On top of that, Deoplice will generate a rich set of method delegations for popular collection types. This means you'll never again have to be crushed by the boilerplate of pulling a collection out of an object, copying it, transforming it, and then `with`ing it (often down several levels) back into place. You can compose together extremely complex actions with ease using Deoplice's generated APIs. 

```java
removeApprovalComments(unwantedComment)
	.andThen(addApprovalComments(Comment.of("lgtm!)))
	.andThen(mapApprvalComments(String::toUpperCase))
	.apply(purchaseOrder); 
```


## Deoplice interops with all your existing code

All of the API methods generated by Deoplice are plain ol' Java Functions. This means you can use them anywhere `Function` is used and freely mix/match them with all your existing code.

For instance, got a collection of `PurchaseOrder`s and need to modify the `Confirmation` date that's three levels deep? That's a one-liner! 

```java
completedOrders = pendingOrders.map(setApprovalConfirmationUpdatedOn(LocalDateTime.now())); 
```

Here's what the same update would look like without Deoplice using the standard `with` API provided by Lombok:

```java
completedOrders = pendingOrders.map(order -> 
        order.withApproval(order.getApproval().withConfirmation(
                order.getApproval().getConfirmation().withUpdatedOn(LocalDateTime.now())
        )) 
    )
```

Gross! What we're trying to actually do is completely drowned in the boilerplate it takes to do it.

Deoplice shines at cutting through that boilerplate and letting the _what_ you're trying to accomplish rise to the forefront and shine.  

## Setters and Updaters

Deoplice generates two primary methods for every field in your class: a **setter** (`set{field}`) and an **updater** (`update{Field}`).

**Setters** are for when you need to _replace_ a value. e.g. 

```java
setApprovalConfirmationUpdatedOn(LocalDateTime.now()); 
```

**Updaters** are for when you need to compute a value based on what's currently there. e.g. 

```java
updateApprovalVersion(version -> version += 1); 
```


## A rich set of collection delegations

Deoplice has first class support for the standard collection types in `java.util.collection` as well as `io.vavr.collection`! 

This means that you get an immutable API with Java's mutable collections for free! Usually, trying to treat java's collections as immutable leads to code that is punishingly verbose. 

Adding an item generally involves pulling the list out of the POJO, making a copy, doing the transform, and then `with`ing it back into place. 

```java
List<Comment> original = order.getApproval().getComments()
List<Comment> updated = Stream.concat(original, Stream.of(Comment.of("lgtm!")).collect(Collectors.toList());
return order.withApproval(order.getApproval().withComments(updated)); 
```

Deoplice does all of this copy-on-write for you behind the scenes! You get to enjoy the quality of life that comes from immutable data structures all while using the standard mutable java collection ones! It turns all of the hullabalo above into this one-liner: 

```
addApprovalComment(Comment.of("lgtm!")).apply(order); 
```

Pretty sweet! 

**It also works for [Vavr's](https://github.com/vavr-io) collection types!** If you know then you know: [Vavr](https://github.com/vavr-io) is the best. 

```
class MyStuff {
    Array<String> things; 
}
```

```
appendThings("myNewItem").apply(mystuff); 
```


## How to find and use the generated files

Deoplice generates two sets of files. The first is the top-level API. It will have the exact same name as your annotated class, but have a suffix of `API` added. 

So, if your class looks something like this

```java
package foo.bar;

class Foo {
    // ... 
}
```

You'll find a generated file in your default build directory called `FooAPI.java` under the same package. 

```
build/ 
   |-- foo.bar
       |-- FooAPI.java
```

You'd import them like anything else:

```java
import foo.bar.FooAPI;
```

and you're off to the races! 

**But the easier way is too...**

Ignore all the specifics of build systems, annotation processing directories, and what goes where and just let your IDE figure it all out (it's very good at this!). Just type either `set`, `update`, and a collection method like `add`/`append` and your IDE will pull up all the options

![Using auto-complete](https://github.com/chriskiehl/Deoplice/raw/master/images/autocomplete.gif)



## Lower level lens API

Behind the scenes, Deoplice's compositional API magic is achieved by generating a set of [Lenses](https://en.wikibooks.org/wiki/Haskell/Lenses_and_functional_references) for every field in each of your annotated classes. What exactly is a Lens? It's some gobbledygook from functional programming. They're basically getters and setters that compose together really well. They're what gives Deoplice the ability to cleanly perform updates on deeply nested objects.

You'll find these lens classes in the build directory along side the generated API files. 

```java
@With
@Value
@Updatable
class Car {
    String make;
    Driver driver;
}
@With
@Value
class Driver {
    String name;
    Interger age;
}
```

Each class is suffixed with `Lens` filled with field accessors like this: 

```java
public static Lens<Car, String> $make = makeLens(Car::getMake, Car::withMake);
public static Lens<Car, String> $model = makeLens(Car::getModel, Car::withModel);

        ...

public static Lens<Car, String> $model = makeLens(Driver::getName, Driver::withName);
public static Lens<Car, String> $model = makeLens(Driver::getAge, Driver::withAge);
```

These form the primitives that the higher level API is built from. 

```java
updatedOrder=set($approval,$status,"COMPLETED")
        .andThen(set($approval,$confirmation,$updatedOn,LocalDateTime.now()))
        .andThen(update($approval,$comments,xs->xs.append(someFinalComment)))
        .apply(order)
```

And just like the API itself, these are just plain functions. So they play well with all existing java interfaces.

```java 
completedOrders = pendingOrders.stream().map(set($approval, $status, "COMPLETED")).toList(); 
```

These are exposed so that you can build your own higher level APIs. For instance, if you use a different collection library that the ones natively supported by Deoplice, you add support for it as a layer ontop of the lenses with very little code. 


## Current quirks & limitations

**Generics**

```java
@Value 
public class MyFoo<A> {/*...*/} 
```

Deoplice doesn't currently have the smartest support for classes with generics. The alpha release uses wildcards in the generated API for simplicity. This means that calls where the root object is generic will need casting in order to help the compiler understand the target type.

This is on the roadmap to fix because it's annoying. 

**Wildcards** 

```
class Bar {
	List<?> items; // This will be ignroed
}
```

Wildcarded type params are ignored. Unless a type is specified, there's no way for Deoplice to create delegated methods like `add`. (because `void add(? input) {...}` is invalid). 


