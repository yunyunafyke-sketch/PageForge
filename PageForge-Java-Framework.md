# PageForge Java Rapid Development Framework

## 一、项目名称

**PageForge**

中文名称：**页面工坊**

项目标识：

```text
page-forge
```

Java 包名：

```java
com.afyke.pageforge
```

## 二、项目定位

PageForge 是一个面向企业后台管理系统的 Java 快速开发框架，适合需要频繁开发管理页面、CRUD 接口、分页查询、导入导出、权限控制和操作日志等功能的项目。

框架目标是减少重复代码，通过统一的项目结构、接口规范和基础能力，提高后台业务页面的开发效率。

## 三、适用场景

PageForge 适合以下类型的项目：

- 企业后台管理系统
- 人力资源管理系统
- 外包人员管理系统
- 账号中心
- 消息中心
- 基础码表管理
- 应用管理
- 配置中心
- 审批管理
- 运营管理平台
- 内部业务中台

## 四、技术选型

建议使用以下技术栈：

```text
JDK 17 或 JDK 21
Spring Boot 3
Spring Security
JWT
MyBatis-Plus
MySQL 5.7.40
Redis
MapStruct
Hibernate Validator
Knife4j
EasyExcel
Maven
Lombok
```

各项技术的作用如下：

| 技术 | 说明 | 在 PageForge 中的主要用途 |
| --- | --- | --- |
| JDK 17 或 JDK 21 | Java 开发和运行环境，二者都是长期支持版本 | 编译、运行整个 Java 项目；新项目优先考虑 JDK 21 |
| Spring Boot 3 | 用于快速构建 Spring 应用的基础框架 | 项目启动、自动配置、依赖整合以及接口开发 |
| Spring Security | Spring 提供的认证和权限控制框架 | 用户登录、身份认证、角色权限和接口访问控制 |
| JWT | 一种携带用户身份信息的令牌格式 | 登录成功后生成 Token，后续请求通过 Token 验证身份 |
| MyBatis-Plus | 基于 MyBatis 的数据库访问增强工具 | 简化单表增删改查、分页查询和条件构造 |
| MySQL 5.7.40 | 关系型数据库 | 保存用户、角色、功能以及业务数据 |
| Redis | 基于内存的键值数据库 | 保存登录状态、缓存、验证码和临时数据 |
| MapStruct | Java 对象转换代码生成工具 | 转换 Entity、Request、VO，减少手写字段赋值代码 |
| Hibernate Validator | Java 参数校验实现 | 校验接口参数，例如必填、长度、格式和数值范围 |
| Knife4j | 基于 OpenAPI 的接口文档增强工具 | 展示、调试和管理后端 API 文档 |
| EasyExcel | Java Excel 处理工具 | 实现 Excel 数据导入和导出 |
| Maven | Java 项目构建和依赖管理工具 | 管理第三方依赖、项目模块、编译、测试和打包 |
| Lombok | Java 编译期代码生成工具 | 通过注解生成 Getter、Setter、构造方法等样板代码 |

按需使用的技术：

| 技术 | 什么时候使用 |
| --- | --- |
| MinIO | 项目需要集中存储和管理上传文件时使用；简单场景可以先使用本地文件存储 |
| Docker | 需要统一运行环境或容器化部署时使用，不影响单体架构 |

当前单体项目不引入 RabbitMQ、Elasticsearch、XXL-JOB、Nacos、Sentinel 和 Seata，避免增加不必要的开发与运维成本。简单定时任务直接使用 Spring `@Scheduled`。

## 五、项目模块设计

```text
page-forge
├── boot
├── common
├── security
├── system
└── business
```

项目初期保持少量模块即可。文件、日志和定时任务先作为功能包放在现有模块中，只有代码量明显增大时再拆成独立模块。

### 5.1 boot

项目入口，只负责：

- Spring Boot 启动类
- 项目配置文件
- 组装其他模块

### 5.2 common

存放各模块都会使用的简单公共代码：

- 统一返回结果
- 全局异常处理
- 分页对象
- 工具类
- 基础实体

### 5.3 security

负责登录和权限：

- 用户登录
- JWT Token 生成与解析
- 登录时返回用户信息、角色和功能列表
- 登录状态校验
- 接口权限校验

### 5.4 system

负责后台最基础的管理功能：

- 用户管理
- 角色管理
- 功能管理
- 菜单管理
- 数据字典

### 5.5 business

存放项目自身的业务代码，例如外包人员、消息、码表和账号等功能。

示例：

```text
business
└── src/main/java/com/afyke/pageforge/business
    ├── staff
    ├── message
    ├── code
    └── account
```

## 六、业务模块分层

每个业务统一创建以下目录：

```text
controller
service
service.impl
mapper
entity
request
vo
converter
enums
```

示例：

```text
com.afyke.pageforge.business.staff
├── controller
│   └── OutsourcedStaffController.java
├── service
│   ├── OutsourcedStaffService.java
│   └── impl
│       └── OutsourcedStaffServiceImpl.java
├── mapper
│   └── OutsourcedStaffMapper.java
├── entity
│   └── OutsourcedStaffEntity.java
├── request
│   ├── OutsourcedStaffCreateRequest.java
│   ├── OutsourcedStaffUpdateRequest.java
│   └── OutsourcedStaffPageRequest.java
├── vo
│   └── OutsourcedStaffVO.java
├── converter
│   └── OutsourcedStaffConverter.java
└── enums
    └── OutsourcedStaffStatusEnum.java
```

所有 Java 类的成员字段都必须添加中文注释，说明字段含义，Entity、Request 和 VO 也遵循此规范。

## 七、统一接口规范

所有业务接口统一使用 `POST`，Controller 只接收一个请求对象，不直接接收 `id`、`status` 等零散参数。相同的业务路径统一提取到 Controller 类上。

外包人员接口公共路径：

```http
/api/admin/outsourced-staff
```

方法路径：

```http
POST /page
POST /detail
POST /create
POST /update
POST /delete
POST /batch-delete
POST /change-status
POST /import
POST /export
```

| 操作 | 接口 | 请求对象 |
| --- | --- | --- |
| 分页查询 | `/page` | `OutsourcedStaffPageRequest` |
| 查询详情 | `/detail` | `IdRequest` |
| 新增数据 | `/create` | `OutsourcedStaffCreateRequest` |
| 修改数据 | `/update` | `OutsourcedStaffUpdateRequest` |
| 删除数据 | `/delete` | `IdRequest` |
| 批量删除 | `/batch-delete` | `IdsRequest` |
| 启用禁用 | `/change-status` | `OutsourcedStaffStatusRequest` |
| Excel 导入 | `/import` | `OutsourcedStaffImportRequest` |
| Excel 导出 | `/export` | `OutsourcedStaffExportRequest` |

### 7.1 分页查询

```http
POST /page
```

请求对象示例：

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "name": "张三",
  "status": 1
}
```

### 7.2 查询详情

```http
POST /detail
```

```json
{
  "id": 1
}
```

### 7.3 新增数据

```http
POST /create
```

### 7.4 修改数据

```http
POST /update
```

### 7.5 删除数据

```http
POST /delete
```

### 7.6 批量删除

```http
POST /batch-delete
```

### 7.7 启用禁用

```http
POST /change-status
```

### 7.8 Excel 导入

```http
POST /import
```

### 7.9 Excel 导出

```http
POST /export
```

## 八、统一返回结果

建议在 `common` 模块的 `com.afyke.pageforge.common.model` 包中提供统一响应模型：

```java
package com.afyke.pageforge.common.model;

import lombok.Data;

@Data
public class ResultModel<T> {

    /** 状态码。 */
    private Integer status;

    /** 是否成功。 */
    private boolean success;

    /** 错误码。 */
    private String errorCode;

    /** 错误信息。 */
    private String errorMessage;

    /** 返回数据。 */
    private T data;

    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> result = new ResultModel<>();
        result.setStatus(200);
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static <T> ResultModel<T> failure(
            Integer status, String errorCode, String errorMessage) {
        ResultModel<T> result = new ResultModel<>();
        result.setStatus(status);
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
```

更新、删除、状态修改和权限分配接口统一返回实际的 `Boolean` 业务结果，不使用 `ResultModel<Void>`，也不通过无参数方法伪造成功数据。

成功返回示例：

```json
{
  "status": 200,
  "success": true,
  "errorCode": null,
  "errorMessage": null,
  "data": {
    "id": 1,
    "name": "张三"
  }
}
```

失败返回示例：

```json
{
  "status": 400,
  "success": false,
  "errorCode": "STAFF_NOT_FOUND",
  "errorMessage": "外包人员不存在",
  "data": null
}
```

## 九、统一分页结构

```java
@Data
public class PageResult<T> {

    /** 数据总数。 */
    private Long total;

    /** 当前页数据列表。 */
    private List<T> records;

    /** 当前页码。 */
    private Long pageNum;

    /** 每页数据条数。 */
    private Long pageSize;
}
```

返回示例：

```json
{
  "status": 200,
  "success": true,
  "errorCode": null,
  "errorMessage": null,
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "records": []
  }
}
```

## 十、基础实体设计

```java
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /** 修改时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /** 创建者 ID。 */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /** 修改者 ID。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String modifier;

    /** 有效标识。 */
    @TableField(fill = FieldFill.INSERT)
    private Integer isValid;
}
```

基础表统一以下字段：

```text
gmt_create
gmt_modified
creator
modifier
is_valid
```

## 十一、通用功能清单

PageForge 应统一提供以下能力：

- 分页查询
- 条件查询
- 详情查询
- 新增
- 修改
- 删除
- 逻辑删除
- 批量删除
- 启用禁用
- Excel 导入
- Excel 导出
- 数据字典转换
- 参数校验
- 全局异常处理
- 统一返回结果
- 操作日志
- 登录日志
- 用户管理
- 角色管理
- 菜单管理
- 功能管理
- 文件上传
- 文件下载
- 接口文档
- 防重复提交
- 接口限流
- 幂等控制
- 数据脱敏
- 敏感字段加密

## 十二、推荐开发流程

新增一个后台页面时，建议按照以下流程开发：

1. 创建数据库表
2. 创建业务目录
3. 编写 Entity 和 Mapper
4. 编写 Service 和 Controller
5. 编写 Request 和 VO
6. 编写业务校验
7. 配置角色功能
8. 增加操作日志
9. 增加导入导出
10. 编写接口测试
11. 完成前后端联调

## 十三、用户、角色和功能设计

### 13.1 数据关系

权限只保留三层关系：

```text
用户 ↔ 角色 ↔ 功能
```

- 每个用户可以关联多个角色
- 基础角色为 `ADMIN`（管理员）和 `USER`（普通用户），后续可以增加 `VIP_USER`、`TECH_MANAGER` 等角色
- 每个角色关联可以使用的功能
- 用户最终拥有的功能是所有角色功能的并集
- 登录时返回角色列表和功能列表
- 后续请求通过 Token 得到当前用户和全部角色

基础数据表：

| 表名 | 用途 |
| --- | --- |
| `sys_user` | 保存用户基本信息 |
| `sys_role` | 保存 `ADMIN`、`USER` 等角色 |
| `sys_user_role` | 保存用户与角色的关系 |
| `sys_function` | 保存系统功能 |
| `sys_role_function` | 保存角色与功能的关系 |

功能编码统一使用 `模块:资源:操作` 格式，例如：

```text
staff:outsourced:list
staff:outsourced:create
staff:outsourced:update
staff:outsourced:delete
```

### 13.2 登录接口

公共路径：

```http
/api/auth
```

登录方法：

```http
POST /login
```

请求对象：

```java
@Data
public class LoginRequest {

    /** 登录用户名。 */
    private String username;

    /** 登录密码。 */
    private String password;
}
```

登录返回对象：

```java
@Data
public class LoginVO {

    /** 登录凭证。 */
    private String token;

    /** 用户 ID。 */
    private Long userId;

    /** 用户名。 */
    private String username;

    /** 当前用户拥有的角色列表。 */
    private List<String> roles;

    /** 当前角色可以使用的功能列表。 */
    private List<String> functions;
}
```

返回示例：

```json
{
  "status": 200,
  "success": true,
  "errorCode": null,
  "errorMessage": null,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "roles": [
      "TECH_MANAGER",
      "USER",
      "VIP_USER"
    ],
    "functions": [
      "staff:outsourced:list",
      "staff:outsourced:create",
      "staff:outsourced:update",
      "staff:outsourced:delete"
    ]
  }
}
```

登录 Controller 示例：

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    /** 登录服务。 */
    private final LoginService loginService;

    @PostMapping("/login")
    public ResultModel<LoginVO> login(
            @Valid @RequestBody LoginRequest request) {
        return ResultModel.success(loginService.login(request));
    }
}
```

### 13.3 注册与密码管理

- `POST /api/auth/register`：公开注册账号，系统自动关联 `USER` 普通用户角色。
- `POST /api/auth/change-password`：登录用户提交旧密码和新密码，修改自己的密码。
- `POST /api/admin/system/user/reset-password`：管理员按用户 ID 将密码重置为 `123456`。

注册和修改密码时，数据库只保存 BCrypt 密文。修改自己的密码不接收用户 ID，而是直接从 JWT 登录信息中取得当前用户，避免修改他人密码。

### 13.4 接口权限校验

JWT 中只保存 `userId` 和 `roles`。每次请求由 JWT 过滤器解析 Token，并将全部角色放入 Spring Security。接口只按路径区分管理员和普通用户，不再配置每个功能对应的接口。

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST,
                    "/api/auth/login", "/api/auth/register")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/public/**")
                .permitAll()
                .requestMatchers("/api/admin/**")
                .hasAuthority("ADMIN")
                .requestMatchers("/api/user/**")
                .hasAnyAuthority("ADMIN", "USER")
                .anyRequest().denyAll()
            );
        return http.build();
    }
}
```

前端使用登录返回的 `functions` 控制菜单和按钮。后端只负责校验 Token 和角色：管理员接口统一放在 `/api/admin/**`，普通用户接口统一放在 `/api/user/**`。

Controller 示例：

```java
@RestController
@RequestMapping("/api/admin/outsourced-staff")
@RequiredArgsConstructor
public class OutsourcedStaffController {

    /** 外包人员业务服务。 */
    private final OutsourcedStaffService outsourcedStaffService;

    @PostMapping("/page")
    public ResultModel<PageResult<OutsourcedStaffVO>> page(
            @Valid @RequestBody OutsourcedStaffPageRequest request) {
        return ResultModel.success(outsourcedStaffService.page(request));
    }
}
```

### 13.5 权限管理入口

管理员通过 `/api/admin/system/**` 维护权限，不需要直接修改关联表。系统提供用户、角色和功能分页查询，支持新增或修改角色与功能，并通过以下两个接口覆盖关联关系：

- `POST /api/admin/system/user/assign-roles`：给用户分配多个角色。
- `POST /api/admin/system/user/reset-password`：将指定用户密码重置为 `123456`。
- `POST /api/admin/system/role/assign-functions`：给角色分配多个功能。

关联接口接收完整 ID 列表，空列表表示清空关联。权限变化后用户需要重新登录，以获取最新的 JWT 角色和前端功能列表。

## 十四、操作日志规范

建议通过注解记录操作日志：

```java
@OperationLog(
    module = "外包人员管理",
    operation = "新增外包人员"
)
@PostMapping("/create")
public ResultModel<Long> create(
        @Valid @RequestBody OutsourcedStaffCreateRequest request) {
    return ResultModel.success(outsourcedStaffService.create(request));
}
```

日志记录内容：

```text
操作模块
操作类型
请求方法
请求地址
请求参数
返回结果
操作人员
操作时间
执行耗时
客户端 IP
执行状态
异常信息
```

## 十五、异常处理设计

建议统一异常类型：

```text
BusinessException
ValidationException
AuthenticationException
AuthorizationException
DataNotFoundException
DuplicateDataException
RemoteCallException
FileException
```

全局异常处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultModel<Object> handleBusinessException(
            BusinessException exception) {
        return ResultModel.failure(
                400,
                exception.getErrorCode(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResultModel<Object> handleException(Exception exception) {
        return ResultModel.failure(
                500,
                "SYSTEM_ERROR",
                "系统异常"
        );
    }
}
```

## 十六、项目配置建议

```yaml
server:
  port: ${SERVER_PORT:8080}

spring:
  application:
    name: ${APP_NAME:page-forge}

  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/page_forge}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: isValid
      logic-delete-value: 0
      logic-not-delete-value: 1

knife4j:
  enable: ${KNIFE4J_ENABLE:true}
```

`${变量名:默认值}` 表示优先读取环境变量，未配置时使用默认值。数据库用户名和密码不设置默认值，启动项目时必须通过环境变量提供：

```text
DB_USERNAME
DB_PASSWORD
```

生产环境中的数据库密码、Redis 密码、JWT 密钥等敏感信息不得写入代码仓库，应通过环境变量或配置中心注入。

## 十七、数据库命名规范

### 17.1 基础 SQL 模板

项目基础建表和初始化脚本：[`sql/page_forge_base.sql`](./sql/page_forge_base.sql)

脚本包含以下内容：

- 创建 `page_forge` 数据库
- 创建用户、角色、用户角色关系、功能、角色功能关系表
- 初始化 `ADMIN` 和 `USER` 角色
- 初始化普通用户和外包人员管理功能
- 将全部基础功能分配给管理员角色
- 提供管理员账号初始化模板，不写入明文密码

### 17.2 命名规则

表名使用小写下划线格式：

```text
sys_user
sys_role
sys_user_role
sys_function
sys_role_function
sys_menu
sys_department
sys_dict
sys_operation_log
biz_outsourced_staff
biz_message
biz_application
```

字段名示例：

```text
id
name
status
creator
modifier
gmt_create
gmt_modified
is_valid
```

## 十八、项目介绍

英文介绍：

> PageForge is a Java rapid development framework designed for enterprise administration systems. It provides unified CRUD APIs, authentication, authorization, operation logs, data dictionaries, file management, and Excel import and export capabilities.

中文介绍：

> PageForge 是一个面向企业后台管理系统的 Java 快速开发框架，提供统一 CRUD、权限认证、数据字典、操作日志、文件管理及 Excel 导入导出能力。

## 十九、项目口号

```text
Build pages faster, keep business clean.
```

中文：

```text
快速构建页面，保持业务清晰。
```
