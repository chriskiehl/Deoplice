<p align="center">
    <img src="https://github.com/chriskiehl/Deoplice/raw/master/images/deoplice-logo.JPG"/>
</p>

>to a great depth

## Overview

Deoplice is a library for modifying deeply nested immutable POJOs. It's symbiotic with Lombok and picks up where `With` and `@Builder` leave off: it gives you a high level, auto-generated modification API that cuts through the boilerplate and let's you express changes exctly as intended. 

## Table of Contents:

* About
* How does it work
* Lower level API
* Understanding code generation
* Customizing the generated API


>Note! Deoplice is currently in alpha and may have some bugs lurking around. Annotation processing is unexpectedly tricky and under-documented...

## How does it work?

Like Lombok, Deoplice works off annotations. To generate an API for your classes, all you've got to do is add the `@Updatable` annotation to any class that's already annotated with Lombok's `@With`.  

```java
import deoplice.annotation.Updatable;

@With
@Value
@Updatable   // ← New Annotation! All it takes!  :) 
class PurchaseOrder {
    String number; 
    Approval approval; 
}
@With
@Value
class Approval {
    ApprovalStatus status;
    Array<Comment> comments; 
    Confirmation customer;
}
@With
@Value
class Confirmation {
    UserAlias alias; 
    LocalDateTime updatedOn; 
}
```

And that's it! At build time, Deoplice will generate a tailor-made API that lets you make declarative modifications with zero boilerplate. It looks like this:  

```java
import static deoplice.generated.example.PurchaseOrderAPI.*;

updatedOrder = setConfirmationUpdatedOn(LocalDateTime.now())
    .andThen(setApprovalStatus("COMPLETED"))
    .andThen(updateApprovalComments(comments -> comments.append(someFinalComment))
    .apply(order)

updatedOrder = setApprovalCustomerUpdatedOn(LocalDateTime.now())
        .andThen(setApprovalStatus(COMPLETED))
        .andThen(setApprovalComments(Array.empty()))
        .apply()
```

So much nicer than vanilla Withers! 

**Deoplice interops with all your existing code** 

What's best, is that the setters generated by Deoplice are just plain ol' Java `Function`s. Meaning, you can use them anywhere and mix/match them with all your existing code.

For instance, got a collection of `PurchaseOrder`s and need to modify those `Confirmation` dates that are three levels deep? Deoplice's got you:

```java
completedOrders = pendingOrders.stream().map(setApprovalCustomerUpdatedOn(LocalDateTime.now())).toList(); 
```

How much is that saving us? Well here's what the same update would look like using the API provided by the with vanilla `@With`: 

```java
completedOrders = pendingOrders.stream().map(order -> 
        order.withApproval(order.getApproval().withConfirmation(
                order.getApproval().getConfirmation().withUpdatedOn(LocalDateTime.now())
        )) 
    ).toList(); 
```

Gross! What we're trying to actually do is completely drowned in the boilerplate it takes to do it.  


## How to find and use the generated files

Deoplice's generated files are all namespaced with your package name prefixed with `deoplice.generated` e.g. 

```
deoplice.generated.{package.of.your.annotated.model}
``` 

So, if your class looks something like this 

```java
package foo.bar;

class Foo {
    String bar; 
    // ... 
}
```

You'll find the generated files in your default build directory. e.g.  

```
builddir/ 
   |-- deoplice.generated.foo.bar.
       |-- FooAPI.java
       |-- FooLens.java 
```

You'd import them like anything else:

```java
import deoplice.generated.foo.bar.FooAPI;
```

**The easier way:**

Alternatively, you can ignore all that and just let your IDE figure out (it's very good at this!) 

![Using auto-complete](https://github.com/chriskiehl/Deoplice/raw/master/images/autocomplete.gif)



## Code Generation



## Customizing the API




## Lower level lens API

Is the high level API not doing what you need? There's a lower level API you can use! 

Behind the scenes, Deoplice generates a set of Lenses[0] for all your class' fields. What exactly is a Lens? It's some gobbledygook from functional programming. They're basically getters and setters that compose together really well. They're what gives deoplice the ability to cleanly perform updates on deeply nested objects. 

Given a simple class that we've annotated with `@Updatable`

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
@Updatable
class Driver {
    String name; 
    Interger age; 
}
```

For each class and field, it'll generate a set of lenses. They look like this: 

```java
public static Lens<Car, String> $make = makeLens(Car::getMake, Car::withMake);
public static Lens<Car, String> $model = makeLens(Car::getModel, Car::withModel);

...

public static Lens<Car, String> $model = makeLens(Driver::getName, Driver::withName);
public static Lens<Car, String> $model = makeLens(Driver::getAge, Driver::withAge);
```

>By default, lenses are all prefixed with `$` so they can be statically imported without collision. See [this section] for how to customize or remove this. 

You can import these and use them directly to perform updates. 

```
```



```set```

```update```

```java
updatedOrder=set($approval,$status,"COMPLETED")
        .andThen(set($approval,$confirmation,$updatedOn,LocalDateTime.now()))
        .andThen(update($approval,$comments,xs->xs.append(someFinalComment)))
        .apply(order)
```

And just like the DSL itself, these are just plain functions. So they play well with all existing java interfaces. 

```java 
completedOrders = pendingOrders.stream().map(set($approval, $status, "COMPLETED")).toList(); 
```


### Footnotes 

* [0] You may scoff at my hyper-simplified / dumbed down take on Lenses. That's OK. Deoplice isn't trying to be a lens library. It only borrows what's useful for its goals -- which is simply making the "common case" update paths less clunky to do in java.   