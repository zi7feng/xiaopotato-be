package com.fzq.tapibackend.controller;

import com.fzq.tapibackend.model.entity.Interfaceinfo;
import com.fzq.tapibackend.service.InterfaceinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/interfaceinfo")
public class InterfaceInfoController {

    @Autowired
    private InterfaceinfoService interfaceinfoService;

    @GetMapping("/all")
    public List<Interfaceinfo> selectAll() {
        return interfaceinfoService.getAllInterface();
    }
}
