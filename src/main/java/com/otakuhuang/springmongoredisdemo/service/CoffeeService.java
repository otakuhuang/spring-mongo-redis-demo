package com.otakuhuang.springmongoredisdemo.service;

import com.otakuhuang.springmongoredisdemo.model.Coffee;
import com.otakuhuang.springmongoredisdemo.repository.CoffeeRepository;
import lombok.extern.log4j.Log4j2;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

/**
 * @author otaku
 * @version 1.0
 * @date 2022/1/16 23:28
 * @description description
 */
@Log4j2
@Service
public class CoffeeService {
    private static final String CACHE_COFFEE_ALL = "coffee-all";
    private static final String CACHE_COFFEE = "coffee";

    @Autowired
    private CoffeeRepository coffeeRepository;
    @Autowired
    private RedisTemplate<String, List<Coffee>> redisTemplate;

    public void getAllCoffee() {
        ValueOperations<String, List<Coffee>> valueOperations = redisTemplate.opsForValue();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(CACHE_COFFEE_ALL))) {
            log.info("Get all coffee from Redis");
            List<Coffee> coffees = valueOperations.get(CACHE_COFFEE_ALL);
            coffees.forEach(c -> log.info("Redis coffee: {}", c));
        } else {
            List<Coffee> coffees = coffeeRepository.findAll();
            coffees.forEach(c -> log.info("Saved coffee: {}", c));
            valueOperations.set(CACHE_COFFEE_ALL, coffees);
            redisTemplate.expire(CACHE_COFFEE_ALL, 1, TimeUnit.MINUTES);
        }
    }

    public void getOneCoffee(String name) {
        HashOperations<String, String, Coffee> hashOperations = redisTemplate.opsForHash();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(CACHE_COFFEE)) && hashOperations.hasKey(CACHE_COFFEE, name)) {
            log.info("Get coffee {} from Redis", name);
            Coffee coffee = hashOperations.get(CACHE_COFFEE, name);
            log.info("Redis coffee: {}", coffee);
        } else {
            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("name", exact().ignoreCase());
            Optional<Coffee> coffee = coffeeRepository.findOne(
                    Example.of(Coffee.builder().name(name).build(), matcher));
            if (coffee.isPresent()) {
                log.info("Saved coffee: {}", coffee.get());
                hashOperations.put(CACHE_COFFEE, name, coffee.get());
                redisTemplate.expire(CACHE_COFFEE, 1, TimeUnit.MINUTES);
            }
        }
    }

    public void addOneCoffee(String name, double price) {
        Coffee coffee = Coffee.builder()
                .name(name)
                .price(Money.of(CurrencyUnit.of("CNY"), price))
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        Coffee saved = coffeeRepository.insert(coffee);
        log.info("Coffee Saved: {}", saved);
    }

    public void removeAllCoffee() {
        ValueOperations<String, List<Coffee>> valueOperations = redisTemplate.opsForValue();
        redisTemplate.delete(CACHE_COFFEE_ALL);
        coffeeRepository.deleteAll();
    }
}
