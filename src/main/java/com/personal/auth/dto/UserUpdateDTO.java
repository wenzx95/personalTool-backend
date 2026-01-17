package com.personal.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;

/**
 * 用户更新数据传输对象
 *
 * @author tendollar
 * @since 2026-01-11
 */
@Data
public class UserUpdateDTO {

    /**
     * 昵称
     */
    @Size(min = 2, max = 20, message = "昵称长度必须在2到20个字符之间")
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单权限ID列表（逗号分隔）
     */
    private String menuIds;
}
