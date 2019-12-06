package com.wf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;


@Configuration
public class RedisConfig {

    @Value("${redis.hostports}")
    private String hostports;

    /**
     * 冻结lua脚本
     */
    public static final String FREEZE_LUA =
            "local exists = redis.call(\"exists\",KEYS[1])\n" +
                    "if(exists ~= 0) then\n" +
                    "  local num = redis.call(\"get\",KEYS[1])\n" +
                    "  local now = num - tonumber(ARGV[1])\n" +
                    "  if(now >= 0) then\n" +
                    "    redis.call(\"set\",KEYS[1],now,\"EX\",\"180\")\n" +
                    "    return now\n" +
                    "  else\n" +
                    "    return -2\n" +
                    "  end\n" +
                    "else\n" +
                    "  return -1\n" +
                    "end";

    /**
     *
     */
    public static final String FIND_ADD_LUA =
            "local exists = redis.call(\"exists\",KEYS[1])\n" +
                    "if(exists ~= 0) then\n" +
                    "  local num = redis.call(\"get\",KEYS[1])\n" +
                    "  local now = num - tonumber(ARGV[1])\n" +
                    "  if(now >= 0) then\n" +
                    "    redis.call(\"set\",KEYS[1],now,\"EX\",\"180\")\n" +
                    "    return now\n" +
                    "  else\n" +
                    "    return -2\n" +
                    "  end\n" +
                    "else\n" +
                    "  redis.call(\"set\",KEYS[1],ARGV[2],\"EX\",\"180\")\n" +
                    "  return -1\n" +
                    "end";

    /**
     * 回滚lua
     */
    public static final String ROLLBACK_LUA =
            "local exists = redis.call(\"exists\",KEYS[1])\n" +
                    "if(exists ~= 0) then\n" +
                    "  local num = redis.call(\"get\",KEYS[1])\n" +
                    "  local now = num + tonumber(ARGV[1])\n" +
                    "  if(now >= tonumber(ARGV[2])) then\n" +
                    "    return -2\n" +
                    "  else\n" +
                    "    redis.call(\"set\",KEYS[1],now,\"EX\",\"180\")\n" +
                    "    return now\n" +
                    "  end\n" +
                    "else\n" +
                    "  return -1\n" +
                    "end";

    /**
     * 锁 lua脚本
     */
    public static final String LOCK_LUA =
            "-- 先判断是否有读或者写权限\n" +
                    "local rw = redis.call(\"exists\",KEYS[1])\n" +
                    "if(rw ~= 0) \n" +
                    "then\n" +
                    "    -- key存在的情况，先要判断是否有权限\n" +
                    "    local val = tonumber(redis.call(\"get\",KEYS[1]))\n" +
                    "    -- 判断val是否为0，大于0表示没有权限，小于0是异常情况\n" +
                    "    -- 针对小于0的情况，对每个key进行纠正处理\n" +
                    "    if(val > 0)\n" +
                    "    then\n" +
                    "        -- 没有权限\n" +
                    "        return 1\n" +
                    "    end\n" +
                    "\n" +
                    "    if(val < 0)\n" +
                    "    then\n" +
                    "        redis.call(\"del\",KEYS[1])\n" +
                    "    end\n" +
                    "end\n" +
                    "\n" +
                    "-- 能获取到全向，先判断是否有值，进行加一操作\n" +
                    "local rw2 = redis.call(\"exists\",KEYS[2])\n" +
                    "if(rw2 ~= 0)\n" +
                    "then\n" +
                    "    local x = tonumber(redis.call(\"get\",KEYS[2]))\n" +
                    "    if(x < 0)\n" +
                    "    then\n" +
                    "        redis.call(\"set\",KEYS[2],1,\"EX\",\"30\")\n" +
                    "    else\n" +
                    "        local y = x + 1\n" +
                    "        redis.call(\"set\",KEYS[2],y,\"EX\",\"30\")\n" +
                    "    end\n" +
                    "else\n" +
                    "    redis.call(\"set\",KEYS[2],1,\"EX\",\"30\")\n" +
                    "end\n" +
                    "return 0";

    /**
     * 解锁 lua脚本
     */
    public static final String UNLOCK_LUA =
            "-- 获取unlock的value\n" +
                    "-- 一般来说，这个unlock的时候key是存在的，不存在就不做处理\n" +
                    "local rw = redis.call(\"exists\",KEYS[1])\n" +
                    "if(rw ~= 0) \n" +
                    "then\n" +
                    "    local val = tonumber(redis.call(\"get\",KEYS[1]))\n" +
                    "    if(val < 0)\n" +
                    "    then\n" +
                    "        redis.call(\"del\",KEYS[1])\n" +
                    "    elseif(val > 0)\n" +
                    "    then\n" +
                    "        local x = val -1\n" +
                    "        redis.call(\"set\",KEYS[1],x,\"EX\",30)\n" +
                    "    else\n" +
                    "        -- null\n" +
                    "    end\n" +
                    "end\n" +
                    "return 0";

    /**
     * 冻结bean
     * @return
     */
    @Bean("freezePassword")
    public DefaultRedisScript<Long> freezePasswordRedisScript(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptText(FREEZE_LUA);
        return redisScript;
    }

    /**
     * 回滚lua
     */
    @Bean("rollbackForFreeze")
    public DefaultRedisScript<Long> rollbackRedisScript(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptText(ROLLBACK_LUA);
        return redisScript;
    }

    /**
     * retemplate相关配置
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setConnectionFactory(factory);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * stringRedisTemplate
     */
    @Bean("stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public ValueOperations<String,Object> valueOperations(RedisTemplate<String,Object> redisTemplate){
        return redisTemplate.opsForValue();
    }

    /**
     * redis连接工厂
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        //优先读取环境变量
        String redisAddress = System.getenv("REDIS_HOSTPORTS");
        if (redisAddress != null){
            this.hostports = redisAddress;
        }
        JedisConnectionFactory connectionFactory;
        String[] hostport = hostports.split(",");
        if (hostport.length < 2){
            int i = hostports.indexOf(":");
            String host = hostports.substring(0,i);
            System.out.println("host"+host);
            String port = hostports.substring(i + 1);
            System.out.println("port"+port);
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host,Integer.parseInt(port));
            connectionFactory = new JedisConnectionFactory(configuration);
        } else {
            RedisClusterConfiguration clusterConfiguration =
                    new RedisClusterConfiguration(Arrays.asList(hostports.split(",")));
            clusterConfiguration.setMaxRedirects(10);
            connectionFactory = new JedisConnectionFactory(clusterConfiguration);
        }
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxWaitMillis(5000);
        poolConfig.setMinIdle(1);
        poolConfig.setTestWhileIdle(true);
        connectionFactory.setPoolConfig(poolConfig);
        connectionFactory.setTimeout(5000);
        connectionFactory.setUsePool(true);
        return connectionFactory;
    }
}
