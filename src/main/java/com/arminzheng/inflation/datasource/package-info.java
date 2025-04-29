/*
# 数据源查询功能
    本功能基于 MyBatis 实现动态数据源查询，支持通过 SQL 文件和数据库两种方式管理 SQL 语句，数据库中的同名 SQL ID 优先生效。
    系统支持 SQL 的新增、更新、删除、发布和取消发布，发布后会动态生成或移除对应的 MappedStatement，实现运行时的 SQL 热更新。
    所有查询通过统一的 SourceMapper 接口进行，返回结果为List<Map<String, Object>>，便于多样化的数据统计和展示。
    通过 REST 接口可按 ID 查询数据，实现灵活的数据访问和管理。返回的结果没有结构定义, 故用 Map 作为返回对象, 默认返回多条, 再用 List 包裹一层。
[原始目的]
    功能的原始目的是为了快速制作查询列表, 以便支持多样化的查询统计, 又因为直接执行SQL 容易造成 SQL 注入, 所以用到 MyBatis 这一层来做隔离。
    在数据源的新增和发布后, 固化成 MappedStatement, 后续查询都使用 MappedStatement 而不是 SQL 数据. 此外只在发布时才会实际更新 MappedStatement。
[细节设计]
    - 在 resource 下面的, 设定固定的 datasource 目录, 里面存所有 sql 文件, 启动时扫描路径 (路径仅一层), 以文件名作为 SQL ID
        - 举例: users.sql 文件包含单条的查询, SQL ID 设置为 users.
    - 全局仅一个 Mapper 接口, 传入 id, 返回 List<Map> 结果

# Data Source Query Function
    This feature implements dynamic data source queries using MyBatis, supporting SQL management through both SQL files and database storage.
    SQL entries with the same ID in the database take priority. The system allows adding, updating, deleting, publishing, and unpublishing SQL templates.
    After publishing, corresponding MappedStatement objects are dynamically generated or removed, enabling runtime SQL hot-reloading.
    All queries are executed through a unified SourceMapper interface, returning results as List<Map<String, Object>> to support flexible data statistics and display.
    A REST API is provided to query data by ID, returning results as List<Map<String, Object>> (using Map for schema-agnostic results, wrapped in List for multiple rows).
[Original Purpose]
    Quickly create query templates for diverse statistical requirements. Prevent SQL injection by isolating raw SQL execution through MyBatis.
    After publishing, SQL templates are solidified into MappedStatement, ensuring subsequent queries use compiled statements instead of raw SQL.
    MappedStatement updates only occur during publishing.
[Design Details]
    - A fixed datasource directory under resources stores SQL files. The directory is scanned at startup (single-level scan), with filenames used as SQL IDs.
        - Example: users.sql contains a query, and its SQL ID is users.
    - A global SourceMapper interface accepts SQL IDs as input and returns List<Map<String, Object>>.
 */
package com.arminzheng.inflation.datasource;
