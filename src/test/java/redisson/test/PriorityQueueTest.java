package redisson.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redisson.test.solutions.Category;
import redisson.test.solutions.UserOrder;
import redisson.test.solutions.UserPriorityQueue;

import java.time.Duration;

public class PriorityQueueTest extends BaseTest{

    private UserPriorityQueue userPriorityQueue;

    @BeforeAll
    public void setUpQueue(){
        //zrange user_order:queue 0 -1
        RScoredSortedSetReactive<UserOrder> scoredSortedSet = this.client.getScoredSortedSet("user_order:queue", new TypedJsonJacksonCodec(UserOrder.class));
        this.userPriorityQueue = new UserPriorityQueue(scoredSortedSet);
    }

    @Test
    public void producer(){
        Flux.interval(Duration.ofSeconds(1))
                .map(l -> l.intValue() * 5)//this is to make order numbers to keep incrementing beyond 5, as there would be 5 orders  each second
                .doOnNext(i ->
                    {
                        UserOrder user01 = new UserOrder(i + 1, Category.GUEST);
                        UserOrder user02 = new UserOrder(i + 2, Category.STD);
                        UserOrder user03 = new UserOrder(i + 3, Category.PRIME);
                        UserOrder user04 = new UserOrder(i + 4, Category.GUEST);
                        UserOrder user05 = new UserOrder(i + 5, Category.PRIME);

                        Mono<Void> mono = Flux.just(user01, user02, user03, user04, user05)
                                .flatMap(this.userPriorityQueue::add)
                                .then();

                        StepVerifier.create(mono)
                                .verifyComplete();
                    }).subscribe();
        sleep(30_000);
    }

    @Test
    public void consumer(){
        this.userPriorityQueue.takeItems()
                .delayElements(Duration.ofMillis(1000))
                .doOnNext(System.out::println)
                .subscribe();

        sleep(300_000);
    }
}
