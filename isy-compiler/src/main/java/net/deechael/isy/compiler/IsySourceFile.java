package net.deechael.isy.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class IsySourceFile {

    public IsySourceFile(File file) throws IOException {
        List<String> source = Files.readAllLines(file.toPath());
        int startLine = 0;
        int endLine = 0;
        for (int i = 0; i < source.size(); i++) {
            int line = i + 1;
            String lineSource = source.get(i);
            while (lineSource.startsWith(" "))
                lineSource = lineSource.substring(1);
            if (!lineSource.startsWith("class "))
                continue;
            if (startLine == 0) {
                startLine = line;
                continue;
            }
            endLine = line - 1;

        }
    }

}
