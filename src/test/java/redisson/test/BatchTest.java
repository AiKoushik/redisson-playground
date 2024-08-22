package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class BatchTest extends BaseTest{

    @Test
    public void testBatch() {
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());
        RListReactive<Long> list = batch.getList("batch-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = batch.getSet("batch-set", LongCodec.INSTANCE);

        for(long i = 0; i < 500_000; i += 1){
            list.add(i);
            set.add(i);
        }

        Mono<BatchResult<?>> execute = batch.execute();

        StepVerifier.create(execute.then())
                .verifyComplete();

        //took about 6.388 seconds for 500,000 numbers in both list and set
    }

    @Test
    public void testWithoutBatch() {
        RListReactive<Long> list = this.client.getList("non-batch-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = this.client.getSet("non-batch-set", LongCodec.INSTANCE);

        Mono<Void> add = Flux.range(0, 500_000)
                .map(Long::valueOf)
                .flatMap(i -> list.add(i).then(set.add(i)))
                .then();

        StepVerifier.create(add)
                .verifyComplete();

        //took about 30.161 seconds for 500,000 numbers in both list and set
    }
}
