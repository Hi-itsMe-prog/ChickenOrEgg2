public class ChickenEggDebate {
    private static String lastWord = "";
    private static boolean res = false;
    private static final Object lock = new Object();
    private static String turn = "Chicken"; // Кто сейчас должен говорить

    public static void main(String[] args) {
        Thread chickenThread = new Thread(new ChickenRunnable(), "Chicken");
        Thread eggThread = new Thread(new EggRunnable(), "Egg");

        System.out.println("Начинается спор: Что появилось сначала - яйцо или курица?");

        chickenThread.start();
        eggThread.start();

        try {
            chickenThread.join();
            eggThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Результат спора:");
        if ("Курица".equals(lastWord)) {
            System.out.println("ПОБЕДИЛА КУРИЦА!");
        } else if ("Яйцо".equals(lastWord)) {
            System.out.println("ПОБЕДИЛО ЯЙЦО!");
        }
    }

    private static boolean speak(String word, String threadName, int iteration) {
        synchronized (lock) {
            // Ждем своей очереди
            while (!threadName.equals(turn) && !res) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return true;
                }
            }

            if (res) return true;

            // Говорим
            lastWord = word;
            System.out.println(lastWord);

            if (iteration >= 5) {
                res = true;
            }

            // Передаем ход другому потоку
            turn = threadName.equals("Chicken") ? "Egg" : "Chicken";
            lock.notifyAll();

            return res;
        }
    }

    static class ChickenRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                if (speak("Курица", "Chicken", i)) {
                    break;
                }
            }
        }
    }

    static class EggRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                if (speak("Яйцо", "Egg", i)) {
                    break;
                }
            }
        }
    }
}