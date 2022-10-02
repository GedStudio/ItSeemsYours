package net.deechael.isy.compiler;

import java.util.*;

public class IsyCode {

    private final List<IsyCodeBase> codeBases = new ArrayList<>();

    private final Map<String, List<String>> importations = new HashMap<>();

    public IsyCode(IsyClass isyClass, List<IsyCodeBase> codeBases) {
        this.codeBases.addAll(codeBases);
        analyseImportations();
        analyseStructures();
    }

    public List<IsyCodeBase> getCodeBases() {
        return codeBases;
    }

    public Map<String, List<String>> getImportations() {
        return importations;
    }

    private void analyseImportations() {
        for (IsyCodeBase base : this.codeBases) {
            if (!(base instanceof IsyCodeContent))
                continue;
            IsyCodeContent isyCodeContent = (IsyCodeContent) base;
            String content = isyCodeContent.getLine();
            while (content.startsWith(" "))
                content = content.substring(1);
            if (!content.startsWith("import"))
                throw new RuntimeException("Unexpected expressions out of class structure");
            content = content.substring(6);
            while (content.startsWith(" "))
                content = content.substring(1);
            StringBuilder fileNameBuilder = new StringBuilder();
            for (int i = content.length() - 1; i >= 0; i--) {
                if (content.charAt(i) == ' ')
                    break;
                fileNameBuilder.insert(0, content.charAt(i));
            }
            String fileName = fileNameBuilder.toString();
            content = content.substring(0, content.length() - fileName.length());
            while (content.endsWith(" "))
                content = content.substring(0, content.length() - 1);
            if (!content.endsWith(" in"))
                throw new RuntimeException("Unexpected importation expressions");
            content = content.substring(0, content.length() - 3);
            content = content.replace(" ", "");
            String[] classNames = content.contains(",") ? content.split(",") : new String[] {content};
            this.importations.put(fileName, Arrays.asList(classNames));
        }
    }

    private void analyseStructures() {
        for (IsyCodeBase base : this.codeBases) {
            if (!(base instanceof IsyCodeStructure))
                continue;
            IsyCodeStructure isyCodeStructure = (IsyCodeStructure) base;
            if (!(isyCodeStructure.getBeforePart().startsWith("class ") ||
                    isyCodeStructure.getBeforePart().startsWith("interface ") ||
                    isyCodeStructure.getBeforePart().startsWith("enum ") ||
                    isyCodeStructure.getBeforePart().startsWith("annotation ")
            ))
                throw new RuntimeException("The outest structure must be class");
        }
    }

    public interface IsyCodeBase {

    }

    public static class IsyCodeStructure implements IsyCodeBase {

        private final String beforePart;
        private final List<IsyCodeBase> codeBases = new ArrayList<>();

        public IsyCodeStructure(IsyClass isyClass, String beforePart, List<IsyCodeBase> codeBases) {
            this.beforePart = beforePart;
            this.codeBases.addAll(codeBases);
        }

        public String getBeforePart() {
            return beforePart;
        }

        public List<IsyCodeBase> getCodeBases() {
            return codeBases;
        }

    }

    public static class IsyCodeContent implements IsyCodeBase {

        private String line;
        private List<Operation> operation = null;

        public IsyCodeContent(IsyClass isyClass, String line, boolean methodContent) {
            this.line = line;
            if (methodContent)
                analyseOperation();
        }

        public String getLine() {
            return line;
        }

        private void analyseOperation() {
            // TODO
            if (this.line.contains("=") && !this.line.contains("==")) {
                String firstString = null;
                String secondString = null;
                int pos = 0;
                int length = this.line.length();
                while (pos < length) {
                    char currentChar = this.line.charAt(pos);
                    if (currentChar == ' ' || currentChar == '\n') {
                        pos++;
                        continue;
                    }
                    if (currentChar == '"')
                        throw new RuntimeException("Wrong expressions");
                    else {
                        String content = "";
                        while (pos < length) {
                            char current = line.charAt(pos);
                            if (current == ' ' || current == '\n') {
                                if (firstString == null) {
                                    firstString = content;
                                } else if (secondString == null) {
                                    secondString = content;
                                }
                            } else if (current == '=') {
                                if (firstString == null)
                                    throw new RuntimeException("Wrong expressions");
                                pos++;
                                String varName = secondString;
                                if (secondString != null) {
                                    varName = firstString;
                                    this.operation.add(new CreateVariable(firstString, secondString));
                                }
                                char c = line.charAt(pos);
                                if (c == '\'') {
                                    if (line.charAt(pos + 2) != '\'')
                                        throw new RuntimeException("Wrong expressions");
                                    int pos2 = pos + 3;
                                    while (pos2 < length) {
                                        if (line.charAt(pos2) != '\n' && line.charAt(pos2) != ' ')
                                            throw new RuntimeException("Wrong expressions");
                                        pos2++;
                                    }
                                    this.operation.add(new GiveValue(varName, new IsyCharacter(line.charAt(pos + 1))));
                                    return;
                                } else if (c == '"') {
                                    StringBuilder stringValue = new StringBuilder();
                                    int pos2 = pos + 1;
                                    boolean stringEnd = false;
                                    while (pos2 < length) {
                                        char c2 = line.charAt(pos2);
                                        if (stringEnd && c2 != '\n' && c2 != ' ')
                                            throw new RuntimeException("Wrong expressions");
                                        if (stringEnd)
                                            continue;
                                        if (c2 == '"') {
                                            stringEnd = true;
                                            continue;
                                        }
                                        stringValue.append(c2);
                                        pos2++;
                                    }
                                    this.operation.add(new GiveValue(varName, new IsyString(stringValue.toString())));
                                    return;
                                } else {
                                    StringBuilder stringValue = new StringBuilder();
                                    int pos2 = pos;
                                    while (pos2 < length) {
                                        stringValue.append(line.charAt(pos2));
                                        pos2++;
                                    }
                                    String value = stringValue.toString();
                                    while (value.startsWith(" ") || value.startsWith("\n"))
                                        value = value.substring(1);
                                    while (value.endsWith(" ") || value.endsWith("\n"))
                                        value = value.substring(0, value.length() - 1);
                                    try {
                                        int i = Integer.parseInt(value);
                                        this.operation.add(new GiveValue(varName, new IsyInteger(i)));
                                        return;
                                    } catch (NumberFormatException ignored) {
                                        try {
                                            double i = Double.parseDouble(value);
                                            this.operation.add(new GiveValue(varName, new IsyDouble(i)));
                                            return;
                                        } catch (NumberFormatException ignored2) {
                                            switch (value) {
                                                case "true":
                                                    this.operation.add(new GiveValue(varName, new IsyBoolean(true)));
                                                    return;
                                                case "false":
                                                    this.operation.add(new GiveValue(varName, new IsyBoolean(false)));
                                                    return;
                                                case "null":
                                                    this.operation.add(new GiveValue(varName, new IsyNull()));
                                                    return;
                                                default:
                                                    this.operation.add(new GiveValue(varName, new IsyVariable(value)));
                                                    return;
                                            }
                                        }
                                    }
                                }
                            }
                            pos++;
                        }
                    }
                }
            }
        }

    }

    private interface Operation {}

    private static class CreateVariable implements Operation {

        private final String type;
        private final String name;

        public CreateVariable(String type, String name) {
            this.type = type;
            this.name = name;
        }

    }

    private static class GiveValue implements Operation {

        private final String variableName;
        private final IsyObj value;

        public GiveValue(String variableName, IsyObj value) {
            this.variableName = variableName;
            this.value = value;
        }

    }

    private static class InvokeMethod implements Operation {

        private final String target;
        private final String method;
        private final List<IsyObj> parameters;

        public InvokeMethod(String target, String methodName, IsyObj... parameters) {
            this.target = target;
            this.method = methodName;
            this.parameters = Arrays.asList(parameters);
        }

    }

    private interface IsyObj {}

    private static class IsyString implements IsyObj {

        private final String value;

        public IsyString(String value) {
            this.value = value;
        }

    }

    private static class IsyVariable implements IsyObj {

        private final String value;

        public IsyVariable(String value) {
            this.value = value;
        }

    }

    private static class IsyInteger implements IsyObj {

        private final int value;

        public IsyInteger(int value) {
            this.value = value;
        }

    }

    private static class IsyBoolean implements IsyObj {

        private final boolean value;

        public IsyBoolean(boolean value) {
            this.value = value;
        }

    }

    private static class IsyDouble implements IsyObj {

        private final double value;

        public IsyDouble(double value) {
            this.value = value;
        }

    }

    private static class IsyCharacter implements IsyObj {

        private final char value;

        private IsyCharacter(char value) {
            this.value = value;
        }

    }

    private static class IsyNull implements IsyObj {

    }

}
