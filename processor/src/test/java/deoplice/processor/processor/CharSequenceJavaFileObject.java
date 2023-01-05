package deoplice.processor.processor;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Stolen from https://github.com/jOOQ/jOOR/blob/7c27785e5c82bd60bb73a5eee569f3d0fb4df01b/jOOR-java-8/src/main/java/org/joor/Compile.java
 */
public class CharSequenceJavaFileObject extends SimpleJavaFileObject {
    final CharSequence content;

    public CharSequenceJavaFileObject(String className, CharSequence content) {
        super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
        this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }
}
