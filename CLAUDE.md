# ImaginaryCraft 项目 — 操作规则

NeoForge 1.21.1 多模块 Kotlin 项目。

> ## 🔴 必读——操作底线
>
> 每次执行文件操作前必须逐条确认：
> 1. **已有文件→用 `apply_patch_update_file` 行级 hunk**，绝不用整文件替换
> 2. **新文件→用 `apply_patch_add_file`**
> 3. **改完立即 `reformat_code`**
> 4. **改完立即 `build_project` 验证编译**
> 5. **hunk 匹配失败→停下来报告，不绕路**
> 6. **修改范围仅限任务相关的代码，不碰无关部分**
> 7. **备份按 `_archived/temp/` 路径规则保存，不乱放**

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
- `@AllOpe` 注解会自动 open 类和其中所有方法，只需标注在基类即可，
  子类无需重复标注，也无需写显式 `open`

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

- **IDE 工具优先** — 使用 `reformat_code`、`refactor_rename` 等 IDE 工具修改文件
- **次选 apply_patch 工具** — `apply_patch_update_file`（行级 hunk）、`apply_patch_add_file`（新文件）、`apply_patch_delete_file`（删除）
- **禁止使用 shell 命令写文件** — 如 PowerShell `Set-Content`、`Out-File`、重定向等绕路手段
- hunk 匹配失败 → 停下报告，不绕路

- 备份按当前时间（yyyyMMdd）和对话编号保存到 _archived/temp/ 下
- 临时脚本用完即删，需要保留的放入 _archived/temp/
- tab/空格缩进对不上没关系，hunk 直接提交，事后用 reformat_code 修正
- 创建新文件→ `apply_patch_add_file`
- 删除文件→ `apply_patch_delete_file`
- 修改已有文件→ `apply_patch_update_file`（行级 hunk）
- 整文件替换→ 禁止，永远用行级补丁
- 修改或创建文件后，调用 IDE 的 `reformat_code` 格式化工具
- 删除前必须备份到 `.migration_plan/`，后缀 `.bak`，确认后再删
- 删除前列出内容→ 检查引用→ 先移动（不立即删除）→ 编译验证→ 再删
- 文件写入：PowerShell `Set-Content` 写入前 `.TrimEnd("", "
")`
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
- 额外资源/ — mod jar 和资源文件，保持原位不动
- 各模块根目录的 `CLAUDE.md` — 项目配置

---

## 修改前自检清单

执行任何文件操作前快速过一遍：

- [ ] 这是新文件 → `apply_patch_add_file`
- [ ] 这是已有文件 → `apply_patch_update_file`（行级 hunk），绝不 `replace_file`
- [ ] hunk 匹配失败 → 停下报告，不绕路不整文件替换
- [ ] 改完了？→ `reformat_code`
- [ ] 编译检查了？→ `build_project`
- [ ] 只改了任务相关的代码？
