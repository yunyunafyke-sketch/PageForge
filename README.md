# PageForge

PageForge 是一个基于 Spring Boot 3 的 Maven 多模块单体项目。

## 环境要求

- JDK 17
- Maven 3.9+
- MySQL 5.7.40
- Redis（未使用缓存功能时可以暂不启动）

## 初始化数据库

执行：

```text
sql/page_forge_base.sql
```

脚本不会写入默认明文密码。请生成 BCrypt 密文后，按照脚本末尾的管理员账号模板初始化账号。

## 环境变量

启动前至少需要设置：

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
ALIYUN_OSS_ACCESS_KEY_ID
ALIYUN_OSS_ACCESS_KEY_SECRET
```

`JWT_SECRET` 长度不得少于 32 个字节。

## 启动项目

```bash
export JAVA_HOME=/Volumes/data/develop/JDK/jdk17/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
export DB_USERNAME=root
export DB_PASSWORD=your-password
export JWT_SECRET=please-change-this-secret-at-least-32-bytes
export ALIYUN_OSS_ACCESS_KEY_ID=your-access-key-id
export ALIYUN_OSS_ACCESS_KEY_SECRET=your-access-key-secret
mvn clean package -DskipTests
java -jar boot/target/boot-1.0.0-SNAPSHOT.jar
```

启动后访问 Knife4j：

```text
http://localhost:8080/doc.html
```

## OSS 接口

- `POST /api/user/oss/upload`：登录用户上传文件，使用 `multipart/form-data`，字段名为 `file`，可选目录字段为 `directory`。
- `POST /api/admin/oss/delete`：管理员删除文件，使用 JSON 传入 `objectKey`。

OSS 默认使用杭州地域和 `personal-afyke` Bucket，可通过 `ALIYUN_OSS_ENDPOINT`、`ALIYUN_OSS_BUCKET`、`ALIYUN_OSS_PUBLIC_DOMAIN` 覆盖。AccessKey 不应写入代码或配置文件，请使用 RAM 用户的最小权限密钥并通过环境变量注入。
