package deoplice.processor.codegen;

import deoplice.processor.types.AST;
import deoplice.processor.types.LensBundle;
import io.vavr.Predicates;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static deoplice.processor.codegen.GrabBag.unqualify;
import static io.vavr.API.*;

/**
 *
 * The most challenging part of this whole ordeal is honoring the user's
 * package and class structure. What makes it tricky is that fields can reference
 * classes from anywhere, including inside other classes (and inside those!).
 *
 * In the case of nested inner classes, for instance, `foo.bar.Foo.Bar`, we need the
 * entire class hierarchy to exist (`foo.bar.Foo`) in order to write our target class (`Bar`)
 * to the correct location (nested inside `Foo`). The problem here is that we only get
 * the leaf node class reference from the Element AST. So, we have to _create_ the
 * parent `Foo` source file and then inject `Bar` inside it.
 *
 * Further complicating things is that you can have numerous levels of classes, each with its
 * own parent, and each with _its_ own parent, and so on. All of these need merged into the
 * appropriate hierarchy.
 *
 * At the end of parsing we have a flat array like this:
 *
 *     [foo.bar.A
 *      foo.bar.A.B
 *      foo.bar.A.B.C
 *      foo.bar.Z
 *      other.pkg.X.Y]
 *
 * Which needs to rolled up into:
 *
 *       foo.bar.A    other.pkg.X
 *         / \            \
 *        B   Z            Y
 *       /
 *      C
 *
 * If you squint, this specific tree-ification is just a prefix matching problem. Thus, the
 * internal guts here are vaguely modeled off a Trie.
 *
 */
public class ClassReconciler {
    Node root;

    public static Array<AST.ClassDef> reconcileClassHierarchy(Array<LensBundle> lenses) {
        ClassReconciler reconciler = new ClassReconciler();
        lenses.forEach(reconciler::insert);
        // The point of reconciliation to reduce the various AST nodes down into a single set
        // of root ClassDefs. So more involved typing could probably avoid the manual cast here
        // but.. I'm not totally sure how to do it without making a mess.
        return reconciler.reconcileClassHierarchy(reconciler.root).map(x -> (AST.ClassDef) x);
    }

    /**
     * The fact that all these Trie shenanigans are going on is an unimportant
     * implementation detail. Thus the constructor and internal state is hidden
     * entirely from any consumers. They interact with it as though it were a pure
     * function.
     */
    private ClassReconciler() {
        this.root = new Node(new HashMap<>(), Option.none(), false);
    }

    /**
     * Inserts a LensBundle into the internal trie(ish) structure.
     *
     * Important Implementation Notes:
     * -------------------------------
     * The key to all of this is the bespoke tokenization method. This can take some (a lot) of thinking
     * as it breaks the usual Trie behavior / expectations.
     *
     * The standard Trie generally operates on some `Iterable A`. This has really nice properties that
     * make it easy to reason about both how the tree is build and how to construct "words" during traversal.
     * For instance, in the case of strings, and collections, you get easy monoidal properties.
     *
     * This trie does NOT have those properties. The node keys (strings) are different from the node
     * type (LensBundle), and those are both different from the traversal type (AST). Further, the tokenization
     * is not on the input data type (like how strings->char[]). It is bespoke and based off breaking a
     * fully qualified (potentially nested) class path into its individual pieces.
     *      e.g. ("foo.bar.Baz.Qup" -> ["foo.bar.Baz", "Qup"])
     *
     * Why? Because these changes, while a bit wonky, get us a whole bunch of complex merging logic for free.
     */
    public void insert(LensBundle lensBundle) {
        Node curr = root;
        val classNames= GrabBag.splitDeclaringClasses2(lensBundle.getDeclaringClass());
        for (String s : classNames) {
            if (!curr.children.containsKey(s)) {
                Node newNode = new Node(
                        new HashMap<>(),
                        s.equals(classNames.last()) ? Option.some(lensBundle) : Option.none(),
                        s.equals(classNames.head())
                );
                curr.children.put(s, newNode);
                curr = newNode;
            } else {
                curr = curr.children.get(s);
            }
        }
    }



    Array<AST> reconcileClassHierarchy(Node node) {
        return Array.ofAll(node.children.entrySet()).map((entry) -> {
            val className = entry.getKey();
            val thisNode = entry.getValue();
            AST classDef = this.chooseClassType(className, thisNode);
            Array<AST> children = reconcileClassHierarchy(thisNode);
            return Match(classDef).of(
                    Case($(Predicates.instanceOf(AST.ClassDef.class)), (AST.ClassDef clss) -> clss.withBody(clss.getBody().appendAll(children))),
                    Case($(Predicates.instanceOf(AST.InnerClassDef.class)), (AST.InnerClassDef clss) -> clss.withBody(clss.getBody().appendAll(children)))
            );
        });
    }

    /**
     * Creates the appropriate AST Class Def type.
     *
     * As we walk the trie, we have to create different data types. This is determined by:
     *   (a) whether we're at the root or an inner level, and
     *   (b) whether we have concrete data to put into the class' body.
     */
    AST chooseClassType(String className, Node node) {
        if (node.isRoot) {
            if (node.lensBundle.isDefined()) {
                LensBundle lensBundle = node.lensBundle.get();
                return AST.ClassDef.builder()
                        .pkg(GrabBag.pkg(lensBundle.getDeclaringClass()))
                        .name(unqualify(lensBundle.getDeclaringClass()))
                        .imports(GrabBag.standardImports)
                        .body(lensBundle.getAssignments())
                        .build();
            } else {
                return AST.ClassDef.builder()
                        .pkg(GrabBag.pkg(className))
                        .name(unqualify(AST.Qualified.of(className)))
                        .imports(GrabBag.standardImports)
                        .body(Array.empty())
                        .build();
            }
        } else {
            return AST.InnerClassDef.builder()
                    .name(className)
                    .body(node.lensBundle.map(LensBundle::getAssignments).getOrElse(Array.empty()))
                    .build();
        }
    }

    /**
     * Internal Node type for our Trie.
     */
    @AllArgsConstructor
    static class Node {
        Map<String, Node> children;
        Option<LensBundle> lensBundle;
        Boolean isRoot;
    }
}
