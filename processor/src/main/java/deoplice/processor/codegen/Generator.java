package deoplice.processor.codegen;

import deoplice.annotation.Updatable;
import deoplice.processor.types.AST;
import deoplice.processor.types.Extractions.ExtractedField;
import deoplice.processor.types.LensBundle;
import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import lombok.Value;
import lombok.val;

import static deoplice.processor.codegen.GrabBag.*;
import static java.lang.String.format;
import static java.util.function.Function.identity;

/**
 * Generates Lenses and custom API Methods for all the annotated class' fields.
 */
@Value
public class Generator {
    Updatable config;
    Array<MethodGenerator> methodGenerationStrats;

    public Array<AST.ClassDef> lensClasses2(Array<ExtractedField> fields) {
        return lensClasses(this.config, fields);
    }

    public AST.ClassDef apiClass2(Array<ExtractedField> fields) {
        return apiClass(this.config, this.methodGenerationStrats, fields);
    }

    public static Array<AST.ClassDef> lensClasses(Updatable conf, Array<ExtractedField> fields) {
        Array<LensBundle> asdf = fields.map(x -> createLensAssignment(conf, x))
                .groupBy(AST.LensAssignment::getDeclaringClass)
                .mapValues(HashSet::ofAll)
                .toArray()
                .map(x -> new LensBundle(x._1, x._2.toArray().map(identity())));

        return ClassReconciler.reconcileClassHierarchy(asdf);

//        return fields.map(x -> createLensAssignment(conf, x))
//                .groupBy(AST.LensAssignment::getDeclaringClass)
//                // The set here is to remove any duplicates caused by multiple
//                // references to the same class at different points throughout
//                // a class' hierarchy
//                .mapValues(HashSet::ofAll)
//                .toArray()
//                .map(tuple -> {
//                    val owner = tuple._1;
//                    val lenses = tuple._2;
//                    return AST.ClassDef.builder()
//                            .pkg(GrabBag.packageish(owner))
//                            .name(unqualify(owner))
//                            .imports(Array.of(
//                                "import deoplice.lenses.Lens;",
//                                "import java.util.function.Function;",
//                                "import static deoplice.lenses.API.makeLens;"))
//                            .body(lenses.toArray().map(identity())) // helping the type system..
//                            .build();
//                });
    }


    public static Array<AST.ClassDef> reconcile(Array<AST.ClassDef> classes) {
        return classes;
    }



    public static AST.LensAssignment createLensAssignment(Updatable conf, ExtractedField field) {
        val name = conf.lensVariablePrefix() + unqualifiedName(field.getElement());
        return AST.LensAssignment.builder()
                .declaringClass(AST.Qualified.of(lensifyDeclaringClass(field.getElement(), conf.lensClassSuffix())))
                .name(name)
                .assignment("public static Lens<CLASS_TYPE, FIELD_TYPE> NAME = makeLens(GETTER, SETTER);"
                        .replace("CLASS_TYPE", declaringType(field.getElement()))
                        .replace("FIELD_TYPE", GrabBag.typeOf(field.getElement()))
                        .replace("NAME", name)
                        .replace("GETTER", field.getGetter())
                        .replace("SETTER", field.getSetter()))
                .build();
    }

    public static AST.LensRef createLensReference(Updatable conf, ExtractedField field) {
        List<AST.Qualified> asfsdf = field.getLineage().map(x -> qualifiedName(createLensAssignment(conf, x)))
                .append(qualifiedName(createLensAssignment(conf, field)));
        return AST.LensRef.of(asfsdf.toArray());
    }

    public static String compo(AST.LensRef lensRefs) {
        return lensRefs.getLenses().tail().foldLeft(lensRefs.getLenses().head().getValue(),
                (stmt, lens) -> format("%s.compose(%s)", stmt, lens.getValue()));
    }

    public static String comm(Array<AST.Parameter> params) {
        return params.map(x -> x.getType().getValue() + " " + x.getName()).mkString(", ");
    }

    public static AST.ClassDef apiClass(Updatable conf, Array<MethodGenerator> providers, Array<ExtractedField> fields) {
        AST.Qualified rootClass = rootClass(fields.head());
        String rootType = generifiedType(rootElement(fields.head()));
        java.util.List<AST> methods = new java.util.ArrayList<>();
        for (ExtractedField field : fields) {
            for (MethodGenerator provider : providers) {
                for (AST.ApiMethodDef thing : provider.generateMethods(field.getElement())) {

                    methods.add(AST.MethodDef.builder()
                            .name(thing.getAction() + nestedVariableName("", field))
                            .declaringClass(rootClass)
                            .body(("public static java.util.function.Function<ROOT_TYPE, ROOT_TYPE> ACTIONMETHOD_NAME(PARAMETERS) {\n" +
                                    "\tLens<ROOT_TYPE, FIELD_TYPE> lens = LENS;\n" +
                                    "\treturn (ROOT_TYPE obj) -> deoplice.lenses.API.update(lens, EXPR).apply(obj);\n" +
                                    "}\n")
                                    .replace("ROOT_TYPE", rootType)
                                    .replace("LENS", compo(createLensReference(conf, field)))
                                    .replace("FIELD_TYPE", typeOf(field.getElement()))
                                    .replace("METHOD_NAME", nestedVariableName("", field))
                                    .replace("ACTION", thing.getAction())
                                    .replace("EXPR", thing.getExpression())
                                    .replace("PARAMETERS", comm(thing.getParams())))
                            .build());
                }
            }
        }

        return AST.ClassDef.builder()
                .pkg(GrabBag.pkg(rootClass))
                .name(unqualify(rootClass) + conf.apiClassSuffix())
                .imports(Array.of("import deoplice.lenses.Lens;"))
                .body(Array.ofAll(methods))
                .build();
    }

}
