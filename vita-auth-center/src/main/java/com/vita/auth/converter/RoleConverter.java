package com.vita.auth.converter;

import cn.hutool.core.convert.Convert;
import com.vita.auth.entity.Role;
import com.vita.common.domain.role.RoleBO;
import com.vita.common.domain.role.RoleListVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleConverter {
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    RoleBO convertDoToBo(Role role);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    RoleListVO convertDoToVo(Role role);

    @Named("longToString")
    default String longToString(Long id) {
        return id == null ? null : Convert.toStr(id);
    }
}
