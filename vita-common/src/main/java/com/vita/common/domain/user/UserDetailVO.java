package com.vita.common.domain.user;

import com.vita.common.domain.role.RoleBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户详情（查库）
 */
@Data
@Accessors(chain = true)
public class UserDetailVO {

    private Long id;

    private String avatar;

    private String username;

    private String phone;

    private String email;

    private Integer delFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<RoleBO> roles;
}
