package redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;

public class PubSubTest extends BaseTest {

    //In redis-cli execute 'publish chat-room:sports "hello world"' command to see the subscribers receive the message

    @Test
    public void subscriber1() {
        RTopicReactive sportsTopic = this.client.getTopic("chat-room:sports", StringCodec.INSTANCE);

        sportsTopic.getMessages(String.class)
                .doOnNext(System.out::println)
                .doOnError(System.err::println)
                .subscribe();

        sleep(120_000);
    }

    @Test
    public void subscriber2() {
        RTopicReactive sportsTopic = this.client.getTopic("chat-room:sports", StringCodec.INSTANCE);

        sportsTopic.getMessages(String.class)
                .doOnNext(System.out::println)
                .doOnError(System.err::println)
                .subscribe();

        sleep(120_000);
    }

    @Test
    public void patternSubscriber() {
        RPatternTopicReactive patternTopic = this.client.getPatternTopic("chat-room:*", StringCodec.INSTANCE);

        patternTopic.addListener(String.class, new PatternMessageListener<String>() {
            @Override
            public void onMessage(CharSequence pattern, CharSequence topic, String message) {
                System.out.println("pattern: " + pattern + " topic: " + topic + " message: " + message);
            }
        }).subscribe();

        sleep(120_000);
    }

}
