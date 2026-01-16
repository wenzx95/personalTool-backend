package com.personal.system.data.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user_data")
public class UserDataEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("module_code")
    private String moduleCode;

    @TableField("data_type")
    private String dataType;

    @TableField("name")
    private String name;

    @TableField("data")
    private String data;

    @TableField("remarks")
    private String remarks;

    @TableField("sort")
    private Integer sort;

    @TableField("status")
    private Integer status;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
