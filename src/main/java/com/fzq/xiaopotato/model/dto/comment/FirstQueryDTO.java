package com.fzq.xiaopotato.model.dto.comment;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;

import java.io.Serializable;

@Data
public class FirstQueryDTO implements Serializable {

    private static final long serialVersionUID = -5541839580930606860L;
    private Integer currentPage = 1;
    private Integer pageSize = 10;
    private Long postId;
}
