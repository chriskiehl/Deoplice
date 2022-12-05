package deoplice.processor.codegen;

import deoplice.processor.types.QualifiedType;
import deoplice.processor.types.VariableName;

public class Stuff {

    public static VariableName titleCase(VariableName s) {
        return VariableName.of(Character.toTitleCase(s.getValue().charAt(0)) + s.getValue().substring(1));
    }

    public static String unqualify(QualifiedType qualifiedType) {
        // TODO: is + 1 safe?
        return qualifiedType.toString().substring(qualifiedType.toString().lastIndexOf(".")).replace(".", "");
    }

}
