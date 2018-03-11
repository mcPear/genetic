import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by maciej on 01.03.18.
 */
public class QapCase {
    private int n;
    private RealMatrix flowMatrix;
    private RealMatrix distanceMatrix;
    private static final int INDEX_TRANSLATION = 1;

    public void loadFromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        n = findN(scanner);
        double[][] flowArray2D = readArray2D(scanner);
        double[][] distanceArray2D = readArray2D(scanner);
        flowMatrix = MatrixUtils.createRealMatrix(flowArray2D);
        distanceMatrix = MatrixUtils.createRealMatrix(distanceArray2D);
    }

    private int findN(Scanner scanner) {
        return scanner.nextInt();
    }

    private double[][] readArray2D(Scanner scanner) {
        List<ArrayList<Integer>> list2D = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Scanner colReader;
            do {
                colReader = new Scanner(scanner.nextLine());
            }
            while (!colReader.hasNextInt());
            ArrayList column = new ArrayList();
            for (int j = 0; j < n; j++) {
                column.add(colReader.nextInt());
            }
            list2D.add(column);
        }

        double[][] array2D = new double[list2D.size()][];

        for (int i = 0; i < list2D.size(); i++) {
            array2D[i] = new double[list2D.size()];
            for (int j = 0; j < list2D.size(); j++) {
                array2D[i][j] = list2D.get(i).get(j);
            }
        }

        return array2D;
    }

    public int getN() {
        return n;
    }

    public int evaluate(Genome genome) {
        RealMatrix rearrangedDistanceMatrix = applyGenomePermutationToMatrix(genome.getVector(), distanceMatrix);
//        RealMatrix costsMatrix = flowMatrix.multiply(rearrangedDistanceMatrix);
        RealMatrix costsMatrix = hadamardMult(flowMatrix, rearrangedDistanceMatrix, n);
        //System.out.println("costsMatrix:\n\n" + matrixToString(costsMatrix) + "\n\n");
        RealMatrixPreservingVisitor sumVisitor = new RealMatrixSumVisitor();
        return (int) costsMatrix.walkInOptimizedOrder(sumVisitor);
    }

    private RealMatrix hadamardMult(RealMatrix matrix1, RealMatrix matrix2, int n) {
        RealMatrix result = MatrixUtils.createRealMatrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result.setEntry(i, j, matrix1.getEntry(i, j) * matrix2.getEntry(i, j));
            }
        }
        return result;
    }

    private RealMatrix applyGenomePermutationToMatrix(List<Integer> permutation, RealMatrix distanceMatrix) {
        int n = permutation.size();
        RealMatrix rearrangedMatrixByColumn = MatrixUtils.createRealMatrix(n, n);
        for (int i = 0; i < n; i++) {
            //System.out.println(i);
            rearrangedMatrixByColumn.setColumn(i, distanceMatrix.getColumn(permutation.get(i) - INDEX_TRANSLATION));
        }
        RealMatrix rearrangedMatrixByRowAndColumn = rearrangedMatrixByColumn.copy();
        for (int i = 0; i < n; i++) {
            rearrangedMatrixByRowAndColumn.setRow(i, rearrangedMatrixByColumn.getRow(permutation.get(i) - INDEX_TRANSLATION));
        }
//        System.out.println("++++++++++++++++++++");
//        System.out.println(matrixToString(distanceMatrix) + "\n\n");
//        System.out.println(matrixToString(rearrangedMatrixByRowAndColumn) + "\n\n");
//        permutation.forEach(n -> System.out.print(n + " "));
//        System.out.println("++++++++++++++++++++");
        return rearrangedMatrixByRowAndColumn;
    }

    public Integer evaluateSubVector(List<Integer> subVector) {
        List<Integer> fakePermutation = getFakePermutation(subVector);
        RealMatrix rearrangedDistanceMatrix = applyGenomePermutationToMatrix(fakePermutation, distanceMatrix);
        RealMatrix rearrangedSubDistanceMatrix = getSubMatrix(subVector, rearrangedDistanceMatrix);
        RealMatrix subFlowMatrix = getSubMatrix(subVector, flowMatrix);
        RealMatrix costsMatrix = hadamardMult(subFlowMatrix, rearrangedSubDistanceMatrix, subVector.size());
        RealMatrixPreservingVisitor sumVisitor = new RealMatrixSumVisitor();
        return (int) costsMatrix.walkInOptimizedOrder(sumVisitor);
    }

    private RealMatrix getSubMatrix(List<Integer> subVector, RealMatrix matrix) {
        int subN = subVector.size();
        RealMatrix subMatrixByColumn = MatrixUtils.createRealMatrix(n, subN);
        List<RealMatrix> subColumns = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (subVector.contains(i + INDEX_TRANSLATION)) {
                subColumns.add(matrix.getColumnMatrix(i));
            }
        }
        for (int i = 0; i < subN; i++) {
            subMatrixByColumn.setColumnMatrix(i, subColumns.get(i));
        }

        RealMatrix subMatrix = MatrixUtils.createRealMatrix(subN, subN);
        List<RealMatrix> subRows = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (subVector.contains(i + INDEX_TRANSLATION)) {
                subRows.add(subMatrixByColumn.getRowMatrix(i));
            }
        }
        for (int i = 0; i < subN; i++) {
            subMatrix.setRowMatrix(i, subRows.get(i));
        }

        return subMatrix;
    }

    private List<Integer> getFakePermutation(List<Integer> subVector) {
        List<Integer> result = new ArrayList<>(subVector);
        while (result.size() < n) {
            result.add(1);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("QuapCase: \n\nn = " + n + "\n\n Flow matrix: \n");
        result.append(matrixToString(flowMatrix));
        result.append("\n\nDistance matrix: \n");
        result.append(matrixToString(distanceMatrix));
        return result.toString();
    }

    private String matrixToString(RealMatrix matrix) {
        double[][] array2D = matrix.getData();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array2D.length; i++) {
            result.append("\n");
            for (int j = 0; j < array2D.length; j++) {
                result.append((int) array2D[i][j] + " ");
            }
        }
        return result.toString();
    }

    private class RealMatrixSumVisitor implements RealMatrixPreservingVisitor {
        private int sum = 0;

        @Override
        public void start(int i, int i1, int i2, int i3, int i4, int i5) {

        }

        @Override
        public void visit(int i, int i1, double v) {
            sum += v;
        }

        @Override
        public double end() {
            return sum;
        }
    }

}
