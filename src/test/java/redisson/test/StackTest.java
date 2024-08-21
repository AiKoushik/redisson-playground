package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StackTest extends BaseTest {

    @Test
    public void testStack() {
        //To pass test of deque size, clear this variable in Redis before running
        RDequeReactive<Object> deque = this.client.getDeque("number-stack", LongCodec.INSTANCE);

        Mono<Void> dequeAdd = Flux.range(1, 10)
                .map(Long::valueOf)
                .flatMap(deque::add)
                .then();

        StepVerifier.create(dequeAdd)
                .verifyComplete();

        sleep(10000);

        Mono<Void> queuePoll = deque.pollLast()
                .repeat(3)  //total 4 times
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(queuePoll)
                .verifyComplete();

        StepVerifier.create(deque.size())
                .expectNext(6)
                .verifyComplete();
    }
}
