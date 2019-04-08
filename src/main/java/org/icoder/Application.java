package org.icoder;

import org.icoder.http.handlers.StaticFileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author igorzg on 2019-04-07.
 * @since 1.0
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public Application(int port, int workers, StaticFileHandler fileServing) {
        final LinkedBlockingQueue<Runnable> connections = new LinkedBlockingQueue<>();
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                workers,
                workers,
                10,
                TimeUnit.SECONDS,
                connections
        );
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleAtFixedRate(
                () -> logger.info(String.format(
                        "[%d/%d] Active: %d, Completed: %d, Task: %d, queueSize: %d",
                        threadPoolExecutor.getPoolSize(),
                        threadPoolExecutor.getCorePoolSize(),
                        threadPoolExecutor.getActiveCount(),
                        threadPoolExecutor.getCompletedTaskCount(),
                        threadPoolExecutor.getTaskCount(),
                        threadPoolExecutor.getQueue().size()
                )),
                0,
                5,
                TimeUnit.SECONDS
        );
        try {
            final ServerSocket serverSocket = new ServerSocket(port);
            while (serverSocket.isBound()) {
                threadPoolExecutor.execute(new SocketHandler(serverSocket.accept(), fileServing));
            }
        } catch (IOException e) {
            logger.error(Application.class.getName(), e);
        } finally {
            threadPoolExecutor.shutdown();
            scheduledExecutorService.shutdown();
        }
    }

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.size() >= 3) {
            String defaultFile = "index.html";
            if (argList.size() >= 4) {
                defaultFile = argList.get(3);
            }
            new Application(
                    Integer.parseInt(argList.get(0)),
                    Integer.parseInt(argList.get(1)),
                    new StaticFileHandler(argList.get(2), defaultFile)
            );
        } else {
            logger.warn("Usage: \njava -jar build/libs/icoder-1.0.jar <port> <threads> <public-folder> <defaults-to-index.html>");
        }
    }
}
