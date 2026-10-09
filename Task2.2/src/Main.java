import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Main {

    public static void main(String[] args) {
        File directory = readDirectory(new Scanner(System.in));

        ForkJoinPool pool = new ForkJoinPool();
        try {
            ForkJoinTask<List<File>> future = pool.submit(new ImageScanTask(directory));
            System.out.println("Пошук запущено, зачекайте...");

            List<File> images = future.get();
            System.out.println("Знайдено зображень: " + images.size());

            if (images.isEmpty()) {
                System.out.println("Зображень не знайдено, відкривати нічого.");
            } else {
                File last = images.get(images.size() - 1);
                System.out.println("Останнє зображення: " + last);
                openFile(last);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Пошук перервано.");
        } catch (ExecutionException e) {
            System.out.println("Під час пошуку сталась помилка: " + e.getCause());
        } finally {
            pool.shutdown();
        }
    }

    private static File readDirectory(Scanner in) {
        while (true) {
            System.out.print("Введіть шлях до директорії: ");
            if (!in.hasNextLine()) {
                System.out.println("\nВведення завершено.");
                System.exit(0);
            }
            String line = in.nextLine().trim();
            if (line.length() >= 2 && line.startsWith("\"") && line.endsWith("\"")) {
                line = line.substring(1, line.length() - 1);
            }
            File directory = new File(line);
            if (line.isEmpty() || !directory.isDirectory()) {
                System.out.println("  Такої директорії не існує. Спробуйте ще раз.");
            } else if (!directory.canRead()) {
                System.out.println("  Немає прав на читання цієї директорії. Оберіть іншу.");
            } else {
                return directory;
            }
        }
    }

    private static void openFile(File file) {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            System.out.println("Автоматично відкрити файл неможливо, відкрийте його вручну.");
            return;
        }
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Не вдалося відкрити файл: " + e.getMessage());
        }
    }
}