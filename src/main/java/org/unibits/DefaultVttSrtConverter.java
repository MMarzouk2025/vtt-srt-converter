package org.unibits;

import java.io.*;
import java.util.regex.Pattern;

public class DefaultVttSrtConverter implements VttSrtConverter {

    private File srcFile;
    private File outputFile;
    private int counter;

    private final Pattern timingPattern = Pattern.compile(".*(\\d{2}:){2}\\d{2}\\.\\d{3}.*");

    public DefaultVttSrtConverter() {
    }

    public DefaultVttSrtConverter(String srcFilePath) {
        srcFile = new File(srcFilePath);
    }

    @Override
    public void setSrcFile(File file) {
        srcFile = file;
    }

    @Override
    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public void convertVttToSrt() {
        BufferedReader srcFileReader;
        BufferedWriter outputFileWriter;

        createOutputFile();

        try {
            srcFileReader = new BufferedReader(new FileReader(this.srcFile));
            outputFileWriter = new BufferedWriter(new FileWriter(this.outputFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        srcFileReader.lines()
                .skip(1)
                .forEach(line -> {
                    String convertedLine = convertDotsToCommasIfTimeStringAndAddNumbering(line);
                    addNewLineToOutputFile(outputFileWriter, convertedLine);
                });

        try {
            srcFileReader.close();
            outputFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createOutputFile() {
        String srcFilePath = srcFile.getPath();
        String outputFilePath = srcFilePath.substring(0, srcFilePath.length() - 4) + ".srt";
        outputFile = new File(outputFilePath);

        boolean outputNewFileCreated;
        try {
            outputNewFileCreated = outputFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!outputNewFileCreated) {
            throw new RuntimeException("file " + outputFilePath + " is already existing!!");
        }
    }

    private String convertDotsToCommasIfTimeStringAndAddNumbering(String str) {
        if (timingPattern.matcher(str).matches()) {
            counter++;
            String numberingLine = counter + "\n";
            return numberingLine + str.replaceAll("\\.", ",");
        }
        return str;
    }

    private void addNewLineToOutputFile(BufferedWriter outputFileWriter, String line) {
        try {
            outputFileWriter.write(line);
            outputFileWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
