package com.project;

import com.project.image.Picture;
import com.project.image.Watermark;
import com.project.matrix.MatrixUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Main {
    static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

//        Picture originalImage = new Picture("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\picture_compress_128x128.bmp");
//        // ! ВАЖНО цвз должна иметь в значениях пикселей только 2 числа - 255 и 0
//
//        Watermark watermark = new Watermark("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\watermark_32x32.bmp");

        final String SAVED_LOCATION = "C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\";

        MatrixUtil matrixUtil = new MatrixUtil();

        Picture originalImage = new Picture("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\picture_compress_128x128.bmp");
        Watermark watermark = new Watermark("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\watermark_32x32.bmp");
        // Watermark watermark = new Watermark("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\test.bmp");

        final int X_ORIGINAL = originalImage.getWidth(); // 128
        final int Y_ORIGINAL = originalImage.getHeight(); // 128

        final int X_WATERMARK = watermark.getWidth(); // 32
        final int Y_WATERMARK = watermark.getHeight(); // 32

//        watermark.saveImage(SAVED_LOCATION);
//        watermark.showImage();

        log.info("Ширина оригинального изображения X_ORIGINAL : {}", X_ORIGINAL);
        log.info("Высота оригинального изображения Y_ORIGINAL : {}", Y_ORIGINAL);

        int[][] basicFunc = new int[X_ORIGINAL][Y_ORIGINAL];
        int d = -1;

        for (int i = 0; i < X_ORIGINAL; i++) {
            d = -d;
            for (int j = 0; j < Y_ORIGINAL; j++) {
                basicFunc[i][j] = d;
                d = -d;
            }
        }

        //Arrays.stream(basicFunc).map(Arrays::toString).forEach(System.out::println);

        int Nfi = X_WATERMARK * Y_WATERMARK; // 1024
        log.info("Общее число базисных функций Nfi : {}", Nfi);

        int n = (int) Math.floor(Math.sqrt((X_ORIGINAL * Y_ORIGINAL)/Nfi));

        log.info("Размерность значащего подмасива отдельной базисной функции n : {}", n);

        int[] polynomial = {1, 1, 1, 1, 1, 1, 1, 0, 0, 1};

        log.info("Полином : {}", Arrays.toString(polynomial));

        d = log(Nfi, 2);

        log.info("Степень полинома d : {}", d);

        int s = 55; // Начальное состаяние регистра s задается в виде произвольного числа

        log.info("Начальное состояние регистра s : {}", s);

        int[] psp = pspGenerator(s, d, polynomial); // Псевдослучайная последовательность

        log.info("Псевдослучайная последовательность (ПСП) psp : " + Arrays.toString(psp));

        log.info("Размер ПСП : {}", psp.length);

//        Arrays.stream(result).map(Arrays::toString).forEach(System.out::println);
        // --------------------------------------------- 5 ---------------------------------------------
        // Встраивание

        List<int[][]> matricesF = formationOfArrayOfBasisFunctions(
                basicFunc,  // массив базисных функций
                psp,        // Псевдослучайная последовательность
                n,          // размерность значащего подмасива отдельной базисной функции
                X_ORIGINAL, // ширина оригинального изображения
                Y_ORIGINAL, // высота оригинального изображения
                Nfi);       // общее число базисных функций

        // Вывод одного из элементов матрицы

        //Arrays.stream(matricesF.get(555)).map(Arrays::toString).forEach(System.out::println);

        // --------------------------------------------- 6 ---------------------------------------------
        // Расчет степени ортогональности сигнала контейнера к базисным функциям

        int[][] redComponents = originalImage.getColorComponent(Picture.RED_CHANEL);

        // Arrays.stream(redComponents).map(Arrays::toString).forEach(System.out::println);

        double deltaMax = calculateOrthogonality(matricesF, redComponents, Nfi);

        // Arrays.stream(redComponents).map(Arrays::toString).forEach(System.out::println);

        log.info("Максимальное значение погрешности delta : {}", deltaMax);

        // --------------------------------------------- 7 ---------------------------------------------
        // Преобразование ЦВЗ в одномерный массив значений (255 и 0)
        // значения пикселей ЦВЗ равняются 0 и 255, так как изображение черно-белое

        Integer[] arrPixelsWatermark = watermark.getBlackWhitePixelsArr();

        // --------------------------------------------- 8 ---------------------------------------------
        // М модуляция полученного массива базисными функциями

        int[][] E = modulation(X_ORIGINAL, Y_ORIGINAL, arrPixelsWatermark, matricesF);

        log.info("Размер модулированного сообщения E : {} x {}", E.length, E.length);

        // --------------------------------------------- 9 ---------------------------------------------
        // Вычисление достаточного коэффициента усиления мощности

        int Kg = calculateKg(n, deltaMax);

        log.info("Достаточные коэффициент усиления по мощности Kg : {}", Kg);


        // --------------------------------------------- 10 ---------------------------------------------
        // Нормирование массива контейнера

        int[][] cNorm = containerNormalization(X_ORIGINAL, Y_ORIGINAL, Kg, redComponents);
        //Arrays.stream(cNorm).map(Arrays::toString).forEach(System.out::println);

        // сохранение изображения
//        originalImage.setColorComponent(cNorm, Picture.RED_CHANEL);
//        originalImage.saveImage(SAVED_LOCATION);

        // --------------------------------------------- 11 ---------------------------------------------
        // Формирование контейнера S = Cnorm + Kg * E

        //Arrays.stream(E).map(Arrays::toString).forEach(System.out::println);

        int[][] S = matrixUtil.add(matrixUtil.multiplyingMatrixByNumber(E, Kg), cNorm);


        // сохранение изображения
//        originalImage.setColorComponent(S, Picture.RED_CHANEL);
//        originalImage.saveImage(SAVED_LOCATION);

//       Arrays.stream(S).map(Arrays::toString).forEach(System.out::println);

        // --------------------------------------------- 12 ---------------------------------------------
        // Извлечение

        int[] mResult = extraction(
                X_ORIGINAL,
                Y_ORIGINAL,
                Nfi,
                S,
                matricesF);

//        System.out.println(Arrays.toString(mResult));
//        System.out.println(mResult.length);

        watermark.recover(mResult);
        watermark.saveImage(SAVED_LOCATION);
        watermark.showImage();
    }

    /*
    Шаг № 11
    извлечение цвз из контейнера
    */

    static int[] extraction(int X, int Y, int Nfi, int[][] S, List<int[][]> matricesF) {
        int[] result = new int[Nfi];
        Random rnd = new Random();
        int m = 0;

        for (int i = 0; i < Nfi; i++) {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    int[][] element = matricesF.get(i);
                    m = S[x][y] * element[x][y];
                }
            }

            if (m > 0) {
                result[i] = 1;
            } else if (m < 0) {
                result[i] = 0;
            } else if (m == 0) {
                result[i] = Math.round(rnd.nextFloat(1));
            }
        }

        return result;
    }

    /*
    Шаг № 10
    Нормирование массива контейнера
    */

    static int[][] containerNormalization(int X, int Y, int Kg, int[][] components) {
        int[][] cNorm = new int[X][Y];

        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                cNorm[x][y] = Math.round((float) components[x][y] / 255 * (255 - 2 * Kg) + Kg);
            }
        }

        return cNorm;
    }


    /*
    Шаг № 9
    Вычисление коэффициента усиления мощности
    */

    static int calculateKg(int n, double deltaMax) {
        int Kg = 1;
        int nf = n * n;

        while (Kg * nf < deltaMax) {
            Kg++;
        }

        return Kg;
    }

    /*
    Шаг № 8
    Модуляция полученного массива базисными функциями
    */

    static int[][] modulation(int X, int Y, Integer[] M, List<int[][]> matricesF) {
        int mLength = M.length;
        int[][] result = new int[X][Y];
        ArrayList<Integer> mVecBin = new ArrayList<>();

        for (int j : M) {
            if (j == 0) {
                mVecBin.add(-1);
            } else {
                mVecBin.add(0);
            }
        }

        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                int sum = 0;
                for (int i = 0; i < mLength; i++) {
                    int[][] element = matricesF.get(i);
                    sum += mVecBin.get(i) * element[x][y];
                }
                result[x][y] = sum;
            }
        }

        return result;
    }

    /*
    Шаг № 6
    Степень ортогональности
    */
    static double calculateOrthogonality(List<int[][]> matricesF, int[][] components, int Nfi) {
        ArrayList<Double> deltas = new ArrayList<>();
        double delta;
        double deltaMax;

        for (int i = 1; i < Nfi; i++) {
            for (int x = 0; x < components.length; x++) {
                for (int y = 0; y < components.length; y++) {
                    int[][] element = matricesF.get(i);
                    delta = element[x][y] * components[x][y];
                    deltas.add(delta);
                }
            }
        }

        deltaMax = deltas.stream().max(Comparator.naturalOrder()).orElseThrow();

        return deltaMax;
    }
    /*
    Шаг №5
    Формирование массива базисных функций
    */
    static List<int[][]> formationOfArrayOfBasisFunctions(int[][] basicFunc, int[] psp, int n, int X, int Y, int Nfi) {
        List<int[][]> matrices = new ArrayList<>();

        for (int i = 0; i < 1024; i++) {
            matrices.add(null);
        }

        // нужно сформировать матрицу на основе матрицы baseFunc и заполнить 0
        int[][] tesResult = new int[X][Y];

        for (int[] row : tesResult) { // заполнение матрицы 0
            Arrays.fill(row, 0);
        }

        // Заполнение массива послдедовательностью 0..127
        ArrayList<Integer> list_128 = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < 128; i++) {
            list_128.add(k);
            k++;
        }

        int c1 = 0;
        int c2 = n - 1;

        for (int i = 1; i <= Nfi; i++) {
            int[][] tempMatrix = copyMatrix(tesResult);
            int indexPsp = psp[i-1] - 1;

            int r1 = (n * (i - 1) + 1) % X - 1;
            int r2 = r1 + n - 1;

            /* Метод slice работает с массивами, поэтому переводим дапазон в массив.
               Метод принимает в качесве параметров массивы строк и столбцов,
               которые нужно вычеркнуть.
            */
            int[] rangeColumns = removeRangeValues(list_128, c1, c2);
            int[] rangeRows = removeRangeValues(list_128, r1, r2);

            // вырезание матрицы
            int[][] submatrix = slice(basicFunc, rangeColumns, rangeRows);
            // встраивание матрицы
            int[][] result = putregion(tempMatrix, submatrix, r1, c1);

            matrices.set(indexPsp, result);

            if (r2 == X - 1) {
                c1 += n;
                c2 += n;
            }
        }

        return matrices;
    }

    /*
    Копирование матрицы. Нужно для корректного формирования списка матриц,
    в который встроены (-1, 1) блоки.
    */
    static int[][] copyMatrix(int[][] matrix) {
        int [][] myInt = new int[matrix.length][];

        for(int i = 0; i < matrix.length; i++) {
            int[] aMatrix = matrix[i];
            int   aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }

        return myInt;
    }

    static int[] removeRangeValues(ArrayList<Integer> list, int a, int b) {
        ArrayList<Integer> temp =  new ArrayList<>(list);

        if (b >= a) {
            temp.subList(a, b + 1).clear();
        }

        int[] result = temp.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        return result;
    }

    public static int [][] slice(int [][] matr, int [] rows, int [] cols) {
        int iCol = (matr[0]).length;
        int iRow = matr.length;

        int c    = cols.length;
        int r    = rows.length;

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

    static int[][] putregion(int[][] m1, int[][] m2, int row, int col) { // встраивание одной матрицы в другую
        int m2Rows = m2.length;
        int m2Columns = m2[0].length;
        int count = 0;

        int[] m2Arr = matrixToArray(m2); // преобразование встраеваемой матрицы в массив

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

    static int[] matrixToArray(int[][] matrix) { // добавить проверку
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

    // Логарифмирование по основанию
    static int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    // Перевод из 10-й в 2-ю
    static int[] d2b(int x) { // здесь нужно добавить корретную проверку на знак
        int[] result = new int[10];
        int sign = x >= 0 ? 1 : -1;
        for (int i = 0; i < 10; i++) {
            result[i] = Math.abs(x) % 2;
            x = (int) Math.floor(Math.abs(x)/2);
        }
        return result;
    }

    static int b2d(int[] x) {
        int result = 0;
        for (int i = 0; i < 10; i++) {
            result += x[i] * Math.pow(2, i);
        }
        return result;
    }

    static int[] pspGenerator(int s, int d, int[] pol) { // Генератор псевдослучайно последовательности (ПСП)
        int period = (int) Math.pow(2,d) - 1; // период повторений ПСП
        int[] polynomial = pol;

        int[] randomSeq = new int[1024];
        int[] rBin = new int[10];

        for (int i = 0; i < period; i++) {
            if (i == 0) {
                randomSeq[i] = s;
                rBin = d2b(randomSeq[i]);
            } else {
                int[] tempArr = new int[10];
                int bit = 0;

                for (int j = 0; j < 10; j++) {
                    if (polynomial[j] == 1) {
                        bit = rBin[j] ^ bit;
                    }
                }

                System.arraycopy(rBin, 0, tempArr, 0, 10);

                for (int j = 0; j < 10; j++) {
                    if (j > 0) {
                        rBin[j] = tempArr[j-1];
                    } else {
                        rBin[j] = bit;
                    }
                }

                randomSeq[i] = b2d(rBin);
            }
        }

        int d0 =  (int) Math.pow(2,d) - 1;
        randomSeq[d0] = (int) Math.pow(2,d);

        return randomSeq;
    }
}