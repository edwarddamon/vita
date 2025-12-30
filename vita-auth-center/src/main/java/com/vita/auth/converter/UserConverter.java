package com.vita.auth.converter;

import com.vita.auth.entity.User;
import com.vita.common.domain.user.ListUserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);


    ListUserInfoVO convertDoToVo(User user);
}
