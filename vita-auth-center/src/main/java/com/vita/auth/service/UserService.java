package com.vita.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.vita.auth.entity.User;
import com.vita.common.domain.user.InterfaceInfoBO;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
public interface UserService extends IService<User> {

    List<InterfaceInfoBO> describeInterfaces(Long userId);
}
