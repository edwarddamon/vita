package com.vita.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vita.auth.entity.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}
