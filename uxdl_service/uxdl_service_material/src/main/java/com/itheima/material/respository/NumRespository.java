package com.itheima.material.respository;

import com.itheima.material.pojo.NumInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NumRespository extends MongoRepository<NumInfo,String> {
}
