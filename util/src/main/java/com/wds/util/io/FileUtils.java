package com.wds.util.io;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * more here: https://www.baeldung.com/reading-file-in-java
 */
public class FileUtils {

    public static List<String> readFileLines(String filePath) throws IOException {
        List<String> result;
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            result = lines.collect(Collectors.toList());
        }
        return result;
    }

    public static BiConsumer<File, File> fileMover = (dirSrc, dirTarget) -> {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(dirSrc, dirTarget);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public static BiConsumer<File, File> directoryMover = (source, target) -> {
        try {
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(source, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };


    public static <T> Consumer<T> moveDir(BiConsumer<T, T> mover, T target) {
        return source -> mover.accept(source, target);
    }

    // from ad-systems/personalizer/deployer
    public static File zipResources(File lambdaFileFolder, List<File> lambdaResourceFolders) throws IOException {

        File lambdazip = File.createTempFile("lambda", "zip");

        File targetDir = new File("lambda-zip-temp");
        if (!targetDir.exists()) targetDir.mkdirs();
        else org.apache.commons.io.FileUtils.cleanDirectory(targetDir);

        fileMover.accept(lambdaFileFolder, targetDir);
        lambdaResourceFolders.forEach(moveDir(directoryMover, targetDir));

        ZipUtil.pack(targetDir, lambdazip);
        org.apache.commons.io.FileUtils.deleteQuietly(targetDir);
        return lambdazip;
    }
}
