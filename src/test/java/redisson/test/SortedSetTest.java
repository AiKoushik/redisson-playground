package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class SortedSetTest extends BaseTest {

    @Test
    public void testSortedSet() {
        RScoredSortedSetReactive<String> scoredSortedSet = this.client.getScoredSortedSet("student:score", StringCodec.INSTANCE);

        Mono<Void> mono = scoredSortedSet.addScore("Alex", 9.75)    //addScore() increments score, where add() sets given score to be the mentioned value
                .then(scoredSortedSet.add(9.25, "Bale"))
                .then(scoredSortedSet.addScore("Chris", 8.5))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();

        scoredSortedSet.entryRange(0, 1)
                .flatMapIterable(Function.identity())   //converts list into Flux
                .map(se -> "score: " + se.getScore() + " value: " + se.getValue())
                .doOnNext(System.out::println)
                .subscribe();
    }
}
