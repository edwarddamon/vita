package com.vita.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vita.auth.entity.UserRoleRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户-角色关联表 Mapper 接口
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
@Mapper
public interface UserRoleRelMapper extends BaseMapper<UserRoleRel> {

    void deleteUserRoleRel(@Param("userId") Long userId);

    void insertBatch(@Param("associateList") List<UserRoleRel> associateList);
}
