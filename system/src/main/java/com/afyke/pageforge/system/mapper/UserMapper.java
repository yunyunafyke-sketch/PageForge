package com.afyke.pageforge.system.mapper;

import com.afyke.pageforge.system.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/** 系统用户 Mapper。 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    /** 查询用户名是否已经存在，包含被逻辑删除的账号。 */
    @Select("SELECT COUNT(1) FROM sys_user WHERE username = #{username}")
    long countByUsername(@Param("username") String username);
}
