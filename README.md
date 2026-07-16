# PageForge

PageForge 是一个基于 Spring Boot 3 的 Maven 多模块单体项目。

仓库中的 `frontend` 目录提供配套的 Vue 3 + TypeScript 管理后台，已接入当前后端全部基础接口。

## 在线演示

- 演示地址：[http://47.99.178.186:77](http://47.99.178.186:77)
- 管理员账号：`admin`
- 初始密码：`123456`

演示账号仅用于功能体验。自行部署后请及时修改初始密码，避免继续使用默认凭据。

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
http://localhost:9090/doc.html
```

## 启动前端

后端启动后，在另一个终端执行：

```bash
cd frontend
npm install
npm run dev
```

访问 `http://localhost:5173`。开发环境会将 `/api` 请求代理到 `http://localhost:8080`；其他配置及目录说明见 `frontend/README.md`。

## 账号接口

- `POST /api/auth/login`：账号密码登录，返回 JWT、角色和功能列表。
- `POST /api/auth/register`：公开注册普通用户，并自动关联 `USER` 角色。
- `POST /api/auth/change-password`：登录用户校验旧密码后修改自己的密码。
- `POST /api/admin/system/user/reset-password`：管理员将指定用户密码重置为 `123456`。

除登录和注册外，账号接口都需要在请求头携带 `Authorization: Bearer <token>`。密码只以 BCrypt 密文写入数据库。

## OSS 接口

- `POST /api/user/oss/upload`：登录用户上传文件，使用 `multipart/form-data`，字段名为 `file`，可选目录字段为 `directory`。
- `POST /api/admin/oss/delete`：管理员删除文件，使用 JSON 传入 `objectKey`。

OSS 默认使用杭州地域和 `personal-afyke` Bucket，可通过 `ALIYUN_OSS_ENDPOINT`、`ALIYUN_OSS_BUCKET`、`ALIYUN_OSS_PUBLIC_DOMAIN` 覆盖。AccessKey 不应写入代码或配置文件，请使用 RAM 用户的最小权限密钥并通过环境变量注入。

## 权限管理接口

权限管理保持为简单的“用户 → 角色 → 功能”关系，以下接口仅允许管理员访问：

- `POST /api/admin/system/user/page`：分页查询用户和用户角色。
- `POST /api/admin/system/user/assign-roles`：使用完整角色 ID 列表覆盖用户角色。
- `POST /api/admin/system/user/reset-password`：将指定用户密码重置为 `123456`。
- `POST /api/admin/system/role/page`：分页查询角色和角色功能。
- `POST /api/admin/system/role/create`：新增角色。
- `POST /api/admin/system/role/update`：修改角色。
- `POST /api/admin/system/role/assign-functions`：使用完整功能 ID 列表覆盖角色功能。
- `POST /api/admin/system/function/page`：分页查询功能。
- `POST /api/admin/system/function/create`：新增功能。
- `POST /api/admin/system/function/update`：修改功能。

新增业务功能后，先通过功能新增接口登记 `模块:资源:操作` 格式的功能编码，再通过角色功能分配接口授权。用户角色或功能发生变化后，用户需要重新登录，以获取最新的 JWT 角色和前端功能列表。
