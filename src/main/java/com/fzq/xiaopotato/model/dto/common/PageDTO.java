package com.fzq.xiaopotato.model.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageDTO implements Serializable {
    private static final long serialVersionUID = -860411473338993839L;
    private Integer currentPage = 1;
    private Integer pageSize = 10;
}
