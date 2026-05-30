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

## 玩家动画系统（ResonatorCombatFramework）

### 核心文件

| 文件                            | 路径              | 职责                                                         |
|-------------------------------|-----------------|------------------------------------------------------------|
| BedrockAnimation.kt           | bedrock/        | 数据模型：BrBoneKeyFrame、BrBoneAnimation、MolangVector3、LerpMode |
| BedrockAnimator.kt            | bedrock/        | 实际插值计算，匹配 eyelib 风格                                        |
| BedrockAnimationController.kt | controller/     | 动画控制器实现，驱动 tickBackend 和 animTime                          |
| BaseAnimationController.kt    | controller/     | 控制器基类：状态机、crossfade、blend、播放边界检查                           |
| BedrockAnimationRegistry.kt   | registry/       | 从 JSON 加载动画资源                                              |
| EasingTypes.kt                | bedrock/molang/ | 仅保留 LINEAR、STEP、catmullRom                                 |

### 关键数据模型

**BrBoneKeyFrame** 有三个取值方法，使用 fallback 链：

- `evaluateValue()`: value → post → pre → (0,0,0)
- `evaluatePre()`: pre → value → post → (0,0,0)
- `evaluatePost()`: post → value → pre → (0,0,0)

这确保对象格式（`{"post": [x,y,z], "lerp_mode": "catmullrom"}`）和简单数组格式（`[x,y,z]`）的关键帧都能正确取值。旧代码中
`evaluateValue()` 只读 `value` 字段，导致 post-only 关键帧错误返回 (0,0,0)。

**LerpMode** 嵌套在 BrBoneKeyFrame 内作为枚举：

- `LINEAR` — 线性插值（prev 用 evaluatePost，next 用 evaluatePre）
- `CATMULLROM` — 4 点 Catmull-Rom 样条插值
- `STEP` — 阶梯插值

### 插值逻辑（BedrockAnimator.interpolate）

参考 eyelib 的 `BrBoneAnimation.lerp` 实现：

1. **二分查找前后关键帧**: `indexOfFirst { it.time > time }`

- `afterIdx < 0` → PAST_END，返回最后一帧
- `afterIdx == 0` → BEFORE_START，返回第一帧
- 其他 → 正常插值

2. **LINEAR 插值**: `prev.evaluatePost()` → `next.evaluatePre()` 做线性 lerp

3. **CATMULLROM 插值**:

- 查找 beforePlus 和 afterPlus 作为曲线控制点
- `useFirstPoint = beforePlus != null && !(before.hasPreData && before.hasPostData)` — 当 before 有完整 pre+post
  控制点时，不扩展 beforePlus
- `useLastPoint = afterPlus != null && !(after.hasPreData && after.hasPostData)` — 同理
- 用 `lerpSplineCurve()` 做每轴独立的分段 Catmull-Rom 样条求值

4. **权重修正**: `adjWeight = weight + (useFirstPoint ? 1 : 0)`，归一化后传入 lerpSplineCurve

### setXxxEmpty 标记

`computeAndWrite` 中，`setPosEmpty`/`setRotEmpty`/`setScaleEmpty` 决定该通道是否被此动画"参与"：

- `true` = 此通道无关键帧，不覆盖，保持原值/其他动画的值
- `false` = 此通道有关键帧，即使最终值全是 0/(1,1,1) 也是主动设置的值

### 循环类型解析

`BedrockAnimationRegistry.parseAnimations` 中 loop 解析：

```kotlin
val loopEl = animDef.get("loop")
val loop = when {
    loopEl?.isJsonPrimitive == true && loopEl.asBoolean -> LoopType.LOOP
    loopEl?.asString == "loop" -> LoopType.LOOP
    loopEl?.asString == "hold_on_last_frame" -> LoopType.HOLD_ON_LAST
    else -> LoopType.ONCE
}
```

同时支持 Bedrock 格式的布尔值 `"loop": true` 和字符串 `"loop": "loop"`。

### EasingTypes

精简后的 EasingTypes 只保留：

- `LINEAR: EasingFunc` — `t → t`
- `STEP: EasingFunc` — `t < 1 ? 0 : 1`
- `catmullRom(t, p0, p1, p2, p3): Double` — 标准 Catmull-Rom 公式
