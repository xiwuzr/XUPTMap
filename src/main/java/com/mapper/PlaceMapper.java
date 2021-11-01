package com.mapper;

import com.pojo.Place;

import java.util.List;

public interface PlaceMapper {
    int insPlace(String name, String msg);
    int delete(String name);

    List<Place> selAll(int start, int pageSize);
    //返回所有的地点数组
    List<Place> SelList();
    //返回所有的地点数量
    int selCount();
    Place selByName(String name);
}
