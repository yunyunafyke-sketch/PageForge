package com.afyke.pageforge.system.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/** 基础字段自动填充处理器。 */
@Component
public class PageForgeMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        strictInsertFill(metaObject, "gmtCreate", Date.class, now);
        strictInsertFill(metaObject, "gmtModified", Date.class, now);
        strictInsertFill(metaObject, "creator", String.class, "system");
        strictInsertFill(metaObject, "modifier", String.class, "system");
        strictInsertFill(metaObject, "isValid", Integer.class, 1);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "gmtModified", Date.class, new Date());
        strictUpdateFill(metaObject, "modifier", String.class, "system");
    }
}
