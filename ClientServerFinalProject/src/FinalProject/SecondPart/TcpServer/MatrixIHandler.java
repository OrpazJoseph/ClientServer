package FinalProject.SecondPart.TcpServer;

import FinalProject.SecondPart.Algorithms.Algorithms;
import com.sun.tools.attach.AgentInitializationException;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.*;

public class MatrixIHandler implements IHandler {

    private Matrix matrix;
    private Index start, end;
    private int[][] twoDArray;


    public MatrixIHandler() {
        this.resetParams();
    }
    private void resetParams(){
        this.matrix = null;
        this.start = null;
        this.end = null;
    }

    @Override
    public void handle(InputStream inClient, OutputStream outClient) throws Exception {
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(outClient);
        ObjectInputStream objectInputStream = new ObjectInputStream(inClient);

        this.resetParams();

        boolean dowork = true;
        while (dowork) {
            switch (objectInputStream.readObject().toString()) {
                default :
                    System.out.println("This is not a valid Menu Option! Please Select Another");
                    break;
                case "matrix": {
                    var primitiveMatrix = objectInputStream.readObject();
                    if(primitiveMatrix.getClass() == Integer[][].class){
                        this.matrix = new Matrix(Matrix.convertToInt((Integer[][]) primitiveMatrix));
                    }
                    else if(primitiveMatrix.getClass() != int[][].class){
                        throw new ClassCastException("Invalid two dimension array input!");
                    }
                    else
                        this.matrix = new Matrix((int[][]) primitiveMatrix);
                    break;
                }
                case "start index": {
                    var object = objectInputStream.readObject();
                    if(object.getClass() != Index.class){
                        throw new ClassCastException("Invalid start Index input!");
                    }
                    this.start = (Index) object;
                    break;
                }
                case "end index": {
                    var object = objectInputStream.readObject();
                    if(object.getClass() != Index.class){
                        throw new ClassCastException("Invalid end Index input!");
                    }
                    this.end = (Index) object;
                    break;
                }
                case "AdjacentIndices": {
                    Index indexAdjacentIndices = (Index) objectInputStream.readObject();
                    Collection<Index> adjacentIndices = new ArrayList<>();
                    if (this.matrix != null){
                        adjacentIndices.addAll(this.matrix.getAdjacentIndices(indexAdjacentIndices));
                    }
                    objectOutputStream.writeObject(adjacentIndices);
                    break;
                }
                case "Reachables": {
                    Index start = (Index) objectInputStream.readObject();
                    Collection<Index> reachables = new ArrayList<>();
                    if (this.matrix != null){
                        reachables.addAll(this.matrix.getReachables(start));
                    }
                    objectOutputStream.writeObject(reachables);
                    break;
                }
                case "findSetsOfOnes": {
                    List<HashSet<Index>> listOfSetsOfOnes = new ArrayList<>();
                    if (this.matrix != null){
                        listOfSetsOfOnes.addAll(Algorithms.findSetsOfOnes(this.matrix.getPrimitiveMatrix()));
                    }
                    objectOutputStream.writeObject(listOfSetsOfOnes);
                    break;
                }

                case "allShortestPath": {
                    Set<List> listOfShortestPaths = new HashSet<>();
                    if (this.matrix != null){
                        listOfShortestPaths.addAll(Algorithms.allShortestPath(this.matrix.getPrimitiveMatrix(),start, end));
                    }
                    objectOutputStream.writeObject(listOfShortestPaths);
                    break;
                }
                case "submarinesGame": {
                    int result = (Algorithms.countValidSubmarines(this.matrix.getPrimitiveMatrix()));
                    objectOutputStream.writeObject(result);
                    break;
                }
                case "findAllPaths": {
                    List<List> listOfAllPaths = new ArrayList<>();
                    if (this.matrix != null) {
                        listOfAllPaths.addAll(Algorithms.allPaths(this.matrix.getPrimitiveMatrix(), start, end));
                    }
                    objectOutputStream.writeObject(listOfAllPaths);
                    break;
                }
                case "stop":{
                    dowork= false;
                    break;
                }
            }
        }
    }
}