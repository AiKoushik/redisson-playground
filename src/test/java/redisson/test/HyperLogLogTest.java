package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class HyperLogLogTest extends BaseTest{

    @Test
    public void testHyperLogLog(){
        RHyperLogLogReactive<Long> counter = this.client.getHyperLogLog("users:visits", LongCodec.INSTANCE);

        /*
        List<Long> list = LongStream.rangeClosed(1, 47_000)
                .boxed()
                .collect(Collectors.toList());
        Mono<Boolean> addAll = counter.addAll(list);
         */

        List<Long> list1 = LongStream.rangeClosed(1, 47_000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> list2 = LongStream.rangeClosed(45_000, 100_000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> list3 = LongStream.rangeClosed(10_000, 15_000)
                .boxed()
                .collect(Collectors.toList());

        Mono<Void> addAllLists = Flux.just(list1, list2, list3)
                .flatMap(counter::addAll)
                .then();

        StepVerifier.create(addAllLists)
                .verifyComplete();

        counter.count()
                .doOnNext(System.out::println)
                .subscribe();
    }
}
