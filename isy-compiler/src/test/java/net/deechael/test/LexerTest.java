package net.deechael.test;

import net.deechael.isy.compiler.IsyCode;
import net.deechael.isy.compiler.util.IsyCodeGenerator;

import java.util.List;
import java.util.Map;

public class LexerTest {

    private final static String EXAMPLE_CODE = "import List, ArrayList in isy.Util;\n" +
            "import JClass, JMethod, JConstructor, Var, JField, JStringVar in deechael.DynamicClassGenerator;" +
            "\n" +
            "class Example {\n" +
            "    static Void main(Array<Str> args) {\n" +
            "        List<Str> list = new ArrayList<>();\n" +
            "        list.add(\"a\");\n" +
            "        list.add(\"b\");\n" +
            "        list.add(\"c\");\n" +
            "        list.add(\"d\");\n" +
            "        System.print(\"Elements: \");\n" +
            "        foreach Str str in list as Loop loop {\n" +
            "            System.print(str);\n" +
            "            if (loop.hasNext()) {\n" +
            "                System.print(\", \");\n" +
            "            }\n" +
            "        }\n" +
            "        System.print(\"\\n\");\n" +
            "        System.println(\"Hello, Isy!\");\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "class ExampleClass {\n" +
            "    \n" +
            "    constructor() {\n" +
            "        System.println(\"Just a test\");\n" +
            "    }\n" +
            "    \n" +
            "    Void printMessage() {\n" +
            "        System.println(\"Printed a message!\");\n" +
            "    }\n" +
            "    \n" +
            "}";

    public static void main(String[] args) {
        IsyCode code = IsyCodeGenerator.generate(null, EXAMPLE_CODE);
        for (IsyCode.IsyCodeBase base : code.getCodeBases()) {
            if (base instanceof IsyCode.IsyCodeStructure) {
                print("", (IsyCode.IsyCodeStructure) base);
            } else {
                System.out.println(((IsyCode.IsyCodeContent) base).getLine());
            }
        }

        for (Map.Entry<String, List<String>> entry : code.getImportations().entrySet()) {
            System.out.println("Imported in " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private static void print(String prefix, IsyCode.IsyCodeStructure structure) {
        System.out.println(prefix + "Structure: " + structure.getBeforePart());
        for (IsyCode.IsyCodeBase base : structure.getCodeBases()) {
            if (base instanceof IsyCode.IsyCodeStructure) {
                print(prefix + "    ", (IsyCode.IsyCodeStructure) base);
            } else {
                System.out.println(prefix + "    " + ((IsyCode.IsyCodeContent) base).getLine());
            }
        }
    }

}
