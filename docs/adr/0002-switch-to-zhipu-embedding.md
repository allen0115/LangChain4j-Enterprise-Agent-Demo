# 0002 切换至智谱 AI Embedding 模型

## 状态 (Status)
已接受 (Accepted)

## 背景 (Context)
在项目初期，为了快速验证和降低部署门槛，我们选择了本地运行的 `AllMiniLmL6V2` 模型作为 RAG 系统的 Embedding 引擎。
然而，随着企业知识库内容的增加，我们发现：
1.  **中文理解能力有限**：AllMiniLM 主要在英文数据集上训练，对中文长文本和专业术语的语义捕捉不够精准。
2.  **检索相关性不足**：用户使用中文提问时，经常无法召回最相关的文档片段，导致回答质量下降。

我们需要一个在中文语义理解上表现更佳的 Embedding 模型，同时保持与 LangChain4j 的兼容性。

## 决策 (Decision)
我们决定**弃用本地 AllMiniLM 模型，全面切换至智谱 AI (Zhipu AI) 的 `embedding-2` 在线模型**。

具体变更包括：
1.  移除 `langchain4j-embeddings-all-minilm-l6-v2` 依赖。
2.  引入 `langchain4j-zhipu-ai` 依赖。
3.  配置 `RagConfig` 使用 `ZhipuAiEmbeddingModel`。
4.  在 `application.properties` 中增加 API Key 配置项。

## 后果 (Consequences)

### 积极影响 (Positive)
*   **中文检索质的飞跃**：智谱 GLM 系列模型对中文语境有深度优化，显著提高了 RAG 系统的召回准确率。
*   **维护简化**：不再需要在应用 jar 包中打包几十 MB 的 ONNX 模型文件，减小了构建产物体积。

### 消极影响/风险 (Negative/Risks)
*   **外部依赖**：系统现在强依赖智谱 AI 的 API 服务，如果外部服务宕机，RAG 功能将不可用。
*   **成本增加**：Embedding 变为付费服务（虽然智谱目前价格较低，但不再是免费的）。
*   **隐私考量**：文档数据需要发送到智谱云端进行向量化，不再是纯本地处理（需确认是否符合企业隐私合规要求）。
*   **配置复杂度**：部署时必须提供有效的 `ZHIPU_API_KEY`，否则应用无法启动。

## 验证 (Validation)
通过 `US-001` 定义的验收测试，确认中文问题的回答准确率有明显提升，且应用启动流程正常。
