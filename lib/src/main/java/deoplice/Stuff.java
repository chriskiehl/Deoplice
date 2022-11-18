package deoplice;


import deoplice.processor.Lensed;

public class Stuff {

    @Lensed
    public static class MyStuff {
        String foo;
    }

    public static void main(String... args) {
        System.out.println("Hello");
    }

}
