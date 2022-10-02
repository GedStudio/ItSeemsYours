package net.deechael.isy.jvmnative.lang;

import net.deechael.isy.bridge.IsyClass;
import net.deechael.isy.bridge.IsyConstructor;
import net.deechael.isy.bridge.IsyMethod;

import java.io.UnsupportedEncodingException;

@IsyClass(fileName = "isy.Lang", className = "Str")
public class IsyStr {

    private final String string;

    @IsyConstructor
    public IsyStr(byte[] bytes) {
        this.string = new String(bytes);
    }

    @IsyConstructor
    public IsyStr(byte[] bytes, String encoding) {
        try {
            this.string = new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @IsyMethod
    public int length() {
        return this.string.length();
    }

    @IsyMethod
    public String substr(int from) {
        return this.string.substring(from);
    }

    @IsyMethod
    public String substr(int from, int to) {
        return this.string.substring(from, to);
    }

    @IsyMethod
    public String replace(String oldStr, String newStr) {
        return this.string.replace(oldStr, newStr);
    }

    @IsyMethod
    public String replaceAll(String regex, String newStr) {
        return this.string.replaceAll(regex, newStr);
    }

    @IsyMethod
    public char[] charArray() {
        return this.string.toCharArray();
    }

}
