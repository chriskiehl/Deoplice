# Random Notes on traversing the AST and extracting fully qualified type info.  

Before realizing `.toString()` was (fairly) reliable/stable across jvm implementations, I was slowly figuring out how to extract qualified type info by hand. 

Notes are for future me in case I have to go back to the parsing route. 

#### GETTING ROOT FIELDS

```
((TypeElement)annotatedHandlers.iterator().next()).getEnclosedElements().stream()
.filter(x -> x.getKind() == ElementKind.FIELD)
.toList()
```

#### RECURSING INTO SUB-OBJECTS

```
processingEnv.getTypeUtils().asElement(((TypeElement) annotatedHandlers.iterator().next())
    .getEnclosedElements()
    .get(2)
    .asType()).getEnclosedElements()
```


### GETTING TYPE SIGNATURE (IS NOT EASY) (PARTIAL SOLUTION)


```
((TypeElement)((DeclaredType) ee.asType()).asElement()).getQualifiedName()
```

What's happening here:

>Element(FIELD) -> Type -> Element(CLASS) -> ElementType -> getQualifiedName()


When you're iterating the fields off the class, you find them as Elements of kind FIELD.
Fields cannot be made into TypeElement directly, because this element is a FIELD. To get its
type signature (at least for non-parameterized types), you've got to from FIELD to TYPE, but
TypeMirrors can capture things like `NoType` or `NullType` which don't have class info, and so,
after verifying that the Type is DECLARED (e.g. a Class or Interface), we can finally go back to
Element, cast it to TypeElement, and get the qualified path!

Holy Crackers!


### GOING TYPE -> ELEMENT LOSES GENERIC TYPE INFORMATION

This:

```
((DeclaredType) ((DeclaredType)element.asType()).getTypeArguments().get(1)).getTypeArguments()
```
will give (for a type of `List<LocalDateTime>`)

```
[LocalDateTime]
```

but THIS:

```
((DeclaredType) ((DeclaredType) ((DeclaredType)element.asType()).getTypeArguments().get(1)).asElement().asType()).getTypeArguments()
```

Will give a List of `E` (The error type) because all the type info gets droppd. WHHHAAAAAAAAT.

