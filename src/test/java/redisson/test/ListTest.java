package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RListReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ListTest extends BaseTest{

    @Test
    public void testList(){
        //To pass test of size 10, clear this variable in Redis before running
        RListReactive<Object> list = this.client.getList("number-input", LongCodec.INSTANCE);

        Mono<Void> listAdd = Flux.range(1, 10)
                .map(Long::valueOf)
                .flatMap(list::add)
                .then();

        StepVerifier.create(listAdd)
                .verifyComplete();

        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    @Test
    public void testListSync(){
        //To pass test of size 10, clear this variable in Redis before running
        RListReactive<Object> list = this.client.getList("number-input-sync", LongCodec.INSTANCE);

        List<Long> longList = LongStream.range(1, 11)
                .boxed()
                .collect(Collectors.toList());

        StepVerifier.create(list.addAll(longList).then())
                .verifyComplete();

        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }
}
