package com.vita.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vita.auth.entity.User;
import com.vita.common.domain.user.InterfaceInfoBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询用户拥有的需要认证的接口权限
     *
     * @param userId 用户Id
     * @return 接口列表
     */
    Set<InterfaceInfoBO> describeUserInterfaces(@Param("userId") Long userId);
}
