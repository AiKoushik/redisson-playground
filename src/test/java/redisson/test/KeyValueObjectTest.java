package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redisson.test.dto.Student;

import java.util.Arrays;

public class KeyValueObjectTest extends BaseTest{

    @Test
    public void keyValueObjectTest() {
        Student student = new Student("Matthew", 22, "Alabama", Arrays.asList("A", "A", "B"));
        //RBucketReactive<Student> bucket = this.client.getBucket("student:1", JsonJacksonCodec.INSTANCE);
        RBucketReactive<Student> bucket = this.client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        Mono<Void> set = bucket.set(student);
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }
}
