package com.vita.common.domain.user;

import com.vita.common.domain.role.RoleBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Edward
 * @date 2024-03-19 21:13
 */
@Data
@Accessors(chain = true)
public class UserInfoVO {

    private Long userId;

    private String avatar;

    private String username;

    private String phone;

    private String email;

    private List<RoleBO> roles;
}
