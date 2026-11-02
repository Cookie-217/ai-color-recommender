# 🎨 基于 Java 的 AI 色彩搭配智能推荐系统

> 前后端分离 · 响应式（手机/平板/桌面全适配）· Spring Boot + Vue3 · 内置 AI 配色 Agent · 一键部署上线

一个将**色彩理论**与**AI 推荐 Agent**结合的智能配色系统：输入一个颜色或一句自然语言需求，
即可获得专业、可解释、可直接落地的配色方案，并支持转换、对比度检测、渐变生成与方案收藏。

---

## ✨ 功能特性

- **AI 配色 Agent（核心）**：综合「情绪 / 场景 / 风格」多维度知识库，自动编排和谐引擎，生成结构化推荐与推荐理由。
- **自然语言顾问**：在 AI 顾问页用中文描述需求（如“温暖高级感的咖啡品牌配色”），自动解析意图并出方案。
- **9 种和谐规则**：互补 / 邻近 / 三角 / 四角 / 分裂互补 / 单色阶 / 暗调 / 亮调 / 正方形。
- **色彩工具箱**：HEX⇄RGB⇄HSL⇄HSV⇄CMYK⇄LAB 转换、WCAG 无障碍对比度检测、渐变生成。
- **用户系统**：JWT 鉴权 + BCrypt 加密，方案收藏 / 管理。
- **现代化响应式 UI**：Vue3 + Tailwind，明暗主题，移动端到桌面端自适应。
- **可选大模型增强**：配置 OpenAI 兼容 API 后，自动用 LLM 生成更自然的方案解读（未配置则回退内置规则引擎，功能完整可用）。

## 🧱 技术栈

| 层 | 技术 |
|----|------|
| 后端 | Java 8 · Spring Boot 2.7 · Spring Security(JWT) · Spring Data JPA · H2/PostgreSQL · springdoc-openapi |
| 前端 | Vue 3 · Vite · Vue Router(hash) · Pinia · Tailwind CSS · Axios |
| 部署 | Docker · docker-compose · GitHub Actions → GHCR · 支持 Render / Railway / Fly 等 |

## 📁 项目结构

```
.
├── backend/                 # Spring Boot 后端（同时托管构建后的前端静态资源）
│   ├── src/main/java/com/colorai/
│   │   ├── util/           # 色彩模型转换 + 和谐引擎 + 对比度
│   │   ├── service/        # ColorService / PaletteAgentService(AI) / LlmAdvisorService(可选) / Auth / Palette
│   │   ├── controller/     # /api/auth / /api/color / /api/agent / /api/palettes
│   │   ├── security/       # JWT + BCrypt
│   │   └── bootstrap/      # 演示数据初始化（dev）
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # Vue3 前端
│   └── src/
│       ├── views/          # Home(生成器) / Agent(顾问) / Tools(工具) / Gallery(收藏) / Login / Register
│       ├── components/     # PaletteResult / PaletteStrip / ChatBubble ...
│       └── api/            # axios 封装
├── docker-compose.yml       # 一键编排 app + postgres
└── .github/workflows/       # CI：构建 + 推送镜像到 GHCR
```

## 🚀 本地运行

### 后端
```bash
cd backend
# 需要 Maven；未安装可用项目内 ./mvnw（首次会自动下载 Maven）
mvn spring-boot:run
# 或打包后运行
mvn package && java -jar target/ai-color-recommender.jar
```
- 默认 H2 内存库，开箱即用；访问 http://localhost:8080
- API 文档：http://localhost:8080/swagger-ui.html
- 演示账号：`demo / demo123`（dev 环境自动初始化）

### 前端（开发模式）
```bash
cd frontend
npm install
npm run dev          # http://localhost:5173 （代理 /api 到 8080）
```
> 说明：生产构建时前端会被打包进后端 `static/` 目录，由 Spring Boot 同域托管，部署后只需一个服务。

## 🐳 容器化部署

```bash
# 1) 准备环境变量（切勿提交 .env）
cp backend/.env.example backend/.env
# 编辑 .env，设置强随机 APP_JWT_SECRET 与数据库密码

# 2) 一键启动（应用 + PostgreSQL）
docker compose up -d
# 访问 http://localhost:8080
```

镜像也可单独构建：
```bash
docker build -t colorai ./backend
docker run -p 8080:8080 -e APP_JWT_SECRET=你的密钥 colorai
```

## ☁️ 上线到云端（CI 自动化）

仓库已配置 `.github/workflows/deploy.yml`：每次 push 到 `main` 会
**自动构建前后端、将前端打包进后端、并把 Docker 镜像推送到 GitHub Container Registry（GHCR）**。

将镜像部署到任意支持 Docker 的平台即可获得公网地址（均一键）：
- **Railway / Render / Fly.io**：连接本仓库或直接使用 GHCR 镜像 `ghcr.io/<你>/<仓库>:latest`
- **自有服务器**：`docker compose up -d`

> 注意：本仓库不含任何密钥；所有密码 / JWT 密钥均通过环境变量注入，`.env` 已被 `.gitignore` 忽略。

## 🔌 API 速览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/color/convert?hex=#3498db` | 色彩空间转换 |
| GET | `/api/color/harmony?base=#3498db&rule=TRIADIC` | 指定规则配色 |
| GET | `/api/color/contrast?fg=#fff&bg=#1D3557` | WCAG 对比度 |
| POST | `/api/agent/recommend` | AI 推荐（情绪/场景/风格） |
| POST | `/api/agent/chat` | 自然语言配色顾问 |
| POST | `/api/auth/register` · `/api/auth/login` | 注册 / 登录 |
| GET/POST/DELETE | `/api/palettes` | 方案收藏（需登录） |

## 🔐 安全说明

- 用户密码使用 **BCrypt** 哈希存储；JWT 无状态鉴权。
- 所有密钥（JWT 密钥、数据库密码、LLM Key）**仅通过环境变量 / `.env` 提供，不进入代码与 Git 历史**。
- 生产部署请：① 设置强随机 `APP_JWT_SECRET`；② 使用 PostgreSQL 并设强密码；③ 将 `APP_CORS_ORIGINS` 改为真实前端域名。

## 📄 许可证

MIT · 仅供学习与交流使用。
