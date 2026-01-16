package com.personal.system.data.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDataDTO {
    private Long id;
    private String name;
    private Object data;
    private String remarks;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
