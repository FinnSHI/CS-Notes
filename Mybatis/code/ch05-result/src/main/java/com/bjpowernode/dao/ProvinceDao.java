package com.bjpowernode.dao;

import com.bjpowernode.vo.ProvinceCity;

import java.util.List;

/**
 *
 */
public interface ProvinceDao {

    List<ProvinceCity> selectProvinceCityList(Integer provinceId);
}
