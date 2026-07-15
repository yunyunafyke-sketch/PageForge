package com.afyke.pageforge.system.mapper;

import com.afyke.pageforge.system.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** 登录认证查询 Mapper。 */
@Mapper
public interface SystemAuthMapper {

    @Select("""
            SELECT id, username, password, nickname, status
            FROM sys_user
            WHERE username = #{username} AND is_valid = 1
            LIMIT 1
            """)
    UserEntity findUserByUsername(@Param("username") String username);

    @Select("""
            SELECT DISTINCT role_data.role_code
            FROM sys_user_role user_role
            JOIN sys_role role_data ON role_data.id = user_role.role_id
            WHERE user_role.user_id = #{userId}
              AND user_role.is_valid = 1
              AND role_data.is_valid = 1
            ORDER BY role_data.role_code
            """)
    List<String> findRoleCodes(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT function_data.function_code
            FROM sys_user_role user_role
            JOIN sys_role role_data ON role_data.id = user_role.role_id
            JOIN sys_role_function role_function ON role_function.role_id = role_data.id
            JOIN sys_function function_data ON function_data.id = role_function.function_id
            WHERE user_role.user_id = #{userId}
              AND user_role.is_valid = 1
              AND role_data.is_valid = 1
              AND role_function.is_valid = 1
              AND function_data.is_valid = 1
            ORDER BY function_data.function_code
            """)
    List<String> findFunctionCodes(@Param("userId") Long userId);
}
