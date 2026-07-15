package com.afyke.pageforge.system.mapper;

import com.afyke.pageforge.system.entity.UserRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 用户角色关系 Mapper。 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    /**
     * 新增或重新启用用户角色关系。
     * 唯一索引已存在时不能再次插入，因此通过 ON DUPLICATE KEY 恢复逻辑删除的数据。
     */
    @Insert("""
            INSERT INTO sys_user_role
                (user_id, role_id, gmt_create, gmt_modified, creator, modifier, is_valid)
            VALUES
                (#{userId}, #{roleId}, NOW(), NOW(), 'system', 'system', 1)
            ON DUPLICATE KEY UPDATE
                gmt_modified = NOW(), modifier = 'system', is_valid = 1
            """)
    int enableRelation(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
