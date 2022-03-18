package com.project.matrix;

public class MatrixUtil {
    public int[][] add(int[][] A, int[][] B) {
        int[][] result = new int[A.length][A[0].length];
        for (int x = 0; x < A.length; x++) {
            for (int y = 0; y < A[0].length; y++) {
                result[x][y] = A[x][y] + B[x][y];
            }
        }
        return result;
    }

    public static int[][] multiplyingMatrixByNumber(int[][] m, int k) {
        int[][] result = new int[m.length][m[0].length];
        for (int x = 0; x < m[0].length; x++) {
            for (int y = 0; y < m.length; y++) {
                result[x][y] = m[x][y] * k;
            }
        }
        return result;
    }

    public static int[][] multiplication(int[][] firstMatrix, int[][] secondMatrix) {
        int[][] result = new int[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }
        return result;
    }

    static int multiplyMatricesCell(int[][] firstMatrix, int[][] secondMatrix, int row, int col) {
        int cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }
}