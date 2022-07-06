package org.unibits;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class DirectoryFilesVttToSrtConverter {

    private static final Logger logger = Logger.getLogger(DirectoryFilesVttToSrtConverter.class.getName());

    private String directoryPath;

    public void convertDirectoryVttFilesToSrtFiles() {
        JFileChooser fileChooser = new JFileChooser("D:\\");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setVisible(true);
        int returnedVal = fileChooser.showOpenDialog(null);
        if (returnedVal == JFileChooser.APPROVE_OPTION) {
            directoryPath = fileChooser.getSelectedFile().getAbsolutePath();
            bulkConvertingForDirectoryFilesFromVttToSrt();
        }
    }

    private void bulkConvertingForDirectoryFilesFromVttToSrt() {
        directoryPath = directoryPath.replace("\\", "\\\\");

        VttSrtConverter vttSrtConverter = new DefaultVttSrtConverter();
        try {
            Stream<Path> filesStream = Files.walk(Paths.get(directoryPath), Integer.MAX_VALUE);
            long filesConvertedCount = filesStream.filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".vtt"))
                    .peek(path -> convertVttFileToSrtFileAndDeleteIt(vttSrtConverter, path))
                    .count();
            logger.info("Total files have been converted: " + filesConvertedCount);
            filesStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void convertVttFileToSrtFileAndDeleteIt(VttSrtConverter vttSrtConverter, Path path) {
        File file = path.toFile();

        vttSrtConverter.setSrcFile(file);
        vttSrtConverter.convertVttToSrt();

        File outputFile = vttSrtConverter.getOutputFile();
        logger.info(outputFile + " >> has been converted successfully!!");

        boolean vttFileConvertedAndDeleted = false;
        if (vttSrtConverter.getOutputFile() != null) {
            vttFileConvertedAndDeleted = file.delete();
        }
        if (!vttFileConvertedAndDeleted) {
            throw new RuntimeException("error while deleting the original vtt file after being converted to srt");
        }
    }

}
