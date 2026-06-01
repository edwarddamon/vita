package com.vita.auth.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vita.auth.entity.Role;
import com.vita.common.domain.role.CreateRoleDTO;
import com.vita.common.domain.role.EditRoleDTO;
import com.vita.common.domain.role.ListRoleDTO;
import com.vita.common.domain.role.RoleAssociateDTO;

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

    void associateRole(RoleAssociateDTO roleAssociateDTO);

    IPage<Role> listRole(Page<Role> page, ListRoleDTO listRoleDTO);

    void createRole(CreateRoleDTO createRoleDTO);

    void updateRole(EditRoleDTO editRoleDTO);

    void deleteRole(Long id);
}
