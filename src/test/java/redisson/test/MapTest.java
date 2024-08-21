package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redisson.test.dto.Student;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MapTest extends BaseTest{

    @Test
    public void mapTest() {
        RMapReactive<String, String> map = this.client.getMap("user:1", StringCodec.INSTANCE);

        Mono<String> name = map.put("name", "Samuel");
        Mono<String> age = map.put("age", "19");
        Mono<String> city = map.put("city", "Alaska");

        StepVerifier.create(name.concatWith(age).concatWith(city)
                .then())
                .verifyComplete();
    }

    @Test
    public void mapTest2() {
        RMapReactive<String, Serializable> map = this.client.getMap("user:2", StringCodec.INSTANCE);

        Map<String, Serializable> javaMap = Map.of(
                "name", "Leo",
                "age", 29,
                "city", "Madrid"
        );

        StepVerifier.create(map.putAll(javaMap)
                        .then())
                .verifyComplete();
    }

    @Test
    public void mapTest3() {
        RMapReactive<Integer, Student> map = this.client.getMap("users", new TypedJsonJacksonCodec(Integer.class, Student.class));

        Student student1 = new Student("Samuel", 19, "Virginia", List.of("A", "B", "A"));
        Student student2 = new Student("Jason", 21, "Florida", List.of("B", "A", "A"));

        Mono<Student> mono1 = map.put(1, student1);
        Mono<Student> mono2 = map.put(2, student2);

        StepVerifier.create(mono1.concatWith(mono2)
                        .then())
                .verifyComplete();
    }
}
