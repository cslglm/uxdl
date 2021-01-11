package com.itheima.user.dao;

import com.itheima.material.pojo.Favourite;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface FavouriteMapper extends Mapper<Favourite> {

}
