package com.wds.util;

import com.wds.util.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void zipResources() throws IOException {
        Path path = Paths.get("src","test","resources");
        File srcFile = createFileWithDirectory(path,"src", "lambda.js");
        File nodeFile = createFileWithDirectory(path, "node_modules", "resource.js");
        File zip = FileUtils.zipResources(srcFile.getParentFile(), asList(nodeFile.getParentFile()));

        try {
            assertTrue(ZipUtil.containsEntry(zip, "lambda.js"));
            assertTrue(ZipUtil.containsEntry(zip, "node_modules"));
        }
        finally {
            org.apache.commons.io.FileUtils.deleteDirectory(srcFile.getParentFile());
            org.apache.commons.io.FileUtils.deleteDirectory(nodeFile.getParentFile());
            org.apache.commons.io.FileUtils.deleteQuietly(new File("/unzipped"));
        }
    }

    private File createFileWithDirectory(Path path, String directory, String filename) throws IOException {
        File dir = new File(path.toString()+"/"+directory);
        if (!dir.exists()) dir.mkdirs();
        File newFile = new File(dir.getPath() + "/" + filename);
        Files.write(newFile.toPath(), "js".getBytes(), StandardOpenOption.CREATE);
        return newFile;
    }
}