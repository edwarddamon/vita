package com.vita.auth.converter;

import com.vita.auth.entity.Role;
import com.vita.common.domain.user.RoleBO;
import com.vita.common.domain.user.RoleListVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleConverter {
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);

    RoleBO convertDoToBo(Role role);

    RoleListVO convertDoToVo(Role role);
}
