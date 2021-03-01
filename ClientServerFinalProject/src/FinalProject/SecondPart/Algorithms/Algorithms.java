package FinalProject.SecondPart.Algorithms;

import FinalProject.SecondPart.TcpServer.Index;
import FinalProject.SecondPart.TcpServer.Matrix;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithms {

    private static Map<Index, Integer> mapOfOnes = new HashMap<>();

    /***********************************************************************************
     * Function for returning map of indices of 1 in the matrix
     * @param matrix Matrix
     * @param twoDArray 2 dimension array
     * Auxiliary function to adding all 1 indices of the matrix into a map
     ************************************************************************************/

    public static Map<Index, Integer> getMapOfOnes(Matrix matrix, int[][] twoDArray) {
        for (Index index: matrix.getOnes(twoDArray)){
            mapOfOnes.put(index, 1);
        }
        return mapOfOnes;
    }

    /***********************************************************************************
     * Task 1: Finding all sets of ones
     * @param twoDArray 2 dimension array
     * Finding every connected component in the matrix
     * and sorting them by size of each component
     ************************************************************************************/

    public static List<HashSet<Index>> findSetsOfOnes(int[][] twoDArray){
        Matrix matrix = new Matrix(twoDArray);
        List<HashSet<Index>> filtered = new ArrayList<>();
        mapOfOnes = getMapOfOnes(matrix, twoDArray);

            // Iterate over the map of indices
            // For each index find it's connected component,
            // then changing it's value to 0 in order to avoid duplications
            // Using auxiliary function: findingConnectedComponent.
            for(Map.Entry<Index, Integer> entry : mapOfOnes.entrySet()) {

                if (entry.getValue() == 1) {
                    mapOfOnes.put(entry.getKey(), 0);
                    HashSet<Index> temp = new HashSet<>(findingConnectedComponent(twoDArray, entry.getKey()));
                    filtered.add(temp);
                }
            }
            // Sorting the list
            return filtered.stream()
                    .sorted(Comparator.comparing(HashSet::size)).collect(Collectors.toList());
    }

    /***********************************************************************************
     * Finding connected component of a given Index
     * @param twoDArray 2 dimension array
     * @param sourceIndex index
     * The function finds and returns a list of the indices that belongs to the connected
     * component of the source index.
     ************************************************************************************/

    public static List<Index> findingConnectedComponent(int[][] twoDArray, Index sourceIndex) {
        Matrix matrix = new Matrix(twoDArray);
        Stack<Index> stack = new Stack<>();
        List<Index> connected = new ArrayList<>();

        // adding source index to the list and push into the stack
        connected.add(sourceIndex);
        stack.push(sourceIndex);

        // Using stack in order to avoid duplication
        // Going over the map, each index with value of 1 is being checked for it's neighbors
        // and add to the list
        // Changing each reachable neighbor's value to 0 in the map
        while (!stack.empty()) {
            for (Index index : matrix.getReachables(stack.pop())) {
                if (mapOfOnes.get(index) == 1) {
                    stack.push(index);
                    connected.add(index);
                    mapOfOnes.put(index, 0);
                }
            }
        }
        return connected;
    }
    /***********************************************************************************
     * inTheSameComponent: check if the indices are in the same connected component
     * auxiliary function for task two and task four.
     * @param twoDArray 2 dimension array
     * @param sourceIndex index
     * @return true if the indices are in the same connected component,
     * otherwise return false.
     ************************************************************************************/
    public static boolean inTheSameComponent(int[][] twoDArray, Index sourceIndex, Index destIndex)  {
        List<HashSet<Index>> connectedComponents = findSetsOfOnes(twoDArray);
            for(HashSet<Index> component : connectedComponents) {
                if(component.contains(sourceIndex) && component.contains(destIndex))
                    return true;
            }
        return false;
    }

    /***********************************************************************************
     * Task 2: Finding all shortest paths
     *  allShortestPath
     * @param twoDArray 2 dimension array
     * @param sourceIndex source index
     * @param destIndex destination index
     * The function finds and returns all the shortest path from the given source index to
     * the destination index using the BFS algorithm idea with some changes.
     ************************************************************************************/
    public static  Set<List<Index>> allShortestPath(int[][] twoDArray, Index sourceIndex, Index destIndex)
    throws IllegalArgumentException, IndexOutOfBoundsException{
        // Function to check if the input of the array is valid
        if(twoDArray.length > 50){
            throw new IllegalArgumentException("Incorrect input, size of array over 50!");
        }
        // Auxiliary functions to check validation of indices
        isValidIndex(twoDArray, sourceIndex);
        isValidIndex(twoDArray, destIndex);
        Matrix matrix = new Matrix(twoDArray);
        Queue<List<Index>> queue = new LinkedList<>();
        Set<List<Index>> shortestPath = new HashSet<>();
        Index currentIndex;
        List<Index> visited = new ArrayList<>();
        List<Index> pathToNode = new ArrayList<>();
        // first check if the indices are in the same connected component
        // Using auxiliary function: inTheSameComponent.
        if(inTheSameComponent(twoDArray, sourceIndex, destIndex)){
            visited.add(sourceIndex);
            pathToNode.add(sourceIndex);
            queue.add(pathToNode);
        }
        while(!queue.isEmpty()) {
            pathToNode = queue.poll();
            // remove all irrelevant lists that can't be one of the shortest path
            if (!shortestPath.isEmpty()) {
                while (!pathToNode.isEmpty() &&
                        (pathToNode.size() > shortestPath.stream().mapToInt(List::size).min().getAsInt())) {
                    if (!queue.isEmpty())
                        pathToNode = queue.poll();
                    else
                        return shortestPath;
                }
            }
            currentIndex = pathToNode.get(pathToNode.size() - 1);

            // found path to destination index
            if (currentIndex.equals(destIndex)) {
                // in case that the path is the first one or in the same size - add the new path to the current list
                if (shortestPath.isEmpty() ||
                        shortestPath.stream().mapToInt(List::size).min().getAsInt() == pathToNode.size()) {
                    shortestPath.add(pathToNode);
                }
                // in case that the path is shortest - clear all the list and add the new path to empty list
                else if (shortestPath.stream().mapToInt(List::size).min().getAsInt() > pathToNode.size()) {
                    shortestPath.clear();
                    shortestPath.add(pathToNode);
                }
            }
            for (Index index : matrix.getReachables(currentIndex)) {
                // notRelevant - all visited indices minus the last visited
                // notRelevant = visited.subList(0 ,  visited.size() - queue.size() );
                if (!visited.subList(0 , visited.size() - queue.size()).contains(index) ||
                        index.equals(destIndex)) {
                    visited.add(index);
                    List<Index> pathToNextNode = new ArrayList<>(pathToNode);
                    pathToNextNode.add(index);
                    queue.add(pathToNextNode);
                }
            }
        }
        return shortestPath;
    }

    /***********************************************************************************
     * Task 3: Submarine algorithm
     * @param twoDArray 2 dimension array
     * This function uses the first algorithm (task 1) in order to receive all connected component in the given matrix
     * for each component call to auxiliary function (isValidSubmarine) and increase the results if it's
     * a valid submarine (square or rectangle)
     ************************************************************************************/

    public static int countValidSubmarines(int[][] twoDArray) throws IllegalArgumentException {
        List<HashSet<Index>> connectedComponents = findSetsOfOnes(twoDArray);

        int result = 0;
        // Validate the connected components
        // Go over all the components and checks if its a valid submarine
        // If the connected component is a square or a rectangle add 1 to result
        // Using auxiliary function: isValidSubmarine
        for(HashSet<Index> component : connectedComponents) {
                result += isValidSubmarine(component);
        }
        return result;
    }

    /***********************************************************************************
     * Auxiliary function for the submarine algorithm (task 3)
     * @param connectedComponent HashSet of Index objects (connected component)
     * This function receives a HashSet of indices (connected component) and checks if is contains
     * at least two indices and its a rectangle or a square
     ************************************************************************************/
    private static int isValidSubmarine(@NotNull HashSet<Index> connectedComponent) {
        // the component should contain at least two elements (indices)
        if (connectedComponent.size() < 2) {
            return 0;
        }
        // find the edges of the component to check if it is a rectangle
        // Credit to stackoverflow
        int rightEdge = Collections.max(connectedComponent, Comparator.comparingInt(Index::getColumn)).getColumn();
        int leftEdge = Collections.min(connectedComponent, Comparator.comparingInt(Index::getColumn)).getColumn();
        int bottomEdge = Collections.max(connectedComponent, Comparator.comparingInt(Index::getRow)).getRow();
        int topEdge = Collections.min(connectedComponent, Comparator.comparingInt(Index::getRow)).getRow();

        // calculate the number of expected element of the rectangle
        int numOfExpectedElements = (rightEdge - leftEdge + 1) * (bottomEdge - topEdge + 1);

        // If the expected number of element equals to number of the element in the component (size)
        // so its a valid submarine
        if (connectedComponent.size() == numOfExpectedElements) {
            return 1;
        }
        return 0;
    }

    /***********************************************************************************
     * Task 4: Finding all  paths
     *  allPaths
     * @param twoDArray 2 dimension array
     * @param sourceIndex source index
     * @param destIndex destination index
     * The function finds and returns all the paths from the given source index to
     * the destination index using the BFS algorithm idea with some changes.
     * the same idea like in task 2, with no length limitation.
     ************************************************************************************/
    public static  List<List<Index>> allPaths(int[][] twoDArray, Index sourceIndex, Index destIndex){
        // Auxiliary functions to check validation of indices
        isValidIndex(twoDArray, sourceIndex);
        isValidIndex(twoDArray, destIndex);
        Matrix matrix = new Matrix(twoDArray);
        Queue<List<Index>> queue = new LinkedList<>();
        List<List<Index>> allPaths = new ArrayList<>();
        Index currentIndex;
        List<Index> visited = new ArrayList<>();

        List<Index> pathToNode = new ArrayList<>();
        // first check if the indices are in the same connected component
        if(inTheSameComponent(twoDArray, sourceIndex, destIndex)){
            visited.add(sourceIndex);
            pathToNode.add(sourceIndex);
            queue.add(pathToNode);
        }

        while(!queue.isEmpty()) {
            pathToNode = queue.poll();
            currentIndex = pathToNode.get(pathToNode.size() - 1);
            // found new path - add to list of all paths
            if (currentIndex.equals(destIndex)) {
                allPaths.add(pathToNode);
            }
            else {
                for (Index index : matrix.getReachables(currentIndex)) {
                    // notRelevant -> all visited indices minus the last visited
                    if (!visited.subList(0, visited.size() - queue.size()).contains(index) || index.equals(destIndex) || !pathToNode.contains(index)  ) {
                        visited.add(index);
                        List<Index> pathToNextNode = new ArrayList<>(pathToNode);
                        pathToNextNode.add(index);
                        queue.add(pathToNextNode);
                    }
                }
            }
        }
        return allPaths.stream().sorted(Comparator.comparing(List::size))
                .collect(Collectors.toList());
    }
    /***********************************************************************************
     * Auxiliary function to validate that index is inside the matrix
     * @param twoDArray 2 dimension array
     * @param index source index
     * This function checks if the index is a valid index inside the matrix, if not than
     * IndexOutOfBoundsException is being thrown
     ************************************************************************************/
    public static void isValidIndex(int[][] twoDArray, Index index){
        if(index.row < 0 || index.column < 0 ||
                index.row >= twoDArray.length || index.column >= twoDArray[0].length)
            throw new IndexOutOfBoundsException("Your index is invalid. Please try again!");
    }
}
