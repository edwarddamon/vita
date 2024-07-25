package com.vita.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.vita.auth.entity.Role;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
public interface RoleService extends IService<Role> {

    List<Role> describeUserRoles(Long id);
}
