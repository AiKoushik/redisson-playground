package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class BucketsAsMapTest extends BaseTest{

    @Test
    public void bucketsAsMapTest() {
        //set "user:1:name", "user:2:name", "user:3:name" using "set user:1:name Sam" commands in redis-cli
        //do not set "user:4:name" and notice that message about it not existing won't show up on spring side

        Mono<Void> mono = this.client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name", "user:4:name")
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
