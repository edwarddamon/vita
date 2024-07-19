package com.vita.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vita.auth.entity.UserRoleRel;
import com.vita.auth.mapper.UserRoleRelMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-角色关联表 服务实现类
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
@Service
public class UserRoleRelServiceImpl extends ServiceImpl<UserRoleRelMapper, UserRoleRel> implements IService<UserRoleRel> {

}
