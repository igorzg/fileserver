package org.icoder.http.responses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public class FileHeadResponse extends Response {

    protected File inputFile;

    public FileHeadResponse(int statusCode, File inputFile) {
        super(statusCode);
        this.inputFile = inputFile;
        try {
            Path source = Paths.get(this.inputFile.toURI());
            String contentType = Files.probeContentType(source);
            if (contentType != null) {
                headers.put("Content-Type", contentType);
            }
            headers.put("Content-Length", String.valueOf(this.inputFile.length()));
        } catch (IOException e) {
            getLogger().error("File don't exists!", e.getMessage());
        }
    }

    @Override
    protected void write(BufferedWriter writer) throws IOException {

    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(FileHeadResponse.class);
    }
}
