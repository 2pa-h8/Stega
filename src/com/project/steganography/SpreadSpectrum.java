package com.project.steganography;

import com.project.image.Picture;
import com.project.image.Watermark;
import com.project.util.MatrixUtil;
import com.project.util.Polynomial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class SpreadSpectrum {
    static Logger log = LoggerFactory.getLogger(SpreadSpectrum.class);

    public static void main(String[] args) throws IOException {
        final String SAVED_LOCATION = "C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\";

        Picture originalImage = new Picture("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\picture_compress_128x128.bmp");
        Watermark watermark = new Watermark("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\qr.bmp");;

        // размеры оригинального изображения
        final int X_ORIGINAL = originalImage.getWidth();
        final int Y_ORIGINAL = originalImage.getHeight();

        log.info("Ширина оригинального изображения X_ORIGINAL : {}", X_ORIGINAL);
        log.info("Высота оригинального изображения Y_ORIGINAL : {}", Y_ORIGINAL);

        // размеры ЦВЗ
        final int X_WATERMARK = watermark.getWidth(); // 32
        final int Y_WATERMARK = watermark.getHeight(); // 32

        log.info("Ширина оригинального изображения X_ORIGINAL : {}", X_WATERMARK);
        log.info("Высота оригинального изображения Y_ORIGINAL : {}", Y_WATERMARK);

        // формирование массива ортогональных функций
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

        // Общее число базисных функций
        int Nfi = X_WATERMARK * Y_WATERMARK;

        log.info("Общее число базисных функций Nfi : {}", Nfi);

        // размерность значащего подмасива отдельной базисной функции
        int n = (int) Math.floor(Math.sqrt((X_ORIGINAL * Y_ORIGINAL)/Nfi));

        log.info("Размерность значащего подмасива отдельной базисной функции n : {}", n);

        // степень полинома
        d = log(Nfi, 2);

        log.info("Степень полинома d : {}", d);

        // выбор полинома в зависимости от d
        int[] polynomial = Polynomial.getPoly(d);

        log.info("Полином : {}", Arrays.toString(polynomial));

        int s = 55; // Начальное состаяние регистра s задается в виде произвольного числа

        log.info("Начальное состояние регистра s : {}", s);

        int[] psp = pspGenerator(s, d, polynomial); // Псевдослучайная последовательность

        log.info("Псевдослучайная последовательность (ПСП) psp : " + Arrays.toString(psp));

        log.info("Размер ПСП : {}", psp.length);

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

        //Arrays.stream(matricesF.get(55)).map(Arrays::toString).forEach(System.out::println);

        // --------------------------------------------- 6 ---------------------------------------------
        // Расчет степени ортогональности сигнала контейнера к базисным функциям

        int[][] redComponents = originalImage.getColorComponent(Picture.RED_CHANEL);

        log.info("Components arr : {}", redComponents);

        // Arrays.stream(redComponents).map(Arrays::toString).forEach(System.out::println);

        double deltaMax = calculateOrthogonality(matricesF, redComponents, Nfi);

        // Arrays.stream(redComponents).map(Arrays::toString).forEach(System.out::println);

        log.info("Максимальное значение погрешности delta : {}", deltaMax);

        // --------------------------------------------- 7 ---------------------------------------------
        // Преобразование ЦВЗ в одномерный массив значений (255 и 0)
        // значения пикселей ЦВЗ равняются 0 и 255, так как изображение черно-белое

        int[] arrPixelsWatermark = watermark.getBlackWhitePixelsArr();
//        int[] arrPixelsWatermark = watermark.testFillContainer();

        //System.out.println(Arrays.toString(arrPixelsWatermark));

        // --------------------------------------------- 8 ---------------------------------------------
        // М модуляция полученного массива базисными функциями

        int[][] E = modulation(X_ORIGINAL, Y_ORIGINAL, arrPixelsWatermark, matricesF);

        //Arrays.stream(E).map(Arrays::toString).forEach(System.out::println);

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

        int[][] S = MatrixUtil.add(cNorm, MatrixUtil.multiplyingMatrixByNumber(E, Kg));

//       Arrays.stream(S).map(Arrays::toString).forEach(System.out::println);

        // --------------------------------------------- 12 ---------------------------------------------
        // Извлечение

        int[] mResult = extraction(
                X_ORIGINAL,
                Y_ORIGINAL,
                Nfi,
                S,
                matricesF);

        watermark.recover(mResult);
        watermark.saveImage(SAVED_LOCATION);
        watermark.showImage();
    }

    /*
    Шаг № 11
    извлечение ЦВЗ из контейнера
    */

    static int[] extraction(int X, int Y, int Nfi, int[][] S, List<int[][]> matricesF) {
        int[] result = new int[Nfi];
        Random rnd = new Random();
        int m = 0;

        for (int i = 0; i < Nfi; i++) {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    int[][] element = matricesF.get(i);
                    m += S[x][y] * element[x][y];
                }
            }

            if (m > 0) {
                result[i] = 1;
            } else if (m < 0) {
                result[i] = 0;
            } else {
                result[i] = Math.round(rnd.nextFloat());
            }

            m = 0;
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

    static int[][] modulation(int X, int Y, int[] M, List<int[][]> matricesF) {
        int mLength = M.length;
        int[][] result = new int[X][Y];
        ArrayList<Integer> mVecBin = new ArrayList<>();

        // замена 0 на -1 в массиве значений ЦВЗ
        for (int j : M) {
            if (j == 0) {
                mVecBin.add(-1);
            } else {
                mVecBin.add(1);
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

        // вычисление максимального значения дельта
        deltaMax = deltas.stream().max(Comparator.naturalOrder()).orElseThrow();

        return deltaMax;
    }

    /*
    Шаг №5
    Формирование массива базисных функций
    */

    static List<int[][]> formationOfArrayOfBasisFunctions(int[][] basicFunc, int[] psp, int n, int X, int Y, int Nfi) {
        List<int[][]> matrices = new ArrayList<>();

        // подготовка контейнера - заполнение
        for (int i = 0; i < Nfi; i++) {
            matrices.add(null);
        }

        // Формирование матрицу на основе матрицы baseFunc и заполнение 0
        int[][] zeroMatrix = new int[X][Y];

        for (int[] row : zeroMatrix) {
            Arrays.fill(row, 0);
        }

        /* Заполнение массива послдедовательностью 0..X
           Этот списко нужен для того, чтобы определить
           строки, которые необходимо вычеркнуть */

        ArrayList<Integer> removedList = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < 128; i++) {
            removedList.add(k);
            k++;
        }

        int c1 = 0;
        int c2 = n - 1;

        for (int i = 1; i <= Nfi; i++) {
            // создание копии нулейвой матрицы
            int[][] tempMatrix = MatrixUtil.copyMatrix(zeroMatrix);
            int indexPsp = psp[i-1] - 1;

            // рассчет значений строк, который небходимо вычеркнуть
            int r1 = (n * (i - 1) + 1) % X - 1;
            int r2 = r1 + n - 1;

            /* Метод slice работает с массивами, поэтому переводим дапазон в массив.
               Метод принимает в качесве параметров массивы строк и столбцов,
               которые нужно вычеркнуть. */

            // Формирование массива строк и стобцов, которые нужно вычеркнуть
            int[] rangeColumns = MatrixUtil.removeRangeValues(removedList, c1, c2);
            int[] rangeRows = MatrixUtil.removeRangeValues(removedList, r1, r2);

            // вырезание матрицы
            int[][] submatrix = MatrixUtil.slice(basicFunc, rangeColumns, rangeRows);
            // встраивание матрицы
            int[][] result = MatrixUtil.putregion(tempMatrix, submatrix, r1, c1);

            /* установка матрицы в результирующий контейнер,
               в качестве индекса контейнера выступает значение
               псп */
            matrices.set(indexPsp, result);

            if (r2 == X - 1) {
                c1 += n;
                c2 += n;
            }
        }

        return matrices;
    }

    // Логарифмирование по основанию
    static int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    // Перевод из 10-й в 2-ю
    static int[] d2b(int x, int length) { // здесь нужно добавить корретную проверку на знак
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.abs(x) % 2;
            x = (int) Math.floor(Math.abs(x)/2);
        }
        return result;
    }

    static int b2d(int[] x, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result += x[i] * Math.pow(2, i);
        }
        return result;
    }

    static int[] pspGenerator(int s, int d, int[] poly) { // Генератор псевдослучайно последовательности (ПСП)
        int period = (int) Math.pow(2,d) - 1; // период повторений ПСП
        int length = (int) Math.pow(2,d);

        int polyLength = poly.length;

        int[] randomSeq = new int[length];
        int[] rBin = new int[polyLength];

        for (int i = 0; i < period; i++) {
            if (i == 0) {
                randomSeq[i] = s;
                rBin = d2b(randomSeq[i], polyLength);
            } else {
                int[] tempArr = new int[polyLength];
                int bit = 0;

                for (int j = 0; j < polyLength; j++) {
                    if (poly[j] == 1) {
                        bit = rBin[j] ^ bit;
                    }
                }

                System.arraycopy(rBin, 0, tempArr, 0, polyLength);

                for (int j = 0; j < polyLength; j++) {
                    if (j > 0) {
                        rBin[j] = tempArr[j-1];
                    } else {
                        rBin[j] = bit;
                    }
                }

                randomSeq[i] = b2d(rBin, polyLength);
            }
        }

        int d0 =  (int) Math.pow(2,d) - 1;
        randomSeq[d0] = (int) Math.pow(2,d);

        return randomSeq;
    }
}