package org.unibits;

import java.io.File;

public interface VttSrtConverter {

    void setSrcFile(File file);

    File getOutputFile();

    void convertVttToSrt();
}
