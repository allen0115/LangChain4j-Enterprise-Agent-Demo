---
title: "ADR-0001: 企业级 AI Agent 开发框架选型"
status: "提议中"
date: "2026-01-31"
authors: "Trae AI"
tags: ["架构", "决策", "ai", "java", "python", "agent"]
supersedes: ""
superseded_by: ""
---

# ADR-0001: 企业级 AI Agent 开发框架选型

## 状态

**提议中**

## 背景

我们需要启动企业级 AI Agent 应用的开发。该系统需要具备复杂的推理、工具使用、记忆管理以及潜在的多智能体编排能力。项目目前初始化为 `langgraph-app-container-demo`，表明初期对 LangGraph (Python) 生态系统有一定兴趣。

**约束与驱动因素：**
- **团队技能**：开发团队主要精通 **Java**，只有少数成员具备 **Python** 经验。
- **可维护性**：优先选择统一的技术栈，以降低运维复杂度和知识孤岛。
- **功能对齐**：所选框架必须支持现代 Agent 模式（如 ReAct、函数调用、RAG 等）。
- **企业集成**：解决方案必须能与现有的 Java 企业服务和中间件良好集成。

## 决策

我们将采用 **LangChain4j** 作为 AI Agent 的主要开发框架。

我们将主要使用 **Java** 开发 Agent 逻辑，以与团队的核心竞争力保持一致。对于某些仅 Python 生态系统支持的、高度复杂的 Agent 工作流（例如使用最新 LangGraph 功能的高级研究型 Agent），我们将允许作为 **Python 微服务** 例外处理，由具备 Python 技能的成员管理，但应尽量减少这种情况。

## 后果

### 正面影响

- **POS-001**: **技能匹配**：利用团队现有的 Java 专业知识，确保更快的上手速度、代码质量和招聘便利性。
- **POS-002**: **成熟的功能集**：LangChain4j (v1.x+) 提供了 Java 生态中最全面的 Agent 支持，紧跟 Python LangChain 的能力。
- **POS-003**: **类型安全**：受益于 Java 的强类型和企业级工具链（IDE、调试、测试、监控）。
- **POS-004**: **集成性**：与现有的 Spring Boot 应用、安全上下文和数据库层无缝集成。

### 负面影响

- **NEG-001**: **更新滞后**：前沿的 AI 功能通常先出现在 Python 库（LangChain/LlamaIndex）中，然后才移植到 Java。
- **NEG-002**: **社区规模**：Java AI 的生态系统和社区资源小于 Python 数据科学社区。
- **NEG-003**: **Python 依赖**：对于特定的数据科学或实验性任务，可能仍需使用 Python，若不严格管理可能导致双栈复杂度。

## 备选方案

### Spring AI

- **ALT-001**: **描述**：Spring 官方的 AI 集成项目。
- **ALT-001**: **拒绝理由**：虽然在基础集成和 RAG 方面表现出色，但其高级 **Agent** 抽象能力（编排、自主循环）目前相比 LangChain4j 不够成熟和丰富。

### Python 微服务 (LangGraph / LangChain)

- **ALT-002**: **描述**：使用市场领先的 LangGraph 库将 Agent 构建为 Python 服务，通过 REST/gRPC 与 Java 后端通信。
- **ALT-002**: **拒绝理由**：引入了分层技术栈架构（“多语言税”）。考虑到团队大部分专注于 Java，维护核心的 Python 代码库会造成瓶颈，并导致围绕少数 Python 开发人员的知识孤岛。
- **注意**：对于 LangChain4j 无法处理的隔离且复杂的子任务，这仍然是一个有效的*次要*选项。

### Semantic Kernel (Java)

- **ALT-003**: **描述**：Microsoft 用于集成 LLM 的 SDK。
- **ALT-003**: **拒绝理由**：相比 LangChain4j，其 Java 实现的功能和社区支持历史上一直落后于 C# 和 Python 版本。

## 实施说明

- **IMP-001**: 使用 `langchain4j-spring-boot-starter` 进行无缝集成。
- **IMP-002**: 如果绝对必要引入 Python 微服务，需建立清晰的接口定义。
- **IMP-003**: 关注 "Spring AI" 的路线图；如果其功能达到同等水平，可以考虑迁移（尽管可能性较小）。

## 参考资料

- **REF-001**: [LangChain4j 文档](https://docs.langchain4j.dev/)
- **REF-002**: [Spring AI 文档](https://docs.spring.io/spring-ai/reference/)
