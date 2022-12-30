package deoplice.processor.codegen;

import deoplice.processor.types.AST;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

/**
 * Renders the AST objects into their final stringly source form.
 */
public class Renderer {

    /**
     * Dispatches to the appropriate renderer.
     */
    public static String render(AST node) {
        return io.vavr.API.Match(node).of(
                Case($(instanceOf(AST.ClassDef.class)), Renderer::render),
                Case($(instanceOf(AST.InnerClassDef.class)), Renderer::render),
                Case($(instanceOf(AST.MethodDef.class)), Renderer::render),
                Case($(instanceOf(AST.LensAssignment.class)), Renderer::render)
        );
    }

    public static String render(AST.ClassDef clsdef) {
        return ("package PACKAGE;\n\n" +
                        "IMPORTS\n\n" +
                        "public class CLASS_NAME {\n\n" +
                        "BODY\n\n" +
                        "}")
                .replace("PACKAGE", clsdef.getPkg())
                .replace("IMPORTS", clsdef.getImports().mkString("\n"))
                .replace("CLASS_NAME", clsdef.getName())
                .replace("BODY", clsdef.getBody().map(Renderer::render).mkString("\n\n"));
    }

    public static String render(AST.InnerClassDef clsdef) {
        return ("public static class CLASS_NAME {\n\n" +
                "BODY\n\n" +
                "}")
                .replace("CLASS_NAME", clsdef.getName())
                .replace("BODY", clsdef.getBody().map(Renderer::render).mkString("\n\n"));
    }

    public static String render(AST.MethodDef node) {
        return node.getBody();
    }

    public static String render(AST.LensAssignment node) {
        return node.getAssignment();
    }

}
