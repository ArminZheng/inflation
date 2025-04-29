/*
# 开发一个数据源, 底层基于 mybatis 实现
功能的目的是为了快速制作查询列表, 以便支持多样化的查询统计, 又因为直接执行SQL 容易造成SQL 注入, 所以希望用到 MyBatis 这一层来做隔离, 支持 SQL 数据的动态新增, 有发布动作, 发布后, 查询就固化成 mappedstatement 了, 后续查询都使用 mappedstatement 而不是裸 SQL 数据, 只在发布时才会更新 mappedstatement. 注意 返回的结果因为没有数据定义, 请使用 Hashmap 作为返回对象, 默认返回多条, 再用 List 包裹一层, 最终的格式为 List<Map<String, Object>>
[功能点]
1. 使用文件夹的方式读取SQL 文件, 支持运行时更新文件
2. 增加数据库存储SQL 数据 (同名时, 数据库优先), 支持运行时更新 MappedStatement, 数据库和文件两种方式并存, 数据库支持新增, 更新, 删除(表数据库删除), 发布, 取消发布(mappedstatement 删除)
[简要设计]
 1. 设定一个 datasource 目录, 里面存放所有的 SQL 文件, 启动时扫描路径 (路径仅一层)
 2. 实现简单 select SQL 文本文件读取, 转换成 mappedStatement, 文件名就是对应的 id
 3. 因为使用时必须要有一个接口, 故设计一个通用 SourceMapper 接口 (注意不是 @Mapper, 仅是一个顶层的父类接口), 入参为 id, 返回格式为 List<Map<String, Object>>, 在启动时, 通过动态代理, 根据一个文件, 生成一个 SourceMapper 实现类, 并注册到 mappedstatement 里面, id 强制指定为 对应的文件名
 4. 逻辑包含自动生成对应的 mapper 接口实现, 如动态代理
 5. 达到的效果是 传入id, 返回的 SQL 的执行结果
 6. 结合 spring boot 3, 需要一个 controller 接口, 传入 /datasource/{id} GET, 返回 List<Map> 结果
[细节设计]
 - 一个 resource 下面的, 设定固定的 datasource 目录, 里面存所有 sql 文件, 启动时扫描路径 (路径仅一层)
   - 举例: users.sql 文件里面是单条的简单查询 SQL, id 以 文件名 为准 (id: users)
 - 后续 SQL 数据会放在数据库里, 有 id, sql 两列, 需要支持运行时 MappedStatement 的动态创建和使用
 - 仅查询,
 - 重点要结合 mybatis, 比如里面的 MappedStatement
 - 全局仅一个 Mapper 接口, 传入 id, 返回 List<Map> 结果
 */
package com.arminzheng.inflation.datasource;
