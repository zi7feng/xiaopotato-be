package com.fzq.xiaopotato.model.dto.comment;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;

import java.io.Serializable;

@Data
public class SecondQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer currentPage = 1;
    private Integer pageSize = 10;
    private Long commentId;
}
