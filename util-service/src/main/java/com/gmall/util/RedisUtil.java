package com.gmall.util;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

    private JedisPool jedisPool;

    public void initJedisPool(String host, int port, int database){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // total
        jedisPoolConfig.setMaxTotal(200);

        // get max wait time when connecting
        jedisPoolConfig.setMaxWaitMillis(1*1000);

        // min Idle
        jedisPoolConfig.setMinIdle(10);
        jedisPoolConfig.setMaxIdle(10);

        // set wait true when connecting
        jedisPoolConfig.setBlockWhenExhausted(true);

        // set test when borrow a connection from pool
        jedisPoolConfig.setTestOnBorrow(true);

        // create connection pool
        jedisPool = new JedisPool(jedisPoolConfig, host, port, 2*1000);

    }

    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
