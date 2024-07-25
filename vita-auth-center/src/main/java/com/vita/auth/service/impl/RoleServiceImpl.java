package com.vita.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vita.auth.entity.Role;
import com.vita.auth.mapper.RoleMapper;
import com.vita.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Role> describeUserRoles(Long id) {
        return roleMapper.describeUserRoles(id);
    }
}
