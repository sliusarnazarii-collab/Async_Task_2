import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WorkStealingSearch {

    private static final long THRESHOLD = 100_000;

    public int[] search(int[][] array) {
        ForkJoinPool pool = new ForkJoinPool();
        try {
            return pool.invoke(new SearchTask(array, 0, array.length));
        } finally {
            pool.shutdown();
        }
    }

    private static class SearchTask extends RecursiveTask<int[]> {
        private final int[][] array;
        private final int from;
        private final int to;

        SearchTask(int[][] array, int from, int to) {
            this.array = array;
            this.from = from;
            this.to = to;
        }

        @Override
        protected int[] compute() {
            if (to - from <= 1 || (long) (to - from) * array[0].length <= THRESHOLD) {
                for (int i = from; i < to; i++) {
                    for (int j = 0; j < array[i].length; j++) {
                        if (array[i][j] == i + j) {
                            return new int[]{i, j};
                        }
                    }
                }
                return null;
            }

            int mid = (from + to) / 2;
            SearchTask upper = new SearchTask(array, from, mid);
            SearchTask lower = new SearchTask(array, mid, to);
            lower.fork();                         // нижню половину віддаємо в чергу
            int[] upperResult = upper.compute();  // верхню рахуємо тут
            int[] lowerResult = lower.join();     // чекаємо нижню
            return upperResult != null ? upperResult : lowerResult;
        }
    }
}