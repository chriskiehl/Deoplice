package deoplice;

import deoplice.annotation.Updatable;
import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * Deoplice will automatically generate delegation methods for
 * common Java and Vavr data structures.
 */
@Value
@With
@Builder
@Updatable
public class DelegatedCollections {
    java.util.List<String> javaList;
    java.util.LinkedList<String> javaLinkedList;
    java.util.ArrayList<String> javaArrayList;
    java.util.HashSet<String> javaHashSet;
    java.util.Set<String> javaSet;

    io.vavr.collection.List<String> vavrList;
    io.vavr.collection.Array<String> vavrArray;
    io.vavr.collection.HashSet<String> vavrHashSet;
}
