package com.fzq.tapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.tapibackend.model.entity.Interfaceinfo;
import com.fzq.tapibackend.service.InterfaceinfoService;
import com.fzq.tapibackend.mapper.InterfaceinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author zfeng
* @description 针对表【InterfaceInfo(interface information)】的数据库操作Service实现
* @createDate 2024-09-11 18:07:57
*/
@Service
public class InterfaceinfoServiceImpl extends ServiceImpl<InterfaceinfoMapper, Interfaceinfo>
    implements InterfaceinfoService{

    @Autowired
    private InterfaceinfoMapper interfaceinfoMapper;

    @Override
    public List<Interfaceinfo> getAllInterface() {
        return interfaceinfoMapper.getAllInterface();  // 使用 MyBatis 查询所有数据
    }


}




