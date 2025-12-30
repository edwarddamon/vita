package com.vita.common.domain.user;

import com.vita.common.domain.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListUserInfoDTO extends BasePageDTO {

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
}
