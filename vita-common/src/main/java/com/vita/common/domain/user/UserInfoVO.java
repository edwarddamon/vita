package com.vita.common.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Edward
 * @date 2024-03-19 21:13
 */
@Data
@Accessors(chain = true)
public class UserInfoVO {

    private String avatar;

    private String username;

    private String phone;

    private String email;

}
