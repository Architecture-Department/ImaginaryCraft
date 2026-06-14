# ImaginaryCraft 项目

NeoForge 1.21.1 模组项目，采用多模块 Gradle 架构。

## 项目结构

```
ImaginaryCraft/
├── build.gradle                  — 根构建脚本（插件声明 + subprojects 插件应用）
├── build-conventions.gradle      — 集中化模块构建约定（仓库/通用依赖/NeoForge配置/任务）
├── gradle.properties             — 所有版本号
├── settings.gradle               — 模块注册
├── create_module.sh              — 快速新建模块脚本
├── templates/
│   └── mods-base.toml            — neoforge.mods.toml 公共基础模板
└── module/
    ├── GoldenBoughsLib/          — 核心库（属性/伤害/行为树/美德/HUD/渲染）
    ├── ResonatorCombatFramework/ — 战斗框架（实体动画/装备渲染）
    ├── EGOCurios/                — 饰品栏模组（~50 E.G.O.饰品，15槽位）
    ├── EGOEquipment/             — 装备模组（~110武器 + ~30护甲套装）
    ├── Abnormalities/            — 异想体模组
    └── ImaginaryCraft/           — 聚合模块（jarJar所有子模块，最终产物）
```

## 模块依赖图

```
GoldenBoughsLib  ←───  ResonatorCombatFramework  ←───  EGOCurios
    ↑                         ↑                           ↑
    ├─────────────────────────┴───────────────────────────┤
    │                                                     │
    └──→  EGOEquipment  ──→  Abnormalities  ──→  ImaginaryCraft (聚合)
```

## 构建约定

- **`build-conventions.gradle`** 提供所有模块公共配置
- 模块 `build.gradle` 通过 `ext` 辅助方法声明依赖：`addModuleDeps(...)`, `addJEI()`, `addMixinSquared()`,
  `addCurios()` 等
- `neoforge.mods.toml` 由 `templates/mods-base.toml`（公共头部+neo/mc依赖）与模块模板合并生成
- 新建模块：`./create_module.sh <ModName> <mod_id> "显示名" <group>`

## 技术栈

- **Kotlin** — 所有业务代码（KotlinForForge）
- **Java** — 仅 Mixin 类（Kotlin 对 mixin 兼容性不好）
- **GeckoLib** — 3D动画模型渲染
- **MixinSquared** — Mixin 增强支持
- **Curios API** — 饰品栏系统
- **NeoForge ModDev** — 开发环境插件

## 命名规范

- **接口**: `I` 前缀（`IEgoItem`, `IPlayerRcf`, `IHasHoldAnim`）
- **Mixin 唯一字段/方法**: 使用 `$` 前缀防止混淆冲突
- **包名**: 全小写 snake_case
- **常量/枚举值**: `UPPER_SNAKE_CASE`

## 关键约定

- Mixin 类必须放在 `src/main/java/`（Java），其他放 `src/main/kotlin/`（Kotlin）
- 版本号统一在根 `gradle.properties` 维护
- 模块特有依赖在各自 `build.gradle` 中通过辅助方法声明

## GoldenBoughsLib 模块结构

GoldenBoughsLib 按领域划分为以下子模块（`module/` 目录下）：

| 模块              | 包路径                    | 内容                                                                                 |
|-----------------|------------------------|------------------------------------------------------------------------------------|
| **corpse**      | `module.corpse.*`      | 尸体实体（entity/）和渲染器（renderer/）                                                       |
| **shield**      | `module.shield.*`      | 护盾系统：状态效果（mobeffect/）、HUD（hud/）、模型（model/）、渲染器（renderer/）                          |
| **virtue**      | `module.virtue.*`      | 美德系统：API（api/）                                                                     |
| **rationality** | `module.rationality.*` | 理智系统：事件（event/）、工具（util/）、命令（command/）、物品（item/）、HUD（hud/）                         |
| **lc_damage**   | `module.lc_damage.*`   | Lc伤害系统：API（api/）、能力（capability/）、属性（attribute/）、粒子（particle/）、工具（util/）、事件（event/） |

**架构约束：**

- 子模块内无 `@SubscribeEvent` / `@EventBusSubscriber` / 自动注册
- 子模块只暴露普通方法和类，不直接影响 mod
- 事件/注册由父级目录（`events/`, `init/`）统一调度
- 子模块通过外部调用的方式被父级代码使用

## 实体动画系统（ResonatorCombatFramework）

### 包结构

所有动画代码在 `entity_animation/animation/` 下，按职责分层：

```
animation/
├── BakingBrAnimation.kt           — 动画数据结构（Baking 前缀 = JSON 解析中间表示）
├── AnimationEventsToFire.kt       — 事件容器（音效/粒子/时间线）
├── controller/
│   ├── BedrockAnimationController.kt  — 控制器实现：状态机 + crossfade + tick 推进
│   ├── IEntityAnimationController.kt  — 控制器接口
│   └── ActionAnimationController.kt   — 动作层控制器（物品切换检测）
├── mapper/
│   ├── AnimationControllerManager.kt  — 控制器管理器：remerge + 插值 + 事件路由
│   ├── EntityAnimationMapper.kt       — 映射器基类：trigger/stop/pause/resume
│   ├── IEntityAnimationMapper.kt      — 映射器接口
│   ├── LivingEntityAnimationMapper.kt — 生物映射器：骨骼标志收集
│   ├── HumanoidEntityAnimationMapper.kt — 人形骨骼映射：→ HumanoidModel
│   └── PlayerAnimationMapper.kt       — 玩家映射器：控制器注册 + 渲染入口
├── data/
│   ├── AnimationPlayData.kt           — 播放配置 data class
│   ├── ProxyBoneConfigData.kt         — 骨骼配置（含静态 parse() 从 JSON 解析）
│   ├── ProxyBoneFlags.kt             — 骨骼标志（lock/blend/pos/rot/scale 控制）
│   └── ProxyTimelineEntry.kt          — 时间线条目
├── model/
│   ├── BakingBrModel.kt               — 几何模型 JSON 解析（Baking* 中间表示）
│   ├── BrModel.kt                     — 运行时几何模型（MutableMap 骨骼 + 变换矩阵计算）
│   └── ProxyModel.kt                  — 代理骨骼模型（每帧计算/合并的变换数据）
└── molang/
    ├── MathParser.kt / MolangValue.kt / MolangData.kt / ...
    └── function/ + value/  — 函数和值节点
```

### Baking 架构（JSON 解析→运行时）

JSON 解析使用 `Baking*` 中间类，运行时使用 `Br*` 类：

```
BakingBrModel  ──→  BrModel.of(bakingBrModel)  ──→  BrModel （运行时，MutableMap）
BakingBrBone   ──→  BrBone.of(bakingBrBone)     ──→  BrBone  （可变字段）
BakingBrCube   ──→  BrCube.of(bakingBrCube)     ──→  BrCube
BakingBrLocator──→  BrLocator.of(bakingBrLocator)──→  BrLocator
```

### BrModel 动态骨骼管理

`BrModel` 使用 `MutableMap<String, BrBone>` 存储骨骼，支持动态操作：

- `set(BakingBrModel)` — 完全替换
- `add(BakingBrModel)` — 合并，不覆盖已有骨骼
- `overwriteAdd(BakingBrModel)` — 合并，覆盖已有骨骼
- `clear()` — 清空
- `computeBoneGlobalMatrix(name, proxyModel)` — 计算骨骼全局变换矩阵
- `computeLocatorGlobalMatrix(name, proxyModel)` — 计算定位器全局变换矩阵

### 核心数据结构

| 类          | 字段                                                                         | 说明                 |
|------------|----------------------------------------------------------------------------|--------------------|
| BrModel    | bones: MutableMap<String, BrBone>, locators: MutableMap<String, BrLocator> | 运行时几何模型            |
| BrBone     | name, parent?, pivot, rotation, cubes, locators                            | 骨骼数据（可变字段）         |
| BrLocator  | name, boneName, position                                                   | 定位器数据              |
| ProxyModel | bones: HashMap<String, ProxyBone>                                          | 帧变换数据（每 tick 重新计算） |
| ProxyBone  | pos, rotation, scale, locators, emptyMask                                  | 骨骼变换 + 空掩码         |

### 状态机

`BedrockAnimationController.State`：

```
IDLE ──trigger──→ TRANSITIONING ──blend≥1──→ PLAYING ──stop──→ FADING_OUT ──blend≤0──→ IDLE
                       ↑                        ↓
                       └── resume ──────────────┘
```

- **IDLE**: 初始状态，不做任何计算
- **TRANSITIONING**: 淡入/跨动画过渡。blendFactor 0→1（每 tick 推进 1/transitionTicks）。动画时间冻结，骨骼在冻结时间计算
- **PLAYING**: 正常播放，动画时间每 tick 推进
- **FADING_OUT**: 淡出。blendFactor 1→0。动画时间冻结
- **PAUSED**: 暂停，冻结骨骼

### 数据流

```
Server tick (20 TPS, PlayerTickEvent.Post):
  tickAnimations:
    prevMergedProxy = mergedProxy 快照（供渲染插值）
    tickAdvance → 每控制器：
      tickBlend() — 基于 tick 推进 blendFactor
      tickBackend() — 计算骨骼写入 proxyModel
        PLAYING: 推进 animTime
        TRANSITIONING/FADING_OUT: freezeTime=true，冻结时间
      crossfadeStep() — 过渡源→当前骨骼混合
      状态转移检查（blend≥1→PLAYING, blend≤0→IDLE）
    remerge() — 合并所有控制器的 proxyModel → mergedProxy
    firePendingEvents() — 执行时间线/音效/粒子事件

Render frame (mixin LivingEntityRenderer.render):
  tickAndRender:
    tickRender() — 仅 PLAYING 时清理骨骼
    remerge() — 重新合并
    getInterpolatedProxy(partialTick) — prevMergedProxy↔mergedProxy 线性插值
    applyRootTransform() — root 骨骼 → PoseStack
    applyProxyToModel() — 代理骨骼 → HumanoidModel ModelPart
```

### crossfade 过渡

- `transitionSource` 存旧动画骨骼快照（含 emptyMask 同步）
- `crossfadeStep()` 在 tickAdvance 中执行，每 tick 按 blendFactor 混合旧→新
- 过渡结束后 blendFactor=1 → state = PLAYING, transitionSource = null
- `effectiveWeight` = 适用于过渡源时 1f，否则 blendFactor
- **修复**: emptyMask 同步——snapshotTransitionSource 复制 emptyMask；crossfadeStep lerp 后更新 emptyMask

### 事件系统

```
tickAdvance 事件顺序:
  TickPre → tickHandler → TickHandlerPost → [主逻辑] → TickPost
```

事件在 AnimationControllerManager.firePendingEvents() 中统一执行：

- 时间线：双端（服务端+客户端）
- 音效/粒子：仅客户端，从 mergedProxy 解析骨骼/定位器位置

### AnimType（覆盖动画自身 loop）

| 类型           | 行为                |
|--------------|-------------------|
| DEFAULT      | 使用动画自身的 loop 类型   |
| PLAY_ONCE    | 播放一次后淡出           |
| STOP_AT_LAST | 播放一次，停止于最后一帧（不淡出） |
| LOOP         | 强制循环              |

### 控制器注册

`AnimationControllerRegisterEvent` 注册预置控制器：

| 名称      | Priority | 角色   |
|---------|----------|------|
| ACTION  | 1000     | 动作层  |
| MAIN    | 0        | 主控制器 |
| COMMAND | -1000    | 命令层  |

`PlayerAnimationMapper.init` 中通过 NeoForge.EVENT_BUS.post 获取排序条目，添加到 `AnimationControllerManager`。

### 已修复问题记录

1. `stop()` 不清理 transitionSource → 淡出无效。添加 `transitionSource = null`
2. crossfade 首帧闪新动画。trigger 末尾立即执行 crossfadeStep()
3. tickRender 删除过渡中的旧骨骼。改为仅在 PLAYING 时清理
4. trigger() 中 affectedBones 未更新。捕获 computeAndWrite 返回值
5. TickHandlerPost 发成 TickHandlerPre。修正事件类型
6. TRANSITIONING→PLAYING 时 lastRawGameTime=-1 导致 delta=0。初始化 lastRawGameTime
7. snapshotTransitionSource/crossfadeStep 不更新 emptyMask → remerge 跳过骨骼。同步 emptyMask
## 操作规则

### 文件删除规则

删除任何文件或目录之前：

1. 列出要删除的内容
2. 检查项目内所有引用
3. 先将文件移动到新位置（不应立即删除）
4. 编译验证通过后，再执行删除

### 命名约定

- **mixed/**（不是 mixin/）：Mixin 接口扩展（如 IPlayerRcf），与 Java Mixin 类路径 mixin/ 区分

### 文件写入规则

使用 PowerShell Set-Content 时会自动追加一个尾部换行。写入前必须去掉尾部换行防止多出空行：

```powershell
$content = $content.TrimEnd("`r", "`n")
Set-Content $file -Value $content -Encoding UTF8
```

### 文件修改备份规则

在修改重要文件之前，先将原始文件复制到 `.migration_plan/` 目录下的对应路径中，方便恢复：

```powershell
# 例如修改 EntityAnimationMapper.kt 前：
Copy-Item "path/to/EntityAnimationMapper.kt" ".migration_plan/path/to/EntityAnimationMapper.kt.bak"
```

`.migration_plan/` 目录结构镜像项目源码结构，备份文件后缀为 `.bak`。修改完成并编译通过后，可清理不再需要的备份。

### 杂项文件归档规则

所有非源码、非构建、非运行时的杂项资源统一存放在项目根目录 `_archived/` 中，按类型分类：

| 子目录                  | 内容                 |
|----------------------|--------------------|
| `scripts/`           | 根目录工具脚本（temp_*.py） |
| `docs/`              | TODO.md            |
| `migration_backups/` | 迁移过程中的 .bak 版本备份   |
| `migration_scripts/` | 迁移用的 Python 脚本     |
| `migration_misc/`    | 迁移暂存的中间代码和 .ps1 脚本 |

**不纳入归档的内容：**

- `.claude/`, `.cursor/`, `.continue/` — IDE 技能配置，保持原位
- `build/`, `run/` — 构建和运行时产物，Gradle/Minecraft 自动管理
- `module/*/src/` — 源码
- `额外资源/` — mod jar 和资源文件，保持原位不动
- 各模块根目录的 `CLAUDE.md` — 项目配置

