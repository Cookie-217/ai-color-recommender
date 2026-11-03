# ColorAI · AI 色彩搭配智能推荐系统

前后端分离 · 响应式（手机/平板/桌面）· Spring Boot + Vue3 · 内置 AI 配色 Agent · 图片取色 · 一键部署。

输入一个颜色或一句话，即可获得专业、可落地的配色方案。

## 功能
- **AI 配色 Agent**：融合「情绪 / 场景 / 风格」知识库，自动编排 9 种和谐规则并给出中文推荐理由与无障碍建议。
- **自然语言顾问**：用中文描述需求（如“温暖高级感的咖啡品牌配色”）即出方案。
- **图片取色 / 品牌提取**：上传图片 K-Means 聚类提取主色，品牌模式额外输出主色/辅助/点缀/中性色板。
- **调色工具**：HEX⇄RGB⇄HSL⇄HSV⇄CMYK⇄LAB、WCAG 对比度、渐变生成。
- **导出**：配色一键导出 CSS 变量 / JSON。
- 用户系统（JWT + BCrypt）、明暗主题、移动端自适应。

## 技术栈
Java 8 · Spring Boot 2.7 · Spring Security(JWT) · JPA · H2/PostgreSQL · Vue3 · Vite · Tailwind · Pinia · Docker · GitHub Actions → GHCR

## 本地运行
```bash
# 后端
cd backend && mvn package && java -jar target/ai-color-recommender.jar   # http://localhost:8080
# 前端（开发）
cd frontend && npm install && npm run dev                               # http://localhost:5173
```
演示账号：`demo / demo123`（dev 自动初始化）。API 文档：`/swagger-ui.html`。

## 部署上线
[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/Cookie-217/ai-color-recommender)

镜像自动推送到 GHCR：`ghcr.io/cookie-217/ai-color-recommender:latest`（GHCR 要求仓库名全小写）。任选其一即可获得公网地址：
- **Render**：仓库根目录已含 `render.yaml`，在 Render 导入仓库即一键部署（免费套餐可用）。
- **Railway**：已含 `railway.json`，连接仓库后自动识别 Docker 部署。
- **Fly.io**：`fly launch`（使用 `fly.toml`）。
- **自有服务器**：`docker compose up -d` → `http://localhost:8080`。

> CI（`.github/workflows/deploy.yml`）：push 到 main 自动构建前后端、打包镜像并推送 GHCR。
> 安全：所有密钥（JWT、DB、LLM）仅走环境变量 / `.env`，已被 `.gitignore` 忽略，仓库不含任何明文密码。

## 目录
```
backend/   Spring Boot 后端（同时托管构建后的前端静态资源）
frontend/  Vue3 前端
docker-compose.yml / render.yaml / railway.json / fly.toml   部署配置
```

MIT · 仅供学习交流。
