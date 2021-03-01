package FinalProject.SecondPart.TcpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Tcp Server class - a multithreaded server
 */
public class TcpServer extends Thread {
    // a TreadPoolExecutor in order to deal with tasks of all clients in one pool
    private static volatile ThreadPoolExecutor executor = null;
    // handle requests in concurrent manner (each client will be handled in a new Thread)
    // Handler in order to handle different client's tasks
    private IHandler requestConcreteIHandler;
    protected Socket request;

    public TcpServer() {
    }

    public TcpServer(Socket socket) {
        this.request = socket;
        if(executor == null) {
            executor = new ThreadPoolExecutor(
                    3, 5, 10,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        }
        run(new MatrixIHandler());
    }
    /*
     define a task that runs in asynchronous manner (its own thread) represents the server's main logic:
     define a task that while stopServer == false:
     Listen for incoming requests.
     Once a request is accepted, serve client in a dedicated thread
     */

    /**
     * This method gets a concrete subclass that implements IHandler and runs in a different thread.
     * @param concreteIHandlerStrategy defines what to do with the input and what to output to client
     */

    public void run(IHandler concreteIHandlerStrategy) {
        this.requestConcreteIHandler = concreteIHandlerStrategy;

        Runnable mainLogic = () -> {

                        System.out.println("Server::client!");
                        Runnable runnable = () -> {
                            try {
                                System.out.println("Server::handle!");
                                requestConcreteIHandler.handle(request.getInputStream(),
                                        request.getOutputStream());
                                System.out.println("server::Close all streams!");
                                // Close all streams
                                request.getInputStream().close();
                                request.getOutputStream().close();
                                request.close();
                            } catch (Exception e) {
                                System.out.println("server::" + e.getMessage());
                                System.err.println(e.getMessage());
                            }
                        };
                        executor.execute(runnable);
        };
        new Thread(mainLogic).start();
    }

    public void stopServer() {
        ReentrantReadWriteLock myLock = new ReentrantReadWriteLock();
        myLock.writeLock().lock();
            if (executor != null) {
                executor.shutdown();
            }
        myLock.writeLock().unlock();
    }
}