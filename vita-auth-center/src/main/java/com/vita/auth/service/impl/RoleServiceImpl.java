package com.vita.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vita.auth.entity.Role;
import com.vita.auth.entity.UserRoleRel;
import com.vita.auth.mapper.RoleMapper;
import com.vita.auth.mapper.UserRoleRelMapper;
import com.vita.auth.service.RoleService;
import com.vita.common.domain.common.enums.DelFlagEnum;
import com.vita.common.domain.role.CreateRoleDTO;
import com.vita.common.domain.role.EditRoleDTO;
import com.vita.common.domain.role.ListRoleDTO;
import com.vita.common.domain.role.RoleAssociateDTO;
import com.vita.common.utils.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

    @Override
    public IPage<Role> listRole(Page<Role> page, ListRoleDTO listRoleDTO) {
        return roleMapper.listRole(page, listRoleDTO);
    }

    @Override
    public void createRole(CreateRoleDTO createRoleDTO) {
        AssertUtil.isTrue(!existsRoleCode(createRoleDTO.getCode(), null), "角色code已存在");

        LocalDateTime now = LocalDateTime.now();
        Role role = new Role();
        role.setCode(createRoleDTO.getCode());
        role.setName(createRoleDTO.getName());
        role.setDataAuth(createRoleDTO.getDataAuth());
        role.setDelFlag(DelFlagEnum.NOT_DELETED.getFlag());
        role.setCreateTime(now);
        role.setUpdateTime(now);
        AssertUtil.isTrue(save(role), "新增角色失败");
    }

    @Override
    public void updateRole(EditRoleDTO editRoleDTO) {
        Role role = getValidRole(editRoleDTO.getId());
        AssertUtil.isTrue(!existsRoleCode(editRoleDTO.getCode(), editRoleDTO.getId()), "角色code已存在");

        role.setCode(editRoleDTO.getCode());
        role.setName(editRoleDTO.getName());
        role.setDataAuth(editRoleDTO.getDataAuth());
        role.setUpdateTime(LocalDateTime.now());
        AssertUtil.isTrue(updateById(role), "编辑角色失败");
    }

    @Override
    public void deleteRole(Long id) {
        Role role = getValidRole(id);
        role.setDelFlag(DelFlagEnum.DELETED.getFlag());
        role.setUpdateTime(LocalDateTime.now());
        AssertUtil.isTrue(updateById(role), "删除角色失败");
    }

    private Role getValidRole(Long id) {
        Role role = getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getId, id)
                .eq(Role::getDelFlag, DelFlagEnum.NOT_DELETED.getFlag()));
        AssertUtil.notNullObject(role, "角色不存在");
        return role;
    }

    private boolean existsRoleCode(String code, Long excludeId) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, code)
                .eq(Role::getDelFlag, DelFlagEnum.NOT_DELETED.getFlag());
        if (Objects.nonNull(excludeId)) {
            wrapper.ne(Role::getId, excludeId);
        }
        return count(wrapper) > 0;
    }
}
