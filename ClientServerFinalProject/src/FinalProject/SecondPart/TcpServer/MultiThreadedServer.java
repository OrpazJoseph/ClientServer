package FinalProject.SecondPart.TcpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiThreadedServer {

    private final int port;
    private volatile boolean stopServer; // a volatile variable is a variable that is kept in RAM
    private static final TcpServer tcpServer = new TcpServer();

    public MultiThreadedServer(int port) {
        this.port = port;
        stopServer = false;
    }
    public void threadedServer() throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!stopServer) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for each client in order to allow concurrently
            new TcpServer(socket).start();
        }
        serverSocket.close();
    }

    public void stop() {
        ReentrantReadWriteLock myLock = new ReentrantReadWriteLock();
        myLock.writeLock().lock();
        if (!stopServer) {
            stopServer = true;
            tcpServer.stopServer();
        }
        myLock.writeLock().unlock();
    }

    public static void main(String[] args) throws IOException {
        MultiThreadedServer multiThreadedServer = new MultiThreadedServer(8010);
        multiThreadedServer.threadedServer();
    }
}
// Credit to Piotr Kochański and Martin Vseticka from stackoverflow for helping with the solution.