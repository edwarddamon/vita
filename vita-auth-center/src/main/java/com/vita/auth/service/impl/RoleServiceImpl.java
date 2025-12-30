package com.vita.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vita.auth.entity.Role;
import com.vita.auth.entity.UserRoleRel;
import com.vita.auth.mapper.RoleMapper;
import com.vita.auth.mapper.UserRoleRelMapper;
import com.vita.auth.service.RoleService;
import com.vita.common.domain.role.RoleAssociateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IService<Role>, RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleRelMapper userRoleRelMapper;

    @Override
    public List<Role> describeUserRoles(Long id) {
        return roleMapper.describeUserRoles(id);
    }

    @Override
    public void associateRole(RoleAssociateDTO roleAssociateDTO) {
        // 删除用户关联的角色信息
        userRoleRelMapper.deleteUserRoleRel(roleAssociateDTO.getUserId());

        // 更新用户关联的角色信息
        if (CollectionUtils.isEmpty(roleAssociateDTO.getRoleIds())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<UserRoleRel> associateList = roleAssociateDTO.getRoleIds().stream().map(roleId -> new UserRoleRel()
                .setUserId(roleAssociateDTO.getUserId())
                .setRoleId(roleId)
                .setCreateTime(now)
                .setUpdateTime(now)).collect(Collectors.toList());
        userRoleRelMapper.insertBatch(associateList);
    }
}
