package com.afyke.pageforge.system.mapper;

import com.afyke.pageforge.system.entity.RoleFunctionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 角色功能关系 Mapper。 */
@Mapper
public interface RoleFunctionMapper extends BaseMapper<RoleFunctionEntity> {

    /**
     * 新增或重新启用角色功能关系。
     * 唯一索引已存在时恢复原关系，避免逻辑删除后重复插入失败。
     */
    @Insert("""
            INSERT INTO sys_role_function
                (role_id, function_id, gmt_create, gmt_modified, creator, modifier, is_valid)
            VALUES
                (#{roleId}, #{functionId}, NOW(), NOW(), 'system', 'system', 1)
            ON DUPLICATE KEY UPDATE
                gmt_modified = NOW(), modifier = 'system', is_valid = 1
            """)
    int enableRelation(
            @Param("roleId") Long roleId,
            @Param("functionId") Long functionId);
}
