package com.otakuhuang.springmongoredisdemo;

import com.otakuhuang.springmongoredisdemo.converter.MoneyReadConverter;
import com.otakuhuang.springmongoredisdemo.model.Coffee;
import com.otakuhuang.springmongoredisdemo.service.CoffeeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
@EnableMongoRepositories
public class SpringMongoRedisDemoApplication implements ApplicationRunner {

    @Autowired
    private CoffeeService coffeeService;

    @Bean
    public RedisTemplate<String, List<Coffee>> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, List<Coffee>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringMongoRedisDemoApplication.class, args);
    }

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(new MoneyReadConverter()));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        coffeeService.removeAllCoffee();
//        coffeeService.addOneCoffee("espresso", 20.0);
//        coffeeService.addOneCoffee("latte", 30.0);
        coffeeService.getAllCoffee();
        coffeeService.getOneCoffee("espresso");
    }
}
