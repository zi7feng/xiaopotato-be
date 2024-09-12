package com.fzq.tapibackend.mapper;

import com.fzq.tapibackend.model.entity.Interfaceinfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zfeng
* @description 针对表【InterfaceInfo(interface information)】的数据库操作Mapper
* @createDate 2024-09-11 18:07:57
* @Entity com.fzq.tapibackend.model.entity.Interfaceinfo
*/
public interface InterfaceinfoMapper extends BaseMapper<Interfaceinfo> {

    @Select("SELECT * FROM InterfaceInfo")
    List<Interfaceinfo> getAllInterface();
}




