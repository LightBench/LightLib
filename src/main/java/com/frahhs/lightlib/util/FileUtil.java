package com.frahhs.lightlib.util;

import com.frahhs.lightlib.LightPlugin;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class for file operations.
 */
public abstract class FileUtil {

    /**
     * Checks if a file is empty.
     *
     * @param file The file to check.
     * @return true if the file exists and its length is 0, false otherwise.
     * @throws IllegalArgumentException If the file does not exist.
     */
    public static boolean isFileEmpty(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Cannot check the file length. The file is not found: " + file.getAbsolutePath());
        }
        return file.length() == 0;
    }

    /**
     * Compresses a file into a .tar.gz archive and deletes the original file.
     *
     * @param file The file to compress.
     * @throws RuntimeException If an error occurs during compression or file deletion.
     */
    public static void toTarGzipFile(File file) {
        Path inputPath = file.toPath();
        Path output = Paths.get(inputPath.getParent().toString(), inputPath.getFileName().toString().replace(".log", "") + ".tar.gz");

        try {
            // Compress the file to .tar.gz
            compressFile(file, output.toFile());

            // Delete the original file
            if (!file.delete()) {
                throw new RuntimeException("Failed to delete log file after compression: " + file.getPath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compresses a file using tar and GZIP compression.
     *
     * @param inputFile  The input file to compress.
     * @param outputFile The output file where compressed data will be written.
     * @throws IOException If an I/O error occurs during compression.
     */
    public static void compressFile(File inputFile, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(bos);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzipOut)) {

            TarArchiveEntry tarEntry = new TarArchiveEntry(inputFile, inputFile.getName());
            tarEntry.setSize(inputFile.length());

            tarOut.putArchiveEntry(tarEntry);

            try (FileInputStream fis = new FileInputStream(inputFile);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {

                byte[] buffer = new byte[1024];
                int len;
                while ((len = bis.read(buffer)) != -1) {
                    tarOut.write(buffer, 0, len);
                }
            }

            tarOut.closeArchiveEntry();
        }
    }
}
