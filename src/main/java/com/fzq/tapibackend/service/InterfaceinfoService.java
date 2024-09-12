package com.fzq.tapibackend.service;

import com.fzq.tapibackend.model.entity.Interfaceinfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
* @author zfeng
* @description 针对表【InterfaceInfo(interface information)】的数据库操作Service
* @createDate 2024-09-11 18:07:57
*/
public interface InterfaceinfoService extends IService<Interfaceinfo> {

    List<Interfaceinfo> getAllInterface();
}
