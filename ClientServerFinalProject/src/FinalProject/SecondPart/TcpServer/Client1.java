package FinalProject.SecondPart.TcpServer;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Client1 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // In order to request something over TCP from a server, we need a port number and an IP address
        Socket socket = new Socket("127.0.0.1",8010);
        // socket is an abstraction of 2-way data pipe
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        // use decorators
        ObjectInputStream fromServer = new ObjectInputStream(inputStream);
        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);

        int[][] source = {
                {1, 1, 1, 1, 0},
                {0, 1, 1, 0, 0},
                {1, 0, 1, 1, 1},
        };
        int[][] source2 = {
                {1, 0},
                {1, 1}
        };

        toServer.writeObject("matrix");
        // according to protocol, after "matrix" string, send 2d int array
        toServer.writeObject(source2);

        toServer.writeObject("AdjacentIndices");
        toServer.writeObject(new Index(0,0));

        Collection<Index> adjacentIndices = new ArrayList<>((Collection<Index>)fromServer.readObject());
        System.out.println("Neighbors: " + adjacentIndices);

        toServer.writeObject("Reachables");
        toServer.writeObject(new Index(0,0));
        Collection<Index> reachableIndices = new ArrayList<>((Collection<Index>)fromServer.readObject());
        System.out.println("Reachables: " + reachableIndices);

        /**
         * Task 1: Find all groups of 1's. Aka find all connected components
         */
        toServer.writeObject("findSetsOfOnes");
        List<HashSet<Index>> findSetsOfOnes = new ArrayList<>((List<HashSet<Index>>)fromServer.readObject());
        if(findSetsOfOnes.size() == 0){
            System.out.println("There are no indices with a value of 1\nPlease try again!");
        } else {
            System.out.println("List of sets of 1's: \n" + findSetsOfOnes.stream()
                    .map(AbstractCollection::toString).collect(Collectors.joining("\n")));
        }

        /**
         * Task 2: Find all shortest paths from source index to destination index
         */

        toServer.writeObject("start index");
        toServer.writeObject(new Index(0, 0));

        toServer.writeObject("end index");
        toServer.writeObject(new Index(1, 1));

        toServer.writeObject("allShortestPath");
        Set<List<Index>> listOfShortestPaths =
                new HashSet<>((Set<List<Index>>)fromServer.readObject());
        if(listOfShortestPaths.size() == 0){
            System.out.println("There is no path from source to dest\nPlease try again!");
        } else {
            System.out.println("List of shortest paths: \n" + listOfShortestPaths.stream()
                    .map(Object::toString).collect(Collectors.joining("\n")));
        }

        /**
         * Task 3: Submarine game
         */
        toServer.writeObject("submarinesGame");
        int res = (int) fromServer.readObject();
        System.out.println("Submarine Game: " + res);

        /**
         * Task 4: Find all paths from source index to destination index
         */
        toServer.writeObject("start index");
        toServer.writeObject(new Index(0, 0));

        toServer.writeObject("end index");
        toServer.writeObject(new Index(1, 1));

        toServer.writeObject("findAllPaths");
        List<List<Index>> listOfAllPaths =
                new ArrayList<>((List<List<Index>>)fromServer.readObject());
        if(listOfAllPaths.size() == 0){
            System.out.println("There is no path from source to dest\nPlease try again!");
        }else {
            System.out.println("List of all paths: \n" + listOfAllPaths.stream()
                    .map(Object::toString).collect(Collectors.joining("\n")));
        }

        /**
         * All task are over, sending stop command to the server
         */

        toServer.writeObject("stop");
        fromServer.close();
        toServer.close();

        socket.close();
        System.out.println("All streams are closed");
    }
}
