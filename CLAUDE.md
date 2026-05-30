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
    ├── ResonatorCombatFramework/ — 战斗框架（玩家动画/装备渲染）
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
- 模块 `build.gradle` 通过 `ext` 辅助方法声明依赖：`addModuleDeps(...)`, `addJEI()`, `addMixinSquared()`, `addEyelib()`,
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

## 玩家动画系统（ResonatorCombatFramework）

### 核心文件

| 文件                            | 路径              | 职责                                                         |
|-------------------------------|-----------------|------------------------------------------------------------|
| BedrockAnimation.kt           | bedrock/        | 数据模型：BrBoneKeyFrame、BrBoneAnimation、MolangVector3、LerpMode |
| BedrockAnimator.kt            | bedrock/        | 实际插值计算                                                     |
| BedrockAnimationController.kt | controller/     | 动画控制器实现，负责 time 推进 + Molang 求值                             |
| BaseAnimationController.kt    | controller/     | 控制器基类：状态机(IDLE/TRANSITIONING/PLAYING/PAUSED/FADING_OUT)、crossfade 过渡、blend 混合、播放边界检查。每个控制器持有自己的 `activeBoneConfig` 和额外的 `boneConfigs` 字段 |
| ControllerManager.kt          | controller/     | 控制器管理器：Map<ResourceLocation, IAnimationController> O(1) 查找 + List 保持插入顺序。先添加的优先级更高 |
| IAnimationController.kt       | controller/     | 控制器接口：trigger/stop/pause/resume/tick 生命周期                  |
| BedrockAnimationRegistry.kt   | registry/       | 从 JSON 加载动画资源                                              |
| EasingTypes.kt                | bedrock/molang/ | 仅保留 LINEAR、STEP、catmullRom                                 |

### 映射器与渲染

| 文件                             | 路径     | 职责                     |
|--------------------------------|--------|------------------------|
| EntityAnimationMapper.kt       | mapper/ | 抽象映射器：管理 ControllerManager、路由 trigger/stop/pause/resume |
| PlayerAnimationMapper.kt       | mapper/ | 玩家映射器：init 中通过 NeoForge.EVENT_BUS.post 注册控制器，tickAndRender 逐控制器渲染 |
| HumanoidEntityAnimationMapper.kt | mapper/ | 人形骨骼映射：proxyModel → HumanoidModel 转换 |
| LivingEntityAnimationMapper.kt | mapper/ | 生物映射器基类：骨骼标志收集         |
| AnimationPlayConfig.kt         | config/ | 播放配置 data class：animId/controllerName/animType/startTime/endTime/speedMultiplier 等 |
| ProxyBoneConfigData.kt         | config/ | 骨骼配置数据：transitionTicks/resolveBoneFlags |
| IAnimationMapper.kt            | api/    | 映射器接口：trigger/stop/pause/resume/addController |

### 控制器注册

`AnimationControllerRegistry` 定义四个预置控制器，通过 `AnimationControllerRegisterEvent` 注册：

| 名称           | Priority | 角色       |
|--------------|----------|----------|
| ADDON        | 2000     | 附加层（最高优先级） |
| DEFAULT      | 1000     | 默认控制器     |
| LOWER_BODY   | 500      | 下半身控制器    |
| UPPER_BODY   | 400      | 上半身控制器（最低） |

`PlayerAnimationMapper.init {}` 通过 `NeoForge.EVENT_BUS.post(AnimationControllerRegisterEvent())` 获取排序后的条目，直接调用 `controllerManager.add(name, factory(isClient))` 添加。所有控制器（包括 DEFAULT）都在 nameMap + ordered 中。

### 控制器管理系统

`ControllerManager` 使用双集合（Map O(1) 查找 + List 保持顺序）管理控制器，支持 `addAfter/addBefore` 灵活插入。先添加的优先级更高。

**`getRenderable()` 逻辑：** 从高优先级到低优先级遍历活跃控制器。如果一个控制器的所有骨骼都已被更高优先级控制器渲染，且自身没有 `isOverriding` 标志，则跳过。`isOverriding` 允许低优先级控制器覆盖高优先级控制器的活跃骨骼。

### 触发流程

1. 外部调用 `mapper.trigger(animId)` → 创建 `AnimationPlayConfig(animId, controllerName=DEFAULT)`
2. `EntityAnimationMapper.trigger(config)` → `controllerManager.get(controllerName) ?? defaultController`
3. `defaultController` 通过 `controllerManager.get(AnimationControllerRegistry.DEFAULT)` 查找，不命中时 fallback 到 `controllerManager.getDefault()`（第一个添加的控制器）
4. 设置控制器的 `resolvedBoneConfig` 和 `boneConfigs`（持久覆盖）
5. `controller.trigger(config)` → `BaseAnimationController.trigger()`

### 每帧渲染（逐控制器）

`PlayerAnimationMapper.tickAndRender()` 被 `LivingEntityRendererMixin` 调用：

```kotlin
tick(tickSec, deltaSec)
for (ctrl in controllerManager.getRenderable()) {
    val bac = ctrl as BaseAnimationController
    val flags = bac.resolveBoneFlags(bac.currentAnimTime)  // 合并 activeBoneConfig + boneConfigs
    val weight = ctrl.effectiveWeight
    applyRootTransform(listOf(bac.proxyModel), poseStack, flags, weight)
    applyProxyToModel(listOf(bac.proxyModel), model, flags, weight)
}
```

每个控制器用自己的权重和骨骼标志独立渲染，高优先级先渲染，低优先级后渲染可覆盖。

### 骨骼配置管理

- **`activeBoneConfig`**（private）：每个控制器自己的活跃骨骼配置，trigger 时设置，forceClear 时清除
- **`boneConfigs`**（var, nullable）：额外骨骼配置，优先级高于 activeBoneConfig，只覆盖已存在的骨骼。通常 null，由 EntityAnimationMapper.trigger 设置
- **`resolvedBoneConfig`**（internal, nullable）：trigger 时临时覆盖，设完后在 trigger() 内立即消费

### 状态机

`BaseAnimationController.State`：
- IDLE → trigger() → TRANSITIONING → blendFactor=1 → PLAYING
- PLAYING → checkPlaybackBounds → FADING_OUT → blendFactor=0 → IDLE
- PLAYING/TRANSITIONING → pause() → PAUSED → resume() → TRANSITIONING/PLAYING

### 循环类型解析

`"loop": true`（布尔值）和 `"loop": "loop"`（字符串）均支持。

### AnimType（覆盖动画自身 loop 的设置）

| 类型            | 行为                    |
|---------------|-----------------------|
| DEFAULT       | 使用动画自身的 loop 类型       |
| PLAY_ONCE     | 播放一次后淡出               |
| STOP_AT_LAST  | 播放一次，停止于最后一帧（保持姿态不淡出） |
| LOOP          | 强制循环播放                |
