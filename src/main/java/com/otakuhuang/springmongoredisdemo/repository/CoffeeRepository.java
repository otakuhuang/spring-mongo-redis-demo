package com.otakuhuang.springmongoredisdemo.repository;

import com.otakuhuang.springmongoredisdemo.model.Coffee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoffeeRepository extends MongoRepository<Coffee, Long> {
}
