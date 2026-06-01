package com.vita.auth.converter;

import cn.hutool.core.convert.Convert;
import com.vita.auth.entity.User;
import com.vita.common.domain.user.ListUserInfoVO;
import com.vita.common.domain.user.UserDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    ListUserInfoVO convertDoToVo(User user);

    UserDetailVO convertDoToDetailVo(User user);

    @Named("longToString")
    default String longToString(Long id) {
        return id == null ? null : Convert.toStr(id);
    }
}
