# ImaginaryCraft 项目 — 操作规则

NeoForge 1.21.1 多模块 Kotlin 项目。

## 工作方式

- 先理解任务、给计划，同意后再执行
- 重大问题改动先备份再改
- 仔细检查流程，不要遗漏
- 读取实际文件而不是依赖记忆
- 构建报错时先分析原因再修复
- 使用 `multi_tool_use.parallel` 并行执行
- 通过 `TODO.md` 跟踪待办事项

## 代码修改规范

- 修改代码只修改相关的部分，不涉及无关代码
- 不随意改动现有注释
- **禁止整文件替换，必须使用行级补丁（apply_patch hunk）或精确行编辑**

## 技术约定

- 所有业务代码 Kotlin，Mixin 必须 Java
- `build-conventions.gradle` 集中配置
- 版本号统一在根 `gradle.properties`

## 命名规范

- **接口**: `I` 前缀（`IEntityAnimationMapper`）
- **Mixin 字段/方法**: `modid + $`
- **包名**: 全小写 snake_case
- **常量/枚举值**: 全部大写 `UPPER_SNAKE_CASE`
- **ID/常用值**: 静态变量引用，不硬编码
- **Mixin 接口**放 `mixed/`，Java mixin 放 `mixin/`

## 代码风格

- 使用 `//region`/`//endregion` 代码折叠
- 注释优先中文
- 使用 Log4j `LogManager`/`Logger`，通过各模块的通用日志常量
- `ext` 辅助方法声明依赖：`addModuleDeps(...)`, `addJEI()` 等
- 复杂函数要拆分为多个小函数，每个职责单一
- 字段和函数必须使用文档注释（`/** ... */`）
- 复杂或可能有异议的逻辑要加行内注释说明意图
- 重复代码必须提取为公用函数或常量
- 能尽早返回的要提前返回，减少嵌套

## CLAUDE.md 编写规范

- 模块级内容编写在对应模块的 `CLAUDE.md`
- 全局 `CLAUDE.md` 只记录跨模块的全局规则和约定

## 文件操作规则

- 删除前必须备份到 `.migration_plan/`，后缀 `.bak`，确认后再删
- 删除前列出内容 → 检查引用 → 先移动（不立即删除）→ 编译验证 → 再删
- 文件写入：PowerShell `Set-Content` 写入前 `.TrimEnd("\r", "\n")`
- 额外资源/ 文件夹不动

## 构建习惯

- 单模块构建：非必要只构建单个模块（`project_path=module/模块名`）

## 杂项归档

所有非源码、非构建、非运行时的杂项资源统一存放在项目根目录 `_archived/` 中，按类型分类：

| 子目录                  | 内容                 |
|----------------------|--------------------|
| `scripts/`           | 根目录工具脚本（temp_*.py） |
| `docs/`              | TODO.md            |
| `migration_backups/` | 迁移过程中的 .bak 版本备份   |
| `migration_scripts/` | 迁移用的 Python 脚本     |
| `migration_misc/`    | 迁移暂存的中间代码和 .ps1 脚本 |

**不纳入归档：**
- `.claude/`, `.cursor/`, `.continue/` — IDE 技能配置，保持原位
- `build/`, `run/` — 构建和运行时产物
- `module/*/src/` — 源码
- `额外资源/` — mod jar 和资源文件，保持原位不动
- 各模块根目录的 `CLAUDE.md` — 项目配置
