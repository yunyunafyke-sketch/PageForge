# PageForge 前端

基于 Vue 3、TypeScript、Vite、Element Plus 和 Pinia 的 PageForge 管理后台。

## 本地运行

需要 Node.js 18.18+。首次使用先安装依赖：

```bash
npm install
npm run dev
```

开发服务默认运行在 `http://localhost:5173`，并将 `/api` 请求代理到 `http://localhost:8080`。
如后端地址不同，可以复制 `.env.example` 为 `.env.local` 并修改：

```text
VITE_API_PROXY_TARGET=http://localhost:8080
```

## 构建验证

```bash
npm run type-check
npm run build
```

生产环境可通过 `VITE_API_BASE_URL` 指定后端接口前缀，默认使用同源 `/api`。

## 目录结构

```text
src
├── api          后端接口及统一 Axios 封装
├── components   公共页面组件
├── layouts      管理后台布局
├── router       路由与登录、角色、功能守卫
├── stores       Pinia 登录状态
├── styles       全局样式与响应式布局
├── types        ResultModel、PageResult 和业务类型
├── utils        本地登录状态存储
└── views        认证、系统、业务、文件和接口总览页面
```

## 后端约定

- 业务接口统一使用 `POST`。
- 自动为登录后请求添加 `Authorization: Bearer <token>`。
- 统一解析 `ResultModel<T>`；业务失败时显示 `errorMessage`。
- 分页请求从第 1 页开始，统一处理 `PageResult<T>`。
- `/api/admin/**` 页面只允许 `ADMIN` 角色访问。
- 菜单和业务按钮使用登录结果中的 `functions` 功能编码控制。
- 用户角色或角色功能调整后，需要重新登录以刷新权限。
