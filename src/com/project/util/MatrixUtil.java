package com.project.util;

import java.util.ArrayList;

public class MatrixUtil {
    public static int[][] add(int[][] A, int[][] B) {
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


    public static int[] matrixToArray(int[][] matrix) {
        int row = matrix.length;
        int col = matrix[0].length;
        int k = 0;
        int arrLength =  row * col;
        int[] result = new int[arrLength];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                result[k] = matrix[i][j];
                k++;
            }
        }

        return result;
    }

    public static int[][] putregion(int[][] m1, int[][] m2, int row, int col) { // встраивание одной матрицы в другую
        int m2Rows = m2.length;
        int m2Columns = m2[0].length;
        int count = 0;

        int[] m2Arr = MatrixUtil.matrixToArray(m2); // преобразование встраеваемой матрицы в массив

        for (int i = row; i < row + m2Rows; i++) {
            for (int j = col; j < col + m2Columns; j++) {
                m1[i][j] = m2Arr[count];
                if (count < m2Arr.length - 1) {
                    count++;
                }
            }
        }

        return m1;
    }

    // вырезание участка матрицы
    public static int [][] slice(int [][] matr, int [] rows, int [] cols) {
        int iCol = (matr[0]).length;
        int iRow = matr.length;

        int c = cols.length;
        int r = rows.length;

        int oCol = iCol-c;
        int oRow = iRow-r;

        int [][] res = new int [oRow] [oCol];

        int i,j,k,ii,jj,flgc,flgr;

        ii=0;

        for (i=0; i<iRow; i++)
        {
            flgr=0;
            for (k=0; k<r; k++)
            {
                if (i==rows[k])
                {
                    flgr=1;
                    break;
                }
            }
            if (flgr == 0)
            {
                jj=0;
                for (j=0; j<iCol; j++)
                {
                    flgc=0;

                    for (k=0; k<c; k++)
                    {
                        if (cols[k]==j)
                        {
                            flgc=1;
                            break;
                        }
                    }
                    if (flgc==0)
                    {
                        res[ii][jj]=matr[i][j];
                        jj++;
                    }
                }
                ii++;
            }
        }
        return res;
    }

    /*
Копирование матрицы. Нужно для корректного формирования списка матриц,
в который встроены (-1, 1) блоки.
*/
    public static int[][] copyMatrix(int[][] matrix) {
        int [][] myInt = new int[matrix.length][];

        for(int i = 0; i < matrix.length; i++) {
            int[] aMatrix = matrix[i];
            int   aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }

        return myInt;
    }

    public static int[] removeRangeValues(ArrayList<Integer> list, int a, int b) {
        ArrayList<Integer> temp =  new ArrayList<>(list);

        if (b >= a) {
            temp.subList(a, b + 1).clear();
        }

        return temp.stream().mapToInt(Integer::intValue).toArray();
    }
}