package redisson.test;

import org.junit.jupiter.api.*;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TransactionTest extends BaseTest {

    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    @BeforeAll
    public void setUpAccounts() {
        this.user1Balance = this.client.getBucket("user:1:balance", LongCodec.INSTANCE);
        this.user2Balance = this.client.getBucket("user:2:balance", LongCodec.INSTANCE);

        Mono<Void> mono = user1Balance.set(100L)
                .then(user2Balance.set(0L))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @AfterAll
    public void accountBalanceStatus(){
        //Mono<Void> mono = Flux.zip(this.user1Balance.get(), this.user2Balance.get()) //check later to find the distinction caused by using "this" here and in later parts
        Mono<Void> mono = Flux.zip(user1Balance.get(), user2Balance.get())
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void nonTransactionTest() {
        this.transfer(user1Balance, user2Balance, 50L)
                .thenReturn(0)  //deliberately written line to cause error. it highlights the erroneous behaviour that the transfer does not revert back even after error.
                .map(i -> (5/i))      //deliberately written line to cause error. it highlights the erroneous behaviour that the transfer does not revert back even after error.
                .doOnError(System.err::println)
                .subscribe();
        sleep(1000);
    }

    @Test
    public void transactionTest() {
        RTransactionReactive transaction = this.client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);

        this.transfer(user1Balance, user2Balance, 50L)
                .thenReturn(0)  //deliberately written line to cause error. it highlights the behaviour that the transfer  reverts back properly if transaction is used.
                .map(i -> (5/i))    //deliberately written line to cause error. it highlights the behaviour that the transfer  reverts back properly if transaction is used.
                .then(transaction.commit())
                .doOnError(System.err::println)
                .onErrorResume(ex -> transaction.rollback())
                .subscribe();

        sleep(1000);
    }

    private Mono<Void> transfer(RBucketReactive<Long> fromAccount, RBucketReactive<Long> toAccount, Long amount) {
        return Flux.zip(fromAccount.get(), toAccount.get())    // zip() makes it a [balance1, balance2] tuple
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> fromAccount.set(t.getT1() - amount).thenReturn(t))
                .flatMap(t -> toAccount.set(t.getT2() + amount))
                .then();
    }

}

