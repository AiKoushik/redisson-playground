package redisson.test.solutions;

import org.redisson.api.RScoredSortedSetReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UserPriorityQueue {

    RScoredSortedSetReactive<UserOrder> userQueue;

    public UserPriorityQueue(RScoredSortedSetReactive<UserOrder> userQueue) {
        this.userQueue = userQueue;
    }

    public Mono<Void> add(UserOrder userOrder){
        return this.userQueue.add(
                //userOrder.getCategory().ordinal(),
                getScore(userOrder.getCategory().ordinal()),
                userOrder
        ).then();
    }

    public Flux<UserOrder> takeItems(){
        return this.userQueue.takeFirstElements()
                .limitRate(1);
    }

    private double getScore(Integer categoryOrdinal){
        //this is to make sure that for items of same category, the first one to arrive gets processed first
        return categoryOrdinal + Double.parseDouble("0." + System.nanoTime());
    }
}
