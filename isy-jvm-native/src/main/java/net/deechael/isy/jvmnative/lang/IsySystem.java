package net.deechael.isy.jvmnative.lang;

import net.deechael.isy.bridge.IsyClass;
import net.deechael.isy.bridge.IsyMethod;

@IsyClass(fileName = "isy.Lang", className = "System")
public class IsySystem {

    @IsyMethod
    public static void print(String string) {
        System.out.print(string);
    }

}
