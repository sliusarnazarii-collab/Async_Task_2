import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;


public class ImageScanTask extends RecursiveTask<List<File>> {
    private static final String[] EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    private final File directory;

    public ImageScanTask(File directory) {
        this.directory = directory;
    }

    @Override
    protected List<File> compute() {
        List<File> images = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files == null) {
            return images;
        }
        Arrays.sort(files);

        List<ImageScanTask> subTasks = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                if (Files.isSymbolicLink(file.toPath())) {
                    continue;
                }
                ImageScanTask task = new ImageScanTask(file);
                task.fork();
                subTasks.add(task);
            } else if (isImage(file)) {
                images.add(file);
            }
        }

        for (ImageScanTask task : subTasks) {
            images.addAll(task.join());
        }
        return images;
    }

    private static boolean isImage(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : EXTENSIONS) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}