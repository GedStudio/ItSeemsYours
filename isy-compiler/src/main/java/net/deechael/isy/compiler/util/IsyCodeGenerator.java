package net.deechael.isy.compiler.util;

import net.deechael.isy.compiler.IsyClass;
import net.deechael.isy.compiler.IsyCode;
import net.deechael.useless.objs.TriObj;

import java.util.ArrayList;
import java.util.List;

public class IsyCodeGenerator {

    public static IsyCode generate(IsyClass isyClass, final String code) {
        int pos = 0;
        int length = code.length();
        List<IsyCode.IsyCodeBase> codeBases = new ArrayList<>();
        int line = 1;
        while (pos < length) {
            char currentChar = code.charAt(pos);
            if (currentChar == ' ') {
                pos++;
                continue;
            }
            if (currentChar == '\n') {
                pos++;
                line += 1;
                continue;
            }
            StringBuilder content = new StringBuilder();
            boolean inString = false;
            while (pos < length) {
                char current = code.charAt(pos);
                if (inString) {
                    if (current == '"') {
                        inString = false;
                    }
                } else {
                    if (current == '{') {
                        TriObj<IsyCode.IsyCodeStructure, Integer, Integer> triObj = findStructure(isyClass, content.toString(), pos, line, code);
                        codeBases.add(triObj.getObject());
                        pos = triObj.getSecond();
                        line = triObj.getThird();
                        break;
                    } else if (current == '"') {
                        inString = true;
                    } else if (current == ';') {
                        pos++;
                        codeBases.add(new IsyCode.IsyCodeContent(isyClass, content.toString(), false));
                        break;
                    }
                }
                content.append(current);
                pos++;
            }
        }
        return new IsyCode(isyClass, codeBases);
    }

    private static TriObj<IsyCode.IsyCodeStructure, Integer, Integer> findStructure(IsyClass isyClass, String beforePart, int pos, int line, final String code) {
        List<IsyCode.IsyCodeBase> bases = new ArrayList<>();
        pos += 1;
        while (pos < code.length()) {
            char currentChar = code.charAt(pos);
            if (currentChar == ' ') {
                pos++;
                continue;
            }
            if (currentChar == '\n') {
                pos++;
                line += 1;
                continue;
            }
            if (currentChar == '}') {
                pos++;
                break;
            }
            StringBuilder content = new StringBuilder();
            boolean inString = false;
            while (pos < code.length()) {
                char current = code.charAt(pos);
                if (inString) {
                    if (current == '"') {
                        inString = false;
                    }
                } else {
                    if (current == '{') {
                        TriObj<IsyCode.IsyCodeStructure, Integer, Integer> triObj = findStructure(isyClass, content.toString(), pos, line, code);
                        bases.add(triObj.getObject());
                        pos = triObj.getSecond();
                        line = triObj.getThird();
                        break;
                    } else if (current == '"') {
                        inString = true;
                    } else if (current == ';') {
                        pos++;
                        bases.add(new IsyCode.IsyCodeContent(isyClass, content.toString(), true));
                        break;
                    }
                }
                content.append(current);
                pos++;
            }
        }
        return new TriObj<>(new IsyCode.IsyCodeStructure(isyClass, beforePart, bases), pos, line);
    }

}
