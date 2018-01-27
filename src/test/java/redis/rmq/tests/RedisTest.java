package redis.rmq.tests;

import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.rmq.Consumer;

/**
 * User: liji
 * Date: 18/1/27
 * Time: 下午7:14
 */
public class RedisTest {

    private static final String TOPIC = "fb_user";
    private static final String REDIS_HOST = "104.236.82.206";
    private Consumer consumer;

    @Test
    public void countUnread() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), REDIS_HOST);
        consumer = new Consumer(pool.getResource(), "a subscriber", TOPIC);
    }
}

