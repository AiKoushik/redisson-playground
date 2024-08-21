package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QueueTest extends BaseTest {
    @Test
    public void testQueue() {
        RQueueReactive<Object> queue = this.client.getQueue("number-queue", LongCodec.INSTANCE);

        Mono<Void> queueAdd = Flux.range(1, 10)
                .map(Long::valueOf)
                .flatMap(queue::add)
                .then();

        StepVerifier.create(queueAdd)
                .verifyComplete();

        sleep(10000);

        Mono<Void> queuePoll = queue.poll()
                .repeat(3)  //total 4 times
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(queuePoll)
                .verifyComplete();

        StepVerifier.create(queue.size())
                .expectNext(6)
                .verifyComplete();
    }
}
