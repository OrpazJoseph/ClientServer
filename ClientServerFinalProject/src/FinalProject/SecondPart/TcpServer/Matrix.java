package FinalProject.SecondPart.TcpServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Matrix implements Serializable {
    private int[][] primitiveMatrix;

    public static int[][] convertToInt(Integer[][] twoDIntegerArray){
        int[][] twoDArray = new int[twoDIntegerArray[0].length][twoDIntegerArray.length];

        for(int i = 0; i < twoDIntegerArray.length; i++){
            twoDArray[i] = Arrays.stream(twoDIntegerArray[i])
                    .mapToInt(Integer::intValue)
                    .toArray();
        }
        return twoDArray;
    }
    public Matrix(int[][] oArray){
        primitiveMatrix = Arrays
                .stream(oArray)
                .map(row -> row.clone())
                .toArray(value -> new int[value][]);
    }

    public void printMatrix(){
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public Collection<Index> getAdjacentIndices(final Index index){
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{
            extracted = primitiveMatrix[index.row+1][index.column];
            list.add(new Index(index.row+1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column+1];
            list.add(new Index(index.row,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column];
            list.add(new Index(index.row-1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column-1];
            list.add(new Index(index.row,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column-1];
            list.add(new Index(index.row-1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row+1][index.column+1];
            list.add(new Index(index.row+1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row+1][index.column-1];
            list.add(new Index(index.row+1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column+1];
            list.add(new Index(index.row-1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }

    public int getValue(Index index) {
        return primitiveMatrix[index.row][index.column];
    }

    public Collection<Index> getReachables(Index index) {
        ArrayList<Index> filteredIndices = new ArrayList<>();
        this.getAdjacentIndices(index).stream().filter(i-> getValue(i)==1)
                .map(neighbor->filteredIndices.add(neighbor)).collect(Collectors.toList());
        return filteredIndices;
    }
    public List<Index> twoDArrayToList(int[][] matrix) {
        List<Index> list = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
                list.add(new Index(i, j));
        }
        return list;
    }
    public ArrayList<Index> getOnes(int[][] twoDArray) {
        ArrayList<Index> list = new ArrayList<>();
        this.twoDArrayToList(twoDArray).stream().filter(i -> getValue(i) == 1)
                .map(list::add).collect(Collectors.toList());
        return list;
    }
}