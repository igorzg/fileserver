package org.icoder.http.handlers;

import org.icoder.http.Methods;
import org.icoder.http.Request;
import org.icoder.http.responses.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public class StaticFileHandler implements Handler {
    private String rootPath;
    private String defaultPath;

    public StaticFileHandler(String rootPath, String defaultPath) {
        this.rootPath = rootPath;
        this.defaultPath = defaultPath;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        if (defaultPath != null && "/".equals(path)) {
            path = defaultPath;
        }
        Response response = new StreamResponse(405, "Method not implemented!");

        switch (request.getMethod()) {
            case Methods.GET:
                Path requestedFile = Paths.get(rootPath, path);
                if (requestedFile.normalize().startsWith(Paths.get(rootPath).normalize())) {
                    if (Files.exists(requestedFile)) {
                        if (Files.isDirectory(requestedFile)) {
                            response = new StreamResponse(403);
                        } else {
                            response = new FileResponse(
                                    200,
                                    new File(Paths.get(rootPath, path).toString())
                            );
                        }
                    } else {
                        response = new StreamResponse(404);
                    }
                } else {
                    response = new StreamResponse(403);
                }
                break;
            case Methods.HEAD:
                if (Files.exists(Paths.get(rootPath, path))) {
                    response = new FileHeadResponse(
                            200,
                            new File(Paths.get(rootPath, path).toString())
                    );
                } else {
                    response = new StreamResponse(404);
                }
                break;
        }

        return response;
    }
}
