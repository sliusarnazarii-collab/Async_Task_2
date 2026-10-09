import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WorkDealingSearch {

    public int[] search(int[][] array) throws InterruptedException, ExecutionException {
        int threads = Runtime.getRuntime().availableProcessors();
        int rows = array.length;
        int parts = Math.min(threads, rows);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<int[]>> futures = new ArrayList<>();
            int start = 0;
            for (int p = 0; p < parts; p++) {
                int size = rows / parts + (p < rows % parts ? 1 : 0);
                int from = start;
                int to = start + size;
                futures.add(pool.submit(() -> searchRows(array, from, to)));
                start = to;
            }
            int[] found = null;
            for (Future<int[]> f : futures) {
                int[] result = f.get();
                if (found == null) {
                    found = result;
                }
            }
            return found;
        } finally {
            pool.shutdown();
        }
    }

    private int[] searchRows(int[][] array, int from, int to) {
        for (int i = from; i < to; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] == i + j) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}