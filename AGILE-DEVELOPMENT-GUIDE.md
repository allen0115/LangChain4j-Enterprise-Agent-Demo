# Agile 开发检查清单: 文档上传功能

本清单旨在指导 `LangChain4j-Enterprise-Agent-Demo` 项目中 "文档上传 API" 功能的开发全流程。

## 1. 需求与规划 (产品负责人 / 架构师)
- [ ] **创建 Epic**: 定义高层目标。
    - *指令*: "Create an agile Epic for Document Management System"
    - *核心*: 上传、删除和列出文档，用于 RAG 知识库摄入。
- [ ] **创建 Feature**: 拆解 Epic。
    - *指令*: "Create an agile Feature for Document Upload API"
    - *范围*: REST 接口 (`POST /api/documents`)，文件校验，向量化摄入。
- [ ] **创建 User Story**: 细化需求。
    - *指令*: "Create an agile User Story for Uploading PDF files"
    - *验收标准*: 必须支持 `.pdf` 和 `.txt`，最大 10MB，自动摄入到向量库。
- [ ] **架构决策 (ADR)**: 记录技术选型。
    - *指令*: "Create an ADR for Document Storage Strategy"
    - *决策点*: 文件存本地还是 S3？内存向量库如何更新？

## 2. 设计 (技术负责)
- [ ] **API 设计**: 定义 OpenAPI/Swagger 规范。
    - `POST /api/docs/upload` (MultipartFile)
- [ ] **C4 架构图**: 系统分层视图。
    - *指令*: "Generate C4 diagrams for Document System"
    
    **Level 1: System Context**
    ```mermaid
    C4Context
      title System Context diagram for Enterprise AI Agent

      Person(user, "User", "Uses the AI Agent for chat and document upload")
      System(agent, "AI Agent Demo", "Provides Chat, RAG, and Tool capabilities")
      System_Ext(deepseek, "DeepSeek API", "LLM for Chat Generation")
      System_Ext(zhipu, "Zhipu AI", "Embedding Model for RAG")

      Rel(user, agent, "Uses", "HTTPS")
      Rel(agent, deepseek, "Generates Text", "HTTPS/JSON")
      Rel(agent, zhipu, "Embeds Text", "HTTPS/JSON")
    ```

    **Level 2: Container**
    ```mermaid
    C4Container
      title Container diagram for AI Agent Demo

      Person(user, "User", "Uses the system")
      
      System_Boundary(c1, "AI Agent Demo") {
        Container(web_app, "Spring Boot App", "Java, LangChain4j", "Handles requests, orchestrates AI flow")
        ContainerDb(vector_db, "Vector Store", "In-Memory", "Stores document embeddings")
        ContainerDb(chat_memory, "Chat Memory", "In-Memory", "Stores conversation context (MessageWindow)")
        ContainerDb(file_storage, "File Storage", "Local Disk", "Stores uploaded raw files")
        ContainerDb(metadata_db, "Metadata DB", "H2/MySQL", "Stores document status & metadata")
      }

      System_Ext(deepseek, "DeepSeek API", "LLM")
      System_Ext(zhipu, "Zhipu AI", "Embedding")

      Rel(user, web_app, "Uploads Docs / Chats", "HTTPS")
      Rel(web_app, vector_db, "Reads/Writes Embeddings")
      Rel(web_app, chat_memory, "Reads/Writes Context")
      Rel(web_app, file_storage, "Reads/Writes Files")
      Rel(web_app, metadata_db, "Reads/Writes Metadata", "JDBC")
      Rel(web_app, deepseek, "API Calls", "HTTPS")
      Rel(web_app, zhipu, "API Calls", "HTTPS")
    ```

- [ ] **UML 类图**: 定义对象结构。
    - *核心类*: `DocumentController`, `IngestionService`, `FileStorageService`, `VectorStoreRepository`.
    
    ```mermaid
    classDiagram
        %% 实体与数据访问
        class DocumentMetadata {
            +Long id
            +String userId
            +String fileName
            +String storagePath
            +DocumentStatus status
            +LocalDateTime uploadedAt
        }
        
        class DocumentStatus {
            <<enumeration>>
            PENDING
            INGESTED
            FAILED
        }
        
        class DocumentRepository {
            <<interface>>
            +save(DocumentMetadata)
            +findById(Long)
        }

        %% 核心业务外观 (Facade)
        class IngestionFacadeService {
            +ingest(MultipartFile file, String userId)
        }

        %% 策略模式 (Strategy) - 文档加载
        class DocumentLoaderStrategy {
            <<interface>>
            +supports(String contentType): boolean
            +load(Path filePath): Document
        }
        
        class PdfLoaderStrategy {
            +supports(String): boolean
            +load(Path): Document
        }
        
        class TextLoaderStrategy {
            +supports(String): boolean
            +load(Path): Document
        }
        
        class DocumentLoaderFactory {
            +getLoader(String contentType): DocumentLoaderStrategy
        }

        %% 依赖关系
        DocumentMetadata --> DocumentStatus
        IngestionFacadeService --> DocumentRepository
        IngestionFacadeService --> DocumentLoaderFactory
        IngestionFacadeService --> FileStorageService
        DocumentLoaderFactory --> DocumentLoaderStrategy
        PdfLoaderStrategy ..|> DocumentLoaderStrategy
        TextLoaderStrategy ..|> DocumentLoaderStrategy
    ```

- [ ] **ER 图 (数据库设计)**: 设计元数据存储。
    - *表结构*: `documents` (id, filename, status, uploaded_at, vector_id).
    
    ```mermaid
    erDiagram
        DOCUMENTS {
            bigint id PK
            varchar filename
            varchar user_id
            varchar storage_path
            varchar status
            timestamp uploaded_at
        }
    ```

- [ ] **时序图**: 可视化数据流。
    - *流程*: 用户 -> Controller -> 文件存储 -> 文档解析器 -> Embedding模型 -> 向量库。
    
    ```mermaid
    sequenceDiagram
        actor User
        participant Controller as DocumentController
        participant Facade as IngestionFacadeService
        participant Storage as FileStorageService
        participant Repo as DocumentRepository
        participant Factory as DocumentLoaderFactory
        participant Ingestor as EmbeddingStoreIngestor
        participant VectorDB as VectorStore

        User->>Controller: POST /upload (file)
        activate Controller
        Controller->>Facade: ingest(file, userId)
        activate Facade
        
        Facade->>Storage: store(file)
        Storage-->>Facade: filePath
        
        Facade->>Repo: save(metadata: PENDING)
        
        Facade->>Factory: getLoader(contentType)
        Factory-->>Facade: loader
        
        Facade->>Facade: loader.load(filePath) -> Document
        
        Facade->>Ingestor: ingest(Document)
        activate Ingestor
        Ingestor->>VectorDB: add(embeddings)
        Ingestor-->>Facade: void
        deactivate Ingestor
        
        Facade->>Repo: updateStatus(INGESTED)
        
        Facade-->>Controller: success
        deactivate Facade
        Controller-->>User: 200 OK (docId)
        deactivate Controller
    ```
- [ ] **安全评审**: 检查文件上传漏洞（如恶意文件类型）。

## 3. 实现 (开发人员)
- [ ] **环境准备**: 确保 `ZHIPU_API_KEY` 已设置。
- [ ] **实现 Controller**: 创建 `DocumentController`。
- [ ] **实现 Service**: 创建 `IngestionService`。
    - 使用 `FileSystemDocumentLoader` 或 `UrlDocumentLoader`。
    - 使用 `EmbeddingStoreIngestor` 进行切分和存储。
- [ ] **单元测试**: 测试 Controller 的校验逻辑。
- [ ] **集成测试**: 测试完整的"上传-向量化"流程（必要时 Mock Embedding API）。

## 4. 详细任务清单 (Task Breakdown)
- [ ] **数据库**: 创建 `schema.sql`，定义 `documents` 表。
- [ ] **实体层**: 编写 `DocumentMetadata` JPA 实体及 Repository。
- [ ] **核心逻辑**: 实现 `DocumentLoaderFactory` 和各种 `LoaderStrategy`。
- [ ] **服务层**: 实现 `IngestionFacadeService`，串联存储和向量化逻辑。
- [ ] **接口层**: 编写 `DocumentController`，处理 MultipartFile 和 DTO 转换。
- [ ] **异常处理**: 定义 `GlobalExceptionHandler` 处理文件过大或格式不支持异常。

## 5. 完成的定义 (Definition of Done - DoD)
- [ ] 所有单元测试通过，核心逻辑覆盖率 > 80%。
- [ ] 集成测试通过，能够成功上传文件并进行 RAG 问答。
- [ ] 代码通过 Checkstyle/SonarQube 静态检查。
- [ ] API 文档 (Swagger/README) 已更新。
- [ ] 无严重安全漏洞 (如任意文件上传)。

## 6. 评审与交付 (团队)
- [ ] **代码评审 (CR)**: 检查代码风格和安全最佳实践。
- [ ] **文档更新**: 在 `README.md` 中添加新 API 说明。
- [ ] **演示 (Demo)**: 使用 `curl` 或 Postman 演示功能。
    - 上传一个文件 -> 问一个相关问题 -> 验证 RAG 回答。
- [ ] **合并**: 将特性分支合并到主干。

## 5. 进度追踪
- **Epic 状态**: [ ] 起草中  [ ] 进行中  [ ] 已完成
- **关键风险**:
    - 大文件处理耗时（是否需要异步？）
    - InMemoryEmbeddingStore 的内存占用问题。

---
*Generated by cursor-rules-agile skill*
