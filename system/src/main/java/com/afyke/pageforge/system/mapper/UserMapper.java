package com.afyke.pageforge.system.mapper;

import com.afyke.pageforge.system.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/** 系统用户 Mapper。 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
