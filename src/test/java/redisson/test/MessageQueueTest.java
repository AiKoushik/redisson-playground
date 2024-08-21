package redisson.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class MessageQueueTest extends BaseTest {

    RBlockingDequeReactive<Long> messageQueue;

    @BeforeAll
    public void setUpQueue(){
        messageQueue = this.client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    @Test
    public void consumer1(){
        this.messageQueue.takeElements()
                .doOnNext(i -> System.out.println("Consumer 1 received: " + i))
                .doOnError(System.err::println)
                .subscribe();

        sleep(60_000);
    }

    @Test
    public void consumer2(){
        this.messageQueue.takeElements()
                .doOnNext(i -> System.out.println("Consumer 2 received: " + i))
                .doOnError(System.err::println)
                .subscribe();

        sleep(60_000);
    }

    @Test
    public void producer1(){
        Mono<Void> mono = Flux.range(1, 60)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> System.out.println("Producer 1 added: " + i))
                .map(Long::valueOf)
                .flatMap(i -> this.messageQueue.add(i))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
