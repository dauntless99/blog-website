import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 高并发压力测试工具
 * 用于验证网关限流效果
 * 
 * 使用方式：
 * 1. 确保网关服务已启动在 http://localhost:8080
 * 2. 编译：javac LoadTest.java
 * 3. 运行：java LoadTest
 * 
 * 参数说明：
 * - targetQPS: 目标QPS（默认6000）
 * - durationSeconds: 测试持续时间（默认30秒）
 * - concurrentThreads: 并发线程数（默认100）
 * - targetUrl: 目标URL
 */
public class LoadTest {

    // 测试配置参数
    private static final int TARGET_QPS = 6000;
    private static final int DURATION_SECONDS = 30;
    private static final int CONCURRENT_THREADS = 100;
    private static final String TARGET_URL = "http://localhost:8080/api/blog/posts";

    // 统计指标
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failCount = new AtomicInteger(0);
    private static final AtomicInteger blockedCount = new AtomicInteger(0);
    private static final List<Long> responseTimes = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================");
        System.out.println("       高并发限流测试工具 v1.0");
        System.out.println("============================================");
        System.out.println("测试配置：");
        System.out.println("  目标QPS: " + TARGET_QPS);
        System.out.println("  测试时长: " + DURATION_SECONDS + "秒");
        System.out.println("  并发线程数: " + CONCURRENT_THREADS);
        System.out.println("  目标URL: " + TARGET_URL);
        System.out.println("============================================");

        // 创建HTTP客户端
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        // 计算每个线程的QPS
        int qpsPerThread = TARGET_QPS / CONCURRENT_THREADS;
        long intervalMs = 1000L / qpsPerThread;

        System.out.println("  每线程QPS: " + qpsPerThread);
        System.out.println("  请求间隔: " + intervalMs + "ms");
        System.out.println("--------------------------------------------");

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(CONCURRENT_THREADS);

        // 启动测试线程
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executor.submit(new LoadTestWorker(httpClient, startLatch, completionLatch, intervalMs));
        }

        // 开始计时并触发所有线程
        long startTime = System.currentTimeMillis();
        startLatch.countDown();

        // 等待所有线程完成
        completionLatch.await();
        long endTime = System.currentTimeMillis();

        // 关闭线程池
        executor.shutdown();

        // 输出测试结果
        printResults(startTime, endTime);
    }

    /**
     * 打印测试结果
     */
    private static void printResults(long startTime, long endTime) {
        long durationMs = endTime - startTime;
        int totalRequests = successCount.get() + failCount.get();
        double actualQps = totalRequests / (durationMs / 1000.0);

        // 计算平均响应时间
        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        // 计算P95响应时间
        double p95ResponseTime = 0;
        if (!responseTimes.isEmpty()) {
            List<Long> sortedTimes = new ArrayList<>(responseTimes);
            sortedTimes.sort(Long::compareTo);
            int p95Index = (int) (sortedTimes.size() * 0.95);
            p95ResponseTime = sortedTimes.get(Math.min(p95Index, sortedTimes.size() - 1));
        }

        System.out.println("\n============================================");
        System.out.println("              测试结果统计");
        System.out.println("============================================");
        System.out.printf("测试时长: %.2f 秒%n", durationMs / 1000.0);
        System.out.println("总请求数: " + totalRequests);
        System.out.printf("实际QPS: %.2f%n", actualQps);
        System.out.println("--------------------------------------------");
        System.out.println("成功请求: " + successCount.get() + " (" + 
                String.format("%.2f", successCount.get() * 100.0 / totalRequests) + "%)");
        System.out.println("失败请求: " + failCount.get() + " (" + 
                String.format("%.2f", failCount.get() * 100.0 / totalRequests) + "%)");
        System.out.println("被限流: " + blockedCount.get());
        System.out.println("--------------------------------------------");
        System.out.printf("平均响应时间: %.2f ms%n", avgResponseTime);
        System.out.printf("P95响应时间: %.2f ms%n", p95ResponseTime);
        System.out.println("============================================");

        // 限流效果判断
        if (blockedCount.get() > 0) {
            System.out.println("\n✓ 限流生效！检测到 " + blockedCount.get() + " 个请求被限流");
        } else {
            System.out.println("\n✗ 未检测到限流，请检查：");
            System.out.println("  1. 网关服务是否启动");
            System.out.println("  2. 限流配置是否正确");
            System.out.println("  3. 目标QPS是否超过限流阈值");
        }
    }

    /**
     * 测试工作线程
     */
    static class LoadTestWorker implements Runnable {
        private final HttpClient httpClient;
        private final CountDownLatch startLatch;
        private final CountDownLatch completionLatch;
        private final long intervalMs;

        public LoadTestWorker(HttpClient httpClient, CountDownLatch startLatch, 
                            CountDownLatch completionLatch, long intervalMs) {
            this.httpClient = httpClient;
            this.startLatch = startLatch;
            this.completionLatch = completionLatch;
            this.intervalMs = intervalMs;
        }

        @Override
        public void run() {
            try {
                // 等待开始信号
                startLatch.await();

                long endTime = System.currentTimeMillis() + (DURATION_SECONDS * 1000L);

                while (System.currentTimeMillis() < endTime) {
                    long requestStart = System.currentTimeMillis();
                    
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(TARGET_URL))
                                .header("Content-Type", "application/json")
                                .timeout(Duration.ofSeconds(3))
                                .GET()
                                .build();

                        HttpResponse<String> response = httpClient.send(request, 
                                HttpResponse.BodyHandlers.ofString());

                        long responseTime = System.currentTimeMillis() - requestStart;
                        responseTimes.add(responseTime);

                        if (response.statusCode() == 200) {
                            successCount.incrementAndGet();
                        } else if (response.statusCode() == 429) {
                            // 429 = Too Many Requests - 限流响应
                            blockedCount.incrementAndGet();
                            failCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                            System.out.println("请求失败，状态码: " + response.statusCode());
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }

                    // 控制请求频率
                    long elapsed = System.currentTimeMillis() - requestStart;
                    if (elapsed < intervalMs) {
                        Thread.sleep(intervalMs - elapsed);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                completionLatch.countDown();
            }
        }
    }
}