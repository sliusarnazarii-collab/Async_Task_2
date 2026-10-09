import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int rows = readInt(in, "Кількість рядків (1..10000)", 1, 10000);
        int cols = readInt(in, "Кількість стовпчиків (1..10000)", 1, 10000);
        int min = readInt(in, "Початкове значення елементів", Integer.MIN_VALUE, Integer.MAX_VALUE);
        int max = readInt(in, "Кінцеве значення елементів (не менше за початкове)", min, Integer.MAX_VALUE);

        int[][] array;
        try {
            array = generate(rows, cols, min, max);
        } catch (OutOfMemoryError e) {
            System.out.println("Не вистачило пам'яті для такого масиву. Спробуйте менші розміри.");
            return;
        }
        printArray(array);

        try {
            long start = System.nanoTime();
            int[] stealingResult = new WorkStealingSearch().search(array);
            long stealingTime = System.nanoTime() - start;
            printResult("Work stealing", array, stealingResult, stealingTime);

            start = System.nanoTime();
            int[] dealingResult = new WorkDealingSearch().search(array);
            long dealingTime = System.nanoTime() - start;
            printResult("Work dealing ", array, dealingResult, dealingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Пошук перервано.");
        } catch (Exception e) {
            System.out.println("Під час пошуку сталась помилка: " + e.getMessage());
        }
    }

    private static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + ": ");
            if (!in.hasNextLine()) {
                System.out.println("\nВведення завершено.");
                System.exit(0);
            }
            try {
                int value = Integer.parseInt(in.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("  Число має бути від " + min + " до " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Це не ціле число, спробуйте ще раз.");
            }
        }
    }

    private static int[][] generate(int rows, int cols, int min, int max) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int[][] a = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                a[i][j] = (int) rnd.nextLong(min, (long) max + 1);
            }
        }
        return a;
    }

    private static void printArray(int[][] a) {
        boolean small = a.length <= 20 && a[0].length <= 20;
        int r = small ? a.length : Math.min(a.length, 10);
        int c = small ? a[0].length : Math.min(a[0].length, 10);
        System.out.println(small ? "\nЗгенерований масив:"
                : "\nМасив завеликий для екрана, показано лише верхній лівий кут " + r + " x " + c + ":");
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                System.out.printf("%12d", a[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void printResult(String name, int[][] a, int[] found, long nanos) {
        String result = (found == null)
                ? "елемента не знайдено"
                : "знайдено a[" + found[0] + "][" + found[1] + "] = " + a[found[0]][found[1]]
                + " (" + found[0] + " + " + found[1] + ")";
        System.out.printf("%s: %s. Час: %.3f мс%n", name, result, nanos / 1_000_000.0);
    }
}