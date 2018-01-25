import com.leomii.consumer.pool.SelfDefinedThreadFactory;
import com.leomii.consumer.service.WelcomeService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author leomii
 * @since 2018-1-25
 */
public class ConsumerMain {
    public static void main(String[] args) throws IOException {


        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"application.xml"});
        context.start();
        final WelcomeService hello = context.getBean(WelcomeService.class);
        ExecutorService executorService = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), new SelfDefinedThreadFactory("dubbo-hystrix-consumer"), new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < 20; i++) {
            executorService.submit(new Runnable() {

                public void run() {
                    try {
                        System.out.println(hello.welcome("leomii"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.close();
    }
}