package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RMapReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redisson.test.dto.Student;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapCacheTest extends BaseTest{

    @Test
    public void testMapCache(){
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapCacheReactive<Object, Object> mapCache = this.client.getMapCache("users:cache", codec);

        Student student1 = new Student("Samuel", 19, "Virginia", List.of("A", "B", "A"));
        Student student2 = new Student("Jason", 21, "Florida", List.of("B", "A", "A"));

        Mono<Object> mono1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        Mono<Object> mono2 = mapCache.put(2, student2, 10, TimeUnit.SECONDS);

        StepVerifier.create(mono1.concatWith(mono2)     //using then() instead of concatWith() ensures mono1 is executed before mono2
                        .then())
                .verifyComplete();

        sleep(3000);

        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();

        sleep(3000);

        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();
    }
}
