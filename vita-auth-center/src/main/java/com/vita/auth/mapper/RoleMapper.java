package com.vita.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vita.auth.entity.Role;
import com.vita.common.domain.role.ListRoleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> describeUserRoles(@Param("userId") Long id);

    IPage<Role> listRole(Page<Role> page, @Param("listRoleDTO") ListRoleDTO listRoleDTO);
}
