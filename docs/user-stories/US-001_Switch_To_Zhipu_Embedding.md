# US-001: 切换至智谱 AI Embedding 模型

## 元数据 (Metadata)
- **所属特性:** FEAT-001: 增强中文 RAG 能力
- **故事点:** 3
- **优先级:** 高
- **状态:** 已完成
- **负责人:** 开发团队

## 描述 (Description)
**作为一个** 最终用户
**我希望** 系统使用智谱 AI (GLM) 进行文本向量化 (Embedding)，而不是本地的 AllMiniLM 模型
**以便于** 我在使用中文提问时能获得更精准的搜索结果和更好的语义理解。

## 背景 (Context)
当前的本地模型 (`all-minilm-l6-v2`) 虽然轻量，但主要针对英文优化。对于包含混合内容或纯中文内容的企业知识库，智谱 AI 的 `embedding-2` 模型在语义检索方面提供了更卓越的性能。

## 验收标准 (Acceptance Criteria)

```gherkin
场景: 使用中文查询进行 RAG 检索
  假如 系统已配置智谱 AI API Key
  并且 "ADR-0001" 文档已加载到向量库中
  当 我通过 RAG 接口问 "我们为什么选择 LangChain4j?"
  那么 系统应该检索到 ADR 中的相关片段
  并且 回答应该准确解释原因 (如 Java 生态、团队技能等)

场景: 应用启动检查
  假如 应用正在启动
  当 RagConfig Bean 初始化时
  那么 它应该成功创建 ZhipuAiEmbeddingModel Bean
  并且 不应该抛出关于 AllMiniLM 的缺失依赖错误

场景: 配置验证
  假如 存在 application.properties 文件
  当 我检查配置内容时
  那么 它应该包含 "langchain4j.zhipu.embedding-model.api-key"
  并且 不应该包含本地 Embedding 模型的配置
```

## 技术说明 (Technical Notes)
- 依赖项: `dev.langchain4j:langchain4j-zhipu-ai:0.35.0`
- 配置项: 需要环境变量 `ZHIPU_API_KEY`
- 模型名: `embedding-2`
