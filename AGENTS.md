# PageForge Codex 工作约定

## 项目定位

PageForge（页面工坊）是面向企业后台管理系统的 Java 快速开发框架，重点提供统一 CRUD、分页查询、认证与权限、操作日志、数据字典、文件管理以及 Excel 导入导出能力。

- Java 包名前缀：`com.afyke.pageforge`
- 架构：Maven 多模块单体应用
- 基线环境：JDK 17、Maven 3.9+、Spring Boot 3
- 主要技术：Spring Security、JWT、MyBatis-Plus、MySQL 5.7.40、Redis、MapStruct、Hibernate Validator、Knife4j、EasyExcel、Lombok
- 架构和业务规范的完整来源：`PageForge-Java-Framework.md`

除非需求明确要求，不引入 RabbitMQ、Elasticsearch、XXL-JOB、Nacos、Sentinel 或 Seata。简单定时任务使用 Spring `@Scheduled`，避免无必要地增加模块和基础设施。

## 模块职责

- `boot`：启动类、配置文件以及其他模块的组装，不放业务实现。
- `common`：统一响应、分页模型、通用请求、基础实体、全局异常和简单公共工具。
- `security`：登录、JWT 生成与解析、登录状态及接口角色校验。
- `system`：用户、角色、功能、菜单和数据字典等系统基础能力。
- `business`：项目自身的业务功能；每个业务使用独立包，避免跨业务耦合。
- `sql`：数据库建表与初始化脚本。

新增代码应放入职责最匹配的现有模块。只有某项能力的规模明显增长时才考虑拆分新模块。

## 业务包结构

业务功能统一放在 `com.afyke.pageforge.business.<业务名>` 下，并按需使用以下分层：

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

- Controller 只负责参数接收、校验、调用 Service 和封装统一结果，不承载业务逻辑。
- Service 负责业务规则和事务边界；涉及多步写操作时使用 `@Transactional(rollbackFor = Exception.class)`。
- Mapper 负责数据库访问，优先使用 MyBatis-Plus 的类型安全能力。
- Entity、Request、VO 职责分离，不直接把 Entity 暴露为接口响应。
- Entity、Request、VO 之间优先使用 MapStruct 转换，避免重复手写字段复制。
- 枚举用于表达有限且稳定的业务状态，避免散落魔法值。

## Java 编码规范

- 所有 Java 类的成员字段必须添加中文注释，清楚说明字段含义；Entity、Request、VO、配置类和依赖字段同样遵守。
- 新增公共类时添加简洁的中文类注释。
- 每次生成或修改 Java 代码时，必须为核心方法、关键业务判断、认证权限流程、异常处理以及容易误解的配置添加必要的中文关键注释。
- 注释重点说明代码的业务目的、处理原因和重要约束，不给简单赋值、普通 getter/setter 或一眼可读的代码堆砌无意义注释。
- 使用构造器注入，优先采用 Lombok `@RequiredArgsConstructor`，不要使用字段注入。
- 请求参数使用 Jakarta Validation 注解，并在 Controller 请求对象上添加 `@Valid`。
- 可预期的业务错误抛出 `BusinessException` 等明确异常，由全局异常处理器转换为 `ResultModel`；不要在 Controller 中吞异常或自行拼装错误响应。
- 保持现有代码风格和命名，不进行与当前任务无关的大范围格式化或重构。

## 接口规范

- 业务接口统一使用 `POST`。
- Controller 类上提取公共路径；管理员接口放在 `/api/admin/**`，普通用户接口放在 `/api/user/**`，认证接口放在 `/api/auth/**`。
- Controller 方法只接收一个请求对象，不直接接收零散的 `id`、`status` 等参数。
- 通用单 ID 和多 ID 请求分别复用 `IdRequest`、`IdsRequest`。
- 标准业务方法路径优先使用：`/page`、`/detail`、`/create`、`/update`、`/delete`、`/batch-delete`、`/change-status`、`/import`、`/export`。
- 所有接口统一返回 `ResultModel<T>`；分页接口返回 `ResultModel<PageResult<T>>`，不得另造响应格式。
- 分页请求继承或复用公共分页请求结构，页码从 1 开始。

## 权限与安全

- 权限关系保持为“用户 ↔ 角色 ↔ 功能”。用户最终功能为其全部角色功能的并集。
- 功能编码使用 `模块:资源:操作`，例如 `staff:outsourced:create`。
- JWT 只保存 `userId` 和角色列表；后端按 `/api/admin/**`、`/api/user/**` 路径校验角色，前端使用登录返回的功能列表控制菜单和按钮。
- 密码必须使用安全哈希（当前项目使用 BCrypt），不得保存或记录明文密码。
- 数据库密码、Redis 密码、JWT 密钥等敏感信息只能通过环境变量或安全配置注入，不得写入源码、测试数据、SQL 初始化数据或提交记录。
- 新增公开接口时必须明确其认证策略，默认不得放行。

## 数据库规范

- 表名和字段名使用小写下划线格式；系统表使用 `sys_` 前缀，业务表使用 `biz_` 前缀。
- 实体字段使用驼峰命名，并依赖 MyBatis-Plus 的下划线映射。
- 业务表默认包含 `gmt_create`、`gmt_modified`、`creator`、`modifier`、`is_valid`。
- 逻辑删除统一使用 `is_valid`：`1` 表示有效，`0` 表示删除。
- 数据库结构或初始化数据变化时，同步维护 `sql/page_forge_base.sql`，确保脚本可重复理解和部署。
- 兼容 MySQL 5.7.40，不使用仅 MySQL 8 支持的 SQL 特性。

## 构建与验证

在仓库根目录执行 Maven 命令：

```bash
mvn test
mvn clean package
```

- 优先运行与改动模块最相关的测试；交付前至少执行 `mvn test`。
- 涉及模块依赖、打包或启动配置时，再执行 `mvn clean package`。
- 运行应用至少需要环境变量 `DB_USERNAME`、`DB_PASSWORD` 和长度不少于 32 字节的 `JWT_SECRET`。
- 新增或修改业务逻辑时补充相应测试，覆盖正常流程、参数校验、权限边界和关键异常分支。
- 不提交 `target/`、IDE 文件、运行日志、本地配置或任何密钥。

## 变更原则

- 开始修改前先阅读相关模块现有实现，并以仓库实际代码为准；若实际实现与设计文档冲突，应指出差异，不擅自扩大改动范围。
- 优先复用公共模型、异常、转换器和配置，避免复制相似实现。
- 只修改完成当前需求所必需的文件，保留用户已有的无关改动。
- 修改 API、数据库结构、环境变量或启动方式时，同步更新相关文档和示例。

## Git 提交约定

- Git 提交信息使用中文，准确描述本次完成的内容。
- 一次提交只包含一个清晰主题，不混入无关改动。
- 未经明确要求，不执行提交、推送、强制覆盖历史或其他破坏性 Git 操作。
