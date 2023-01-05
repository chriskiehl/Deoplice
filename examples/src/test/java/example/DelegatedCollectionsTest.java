package example;

import deoplice.DelegatedCollections;
import deoplice.DelegatedCollectionsAPI;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static deoplice.DelegatedCollectionsAPI.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DelegatedCollectionsTest {

    @Test
    public void exerciseCollectionDelegations() {
        // Phew. Ok. Here we go...
        // Making sure that every delegated API method
        // actually behaves as expected.
        DelegatedCollections colls = DelegatedCollections.builder()
                .javaList(new ArrayList<>())
                .javaArrayList(new ArrayList<>())
                .javaLinkedList(new LinkedList<>())
                .javaSet(new HashSet<>())
                .javaHashSet(new HashSet<>())
                .vavrList(List.empty())
                .vavrArray(Array.empty())
                .vavrHashSet(io.vavr.collection.HashSet.empty())
                .build();

        String singleItem = "zoo";
        java.util.List<String> manyItems = Arrays.asList("bar", "baz");

        // Additive / sort APIs
        val additions = addJavaList(singleItem)
            .andThen(addAllJavaList(manyItems))
            .andThen(mapJavaList(String::toUpperCase))
            .andThen(sortJavaList(String::compareTo))
            // Java LinkedList
            .andThen(addJavaLinkedList(singleItem))
            .andThen(addAllJavaLinkedList(new LinkedList<>(manyItems)))
            .andThen(mapJavaLinkedList(String::toUpperCase))
            .andThen(sortJavaLinkedList(String::compareTo))
            // Java ArrayList
            .andThen(addJavaArrayList(singleItem))
            .andThen(addAllJavaArrayList(new ArrayList<>(manyItems)))
            .andThen(mapJavaArrayList(String::toUpperCase))
            .andThen(sortJavaArrayList(String::compareTo))
            // Java HashSet
            .andThen(addJavaHashSet(singleItem))
            .andThen(addAllJavaHashSet(new HashSet<>(manyItems)))
            .andThen(mapJavaHashSet(String::toUpperCase))
            // Java Set
            .andThen(addJavaSet(singleItem))
            .andThen(addAllJavaSet(new HashSet<>(manyItems)))
            .andThen(mapJavaSet(String::toUpperCase))
            // Vavr HashSet
            .andThen(addVavrHashSet(singleItem))
            .andThen(addAllVavrHashSet(io.vavr.collection.HashSet.ofAll(manyItems)))
            .andThen(mapVavrHashSet(String::toUpperCase))
            // Vavr Array
            .andThen(appendVavrArray(singleItem))
            .andThen(appendAllVavrArray(Array.ofAll(manyItems)))
            .andThen(mapVavrArray(String::toUpperCase))
            .andThen(prependVavrArray(singleItem))
            .andThen(prependAllVavrArray(Array.ofAll(manyItems)))
            // Vavr List
            .andThen(appendVavrList(singleItem))
            .andThen(appendAllVavrList(List.ofAll(manyItems)))
            .andThen(mapVavrList(String::toUpperCase))
            .andThen(prependVavrList(singleItem))
            .andThen(prependAllVavrList(List.ofAll(manyItems)))
            .apply(colls);

        assertEquals(Array.of("BAR", "BAZ", "ZOO"), Array.ofAll(additions.getJavaList()));
        assertEquals(Array.of("BAR", "BAZ", "ZOO"), Array.ofAll(additions.getJavaLinkedList()));
        assertEquals(Array.of("BAR", "BAZ", "ZOO"), Array.ofAll(additions.getJavaArrayList()));
        assertEquals(Array.of("BAR", "BAZ", "ZOO").toJavaSet(), additions.getJavaHashSet());
        assertEquals(Array.of("BAR", "BAZ", "ZOO").toJavaSet(), additions.getJavaSet());

        assertEquals(Array.of("bar", "baz", "zoo", "ZOO", "BAR", "BAZ"), additions.getVavrArray());
        assertEquals(List.of("bar", "baz", "zoo", "ZOO", "BAR", "BAZ"), additions.getVavrList());
        assertEquals(Array.of("BAR", "BAZ", "ZOO").toSet(), additions.getVavrHashSet());
    }
}
