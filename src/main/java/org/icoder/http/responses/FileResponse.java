package org.icoder.http.responses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public class FileResponse extends FileHeadResponse {


    public FileResponse(int statusCode, File inputFile) {
        super(statusCode, inputFile);
    }

    /**
     * This function writes the HTTP response to an output stream.
     */
    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        if (inputFile != null) {
            write(new FileInputStream(this.inputFile), bufferedWriter);
        }
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(FileResponse.class);
    }
}
