package com.vita.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vita.auth.entity.Role;
import org.apache.ibatis.annotations.Mapper;

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

}
