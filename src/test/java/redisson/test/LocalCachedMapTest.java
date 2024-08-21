package redisson.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import reactor.core.publisher.Flux;
import redisson.test.config.RedissonConfig;
import redisson.test.dto.Student;

import java.time.Duration;
import java.util.List;

public class LocalCachedMapTest extends BaseTest{

    RLocalCachedMap<Integer, Student> studentsMap;

    @BeforeEach
    public void setupClient(){
        RedissonConfig redissonConfig = new RedissonConfig();
        RedissonClient redissonClient = redissonConfig.getClient();

        //below line is no longer supported since redisson 3.15.0, check new approach below
        //LocalCachedMapOptions.defaults();

        //New Approach

        LocalCachedMapOptions<Integer, Student> options = LocalCachedMapOptions.<Integer, Student>name("students")
                //.evictionPolicy(LocalCachedMapOptions.EvictionPolicy.NONE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE)
                //.cacheProvider(LocalCachedMapOptions.CacheProvider.REDISSON)
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE);
                //.cacheSize(5);

        //LocalCachedMapOptions<Integer, Student> options = LocalCachedMapOptions.name("students");

        studentsMap = redissonClient.getLocalCachedMap(options);

        //map.getCachedMap();
    }

    @Test
    public void appServer1(){
        Student student1 = new Student("Samuel", 19, "Virginia", List.of("A", "B", "A"));
        Student student2 = new Student("Jason", 21, "Florida", List.of("B", "A", "A"));

        this.studentsMap.put(1, student1);
        this.studentsMap.put(2, student2);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println(i + " : " + studentsMap.get(1)))
                .subscribe();

        sleep(60000);
    }

    @Test
    public void appServer2(){
        Student student1 = new Student("Updated Samuel", 19, "Virginia", List.of("A", "B", "A"));

        this.studentsMap.put(1, student1);
    }
}
