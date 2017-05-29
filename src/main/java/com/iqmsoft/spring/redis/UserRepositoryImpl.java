package com.iqmsoft.spring.redis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqmsoft.spring.redis.User.UserGroup;


@Component
public class UserRepositoryImpl implements UserRepository {

    private static final Logger log = Logger.getLogger(UserRepositoryImpl.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final Charset charSet = Charset.forName("utf-8");
    
    private final Jackson2JsonRedisSerializer<User> jackson2JsonRedisSerializer 
        =  new Jackson2JsonRedisSerializer<User>(User.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

   
    @PostConstruct
    public void init() {
        
       
    }

    @Override
    public User saveUser(Long id) {
        log.debug("Saving user...");
        User user = new User();
        user.setId(id);
        return user;
    }

    @Override
    public User getUser(Long id) {
        log.debug("Retrieving user...");
        User user = new User();
        user.setId(id);
        user.setUserName("Test1");
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user...");
       
    }

    @Override
    public User getFromRedis(final Long id) {
        log.debug("Getting user from redis...");
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        return (User)redisTemplate.opsForValue().get(id);
        }

    @Override
    public User saveToRedis(Long id) {
        log.debug("Saving user to redis...");
        User user = new User();
        user.setId(id);
        user.setUserName("test-UserName");
        UserGroup userGroup = new UserGroup();
        userGroup.setId(id);
        userGroup.setUserGroupName("test-GroupName");
        user.setUserGroup(userGroup);
        try {
            log.debug("writeValueAsString: " + objectMapper.writeValueAsString(user));
            log.debug("writeValueAsBytes: "
                    + new String(objectMapper.writeValueAsBytes(user), charSet));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.opsForValue().set(id.toString(), user, 5, TimeUnit.MINUTES);
      
        return user;
    }

   

   
}
