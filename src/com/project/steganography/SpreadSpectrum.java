package com.project.steganography;

import com.project.image.Picture;
import com.project.image.Watermark;
import com.project.util.MatrixUtil;
import com.project.util.Polynomial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SpreadSpectrum extends SteganographyAlgorithm {
    static Logger log = LoggerFactory.getLogger(SpreadSpectrum.class);

    @Override
    public Picture encode(Picture originalImage, Watermark watermark) throws Exception {
        log.info("---------------------------  КОДИРОВАНИЕ  ---------------------------");
        // размеры оригинального изображения
        int X_ORIGINAL = originalImage.getWidth();
        int Y_ORIGINAL = originalImage.getHeight();

        log.info("Ширина оригинального изображения X_ORIGINAL : {}", X_ORIGINAL);
        log.info("Высота оригинального изображения Y_ORIGINAL : {}", Y_ORIGINAL);

        // размеры ЦВЗ
        int X_WATERMARK = watermark.getWidth();
        int Y_WATERMARK = watermark.getHeight();

        log.info("Ширина ЦВЗ X_WATERMARK : {}", X_WATERMARK);
        log.info("Высота ЦВЗ Y_WATERMARK : {}", Y_WATERMARK);

        // Общее число базисных функций
        int Nfi = X_WATERMARK * Y_WATERMARK;

        log.info("Общее число базисных функций Nfi : {}", Nfi);

        int s = 55;

        // размерность значащего подмасива отдельной базисной функции
        int n = (int) Math.floor(Math.sqrt((X_ORIGINAL * Y_ORIGINAL)/Nfi));

        log.info("Размерность значащего подмасива отдельной базисной функции n : {}", n);

        List<int[][]> f = getArrayOfBasicFunctions(X_ORIGINAL,
                Y_ORIGINAL,
                X_WATERMARK,
                Y_WATERMARK,
                Nfi,
                s,
                n
        );

        // Получение значений пикселей из цветового канала
        String chanel = Picture.BLUE_CHANEL;
        int[][] colorComponents = originalImage.getColorComponent(chanel);

        log.info("Цветовой канал : {}", chanel);

        // Расчет степени ортогональности сигнала контейнера к базисным функциям
        double deltaMax = calculateOrthogonality(f, colorComponents, Nfi);

        log.info("Максимальное значение погрешности delta : {}", deltaMax);

        // Преобразование ЦВЗ в одномерный массив значений (255 и 0)
        // значения пикселей ЦВЗ равняются 0 и 255, так как изображение черно-белое

        int[] arrPixelsWatermark = watermark.getBlackWhitePixelsArr();

        // М модуляция полученного массива базисными функциями

        int[][] E = modulation(X_ORIGINAL, Y_ORIGINAL, arrPixelsWatermark, f);

        // Вычисление достаточного коэффициента усиления мощности

        int Kg = calculateKg(n, deltaMax);

        log.info("Достаточные коэффициент усиления по мощности Kg : {}", Kg);

        // Нормирование массива контейнера

        int[][] cNorm = containerNormalization(X_ORIGINAL, Y_ORIGINAL, Kg, colorComponents);

        // Формирование контейнера S = Cnorm + Kg * E
        int[][] S = MatrixUtil.add(cNorm, MatrixUtil.multiplyingMatrixByNumber(E, Kg));

        originalImage.setColorComponent(S, chanel);

        return originalImage;
    }

    @Override
    public Picture decode(Picture filledImage, int X_WATERMARK, int Y_WATERMARK, int startOfSequencePSP) throws Exception {
        log.info("--------------------------- ДЕКОДИРОВАНИЕ ---------------------------");

        int X_ORIGINAL = filledImage.getWidth();
        int Y_ORIGINAL = filledImage.getHeight();

        log.info("Ширина заполненного контейнера X_ORIGINAL : {}", X_ORIGINAL);
        log.info("Высота заполненного контейнера Y_ORIGINAL : {}", Y_ORIGINAL);
        log.info("Ширина извлекаемого ЦВЗ X_WATERMARK : {}", X_WATERMARK);
        log.info("Высота извлекаемого ЦВЗ Y_WATERMARK : {}", Y_WATERMARK);

        log.info("Начальное состаяние регистра s : {}", startOfSequencePSP);

        String chanel = Picture.BLUE_CHANEL;
        int[][] colorComponents = filledImage.getColorComponent(chanel);

        log.info("Цветовой канал : {}", chanel);

        // общее число базисных функций
        int Nfi = X_WATERMARK * Y_WATERMARK;

        log.info("Общее число базисных функций Nfi : {}", Nfi);

        // размерность значащего подмасива отдельной базисной функции
        int n = (int) Math.floor(Math.sqrt((X_ORIGINAL * Y_ORIGINAL)/Nfi));

        log.info("Размерность значащего подмасива отдельной базисной функции n : {}", n);

        List<int[][]> f = getArrayOfBasicFunctions(X_ORIGINAL,
                Y_ORIGINAL,
                X_WATERMARK,
                Y_WATERMARK,
                Nfi,
                startOfSequencePSP,
                n
        );

        // извлечение ЦВЗ
        int[] mResult = extraction(
                X_ORIGINAL,
                Y_ORIGINAL,
                Nfi,
                colorComponents,
                f
        );

        Watermark resultWatermark = new Watermark(X_WATERMARK, Y_WATERMARK);
        resultWatermark.recover(mResult);

        return resultWatermark;
    }

    // ---------------------------------------- Вспомогательные методы ----------------------------------------

    // Извлечение ЦВЗ из контейнера

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

    // Вычисление степени ортогональности
    static double calculateOrthogonality(List<int[][]> f, int[][] components, int Nfi) {
        ArrayList<Double> deltas = new ArrayList<>();
        double delta;
        double deltaMax;

        for (int i = 1; i < Nfi; i++) {
            for (int x = 0; x < components.length; x++) {
                for (int y = 0; y < components.length; y++) {
                    int[][] element = f.get(i);
                    delta = element[x][y] * components[x][y];
                    deltas.add(delta);
                }
            }
        }

        // вычисление максимального значения дельта
        deltaMax = deltas.stream().max(Comparator.naturalOrder()).orElseThrow();

        return deltaMax;
    }

    // Нормирование массива контейнера
    static int[][] containerNormalization(int X, int Y, int Kg, int[][] components) {
        int[][] cNorm = new int[X][Y];

        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                cNorm[x][y] = Math.round((float) components[x][y] / 255 * (255 - 2 * Kg) + Kg);
            }
        }

        return cNorm;
    }

    // Вычисление коэффициента усиления мощности
    static int calculateKg(int n, double deltaMax) {
        int Kg = 1;
        int nf = n * n;

        while (Kg * nf < deltaMax) {
            Kg++;
        }

        return Kg;
    }

    // М Модуляция полученного массива базисными функциями
    static int[][] modulation(int X, int Y, int[] M, List<int[][]> f) {
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
                    int[][] element = f.get(i);
                    sum += mVecBin.get(i) * element[x][y];
                }
                result[x][y] = sum;
            }
        }

        return result;
    }

    static List<int[][]> getArrayOfBasicFunctions(int X_ORIGINAL, int Y_ORIGINAL, int X_WATERMARK, int Y_WATERMARK, int Nfi, int s, int n) {
        if (s >= Nfi) {
            return null; // Исключение
        }

        log.info("Начальное состояние регистра s : {}", s);

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

        // степень полинома
        d = log(Nfi, 2);

        log.info("Степень полинома d : {}", d);

        // выбор полинома в зависимости от d
        int[] polynomial = Polynomial.getPoly(d);

        log.info("Полином : {}", Arrays.toString(polynomial));

        int[] psp = pspGenerator(s, d, polynomial); // Псевдослучайная последовательность

        log.info("Размер ПСП : {}", psp.length);

        // формирование массива базисных функций

        List<int[][]> f = formationOfArrayOfBasisFunctions(
                basicFunc,  // массив базисных функций
                psp,        // Псевдослучайная последовательность
                n,          // размерность значащего подмасива отдельной базисной функции
                X_ORIGINAL, // ширина оригинального изображения
                Y_ORIGINAL, // высота оригинального изображения
                Nfi);       // общее число базисных функций

        return f;
    }

    // Формирование массива базисных функиций
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

    // Генератор псевдослучайно последовательности (ПСП)
    static int[] pspGenerator(int s, int d, int[] poly) {
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

    // Логарифмирование по основанию
    static int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    // Перевод из 10-й в 2-ю систему счисления
    static int[] d2b(int x, int length) { // здесь нужно добавить корретную проверку на знак
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = Math.abs(x) % 2;
            x = (int) Math.floor(Math.abs(x)/2);
        }
        return result;
    }

    // Перевод из 2-й в 10-ю систему счисления
    static int b2d(int[] x, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result += x[i] * Math.pow(2, i);
        }
        return result;
    }
}