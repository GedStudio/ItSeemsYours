package net.deechael.isy.compiler;

import java.util.ArrayList;
import java.util.List;

public class IsyClass {

    private final String name;

    private final List<IsyConstructor> constructors = new ArrayList<>();

    private final List<IsyField> fields = new ArrayList<>();

    private final List<IsyMethod> methods = new ArrayList<>();

    public IsyClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
