# ImaginaryCraft 项目技能

这是一个完整的项目记忆/技能文档，记录了 ImaginaryCraft 项目的结构、架构模式、重要 API 和代码规范。用于快速恢复项目上下文。

---

## 1. 项目概况

基于 **NeoForge 1.21.1** 的 Minecraft 模组项目，使用 **Kotlin**（KotlinForForge）作为主要开发语言，**Java** 仅用于 Mixin
类。采用**多模块 Gradle 架构**，最终通过聚合模块 ImaginaryCraft 用 jarJar 将所有子模块打包为单一产物。

- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.230
- **Kotlin**: 2.2.20 (KotlinForForge 5.11.0)
- **GeckoLib**: 4.8.4
- **Curios API**: 9.5.1+1.21.1
- **Kotlin 编译器插件**: kotlin.plugin.allopen, kotlin.plugin.serialization, ksp (+ kmixin 0.1.2)

### 模块列表

| 模块                       | mod_id                     | 说明                          |
|--------------------------|----------------------------|-----------------------------|
| GoldenBoughsLib          | goldenboughs_lib           | 核心库 — 伤害系统/属性/美德/行为树/HUD/渲染 |
| ResonatorCombatFramework | resonator_combat_framework | 战斗框架 — 实体动画/装备渲染            |
| EGOCurios                | ego_curios                 | 饰品模组 — ~50 E.G.O.饰品，15槽位    |
| EGOEquipment             | ego_equipment              | 装备模组 — ~110武器 + ~30护甲套装     |
| Abnormalities            | abnormalities              | 异想体模组                       |
| ImaginaryCraft           | imaginarycraft             | 聚合模组 — jarJar 打包所有子模块       |

### 模块依赖图

`
GoldenBoughsLib  ←───  ResonatorCombatFramework  ←───  EGOCurios
    ↑                         ↑                           ↑
    ├─────────────────────────┴───────────────────────────┤
    │                                                     │
    └──→  EGOEquipment  ──→  Abnormalities  ──→  ImaginaryCraft (聚合)
`

---

## 2. 项目架构与构建系统

### 2.1 构建文件结构

- **build.gradle** (根) — 插件声明 + subprojects 块应用各插件
- **build-conventions.gradle** — 所有模块集中化配置：仓库、通用依赖、NeoForge配置、模板合并、DataGen、发布
- **gradle.properties** — 所有版本号统一管理
- **settings.gradle** — 模块注册（使用 include "module:<Name>"）
- **create_module.sh** — 快速新建模块脚本

### 2.2 模块 build.gradle 模式

每个模块通过 apply from: rootProject.file('build-conventions.gradle') 继承公共配置，通过辅助方法声明依赖:

`
dependencies {
    addModuleDeps('GoldenBoughsLib', 'ResonatorCombatFramework')  // 模块间依赖
    addMixinSquared()       // MixinSquared 编译/内嵌
    addJade()               // Jade 高亮显示
    addJEI()                // JEI 配方查看
    addCurios()             // Curios API 饰品栏
    addDummyMobs()          // 测试假人
    addLocalLibs()          // 本地 libs/ 目录
}
`

ImaginaryCraft 聚合模块使用 addJarJarModuleDeps() 将所有子模块 jarJar 内嵌。

### 2.3 neoforge.mods.toml 生成

- 公共基础模板: templates/mods-base.toml (modLoader=kotlinforforge, neoforge/minecraft/kotlinforforge 依赖)
- 模块特有内容: src/main/templates/META-INF/neoforge.mods.toml
- 构建时通过 mergeModsTemplate 任务合并

### 2.4 数据生成 (DataGen)

每个模块通过 NeoForge 的 data run 生成，输出到 src/generated/resources/。

---

## 3. 核心业务领域

### 3.1 脑叶公司伤害系统 (LC Damage System)

核心枚举: LcDamageType — 四种伤害类型

| 类型       | 颜色      | 说明              |
|----------|---------|-----------------|
| PHYSICS  | #ff0000 | 物理伤害            |
| SPIRIT   | #ffffff | 精神伤害            |
| EROSION  | #8a2be2 | 侵蚀伤害（同时造成物理+精神） |
| THE_SOUL | #00ffff | 灵魂伤害            |

LcLevel — 脑叶等级: ZAYIN(1) > TETH(2) > HE(3) > WAW(4) > ALEPH(5)

易伤属性 (LibAttributes):

- PHYSICS_VULNERABLE / SPIRIT_VULNERABLE / EROSION_VULNERABLE / THE_SOUL_VULNERABLE
- 默认值: PHYSICS=1.0, SPIRIT=1.0, EROSION=1.5, THE_SOUL=2.0
- PHYSICS_DEFENSE / SPIRIT_DEFENSE / EROSION_DEFENSE / THE_SOUL_DEFENSE

伤害计算在 LcDamageEventExecutes 中处理，通过 Mixin 注入 CombatRules 和 LivingDamageEvent.Post。

### 3.2 四德系统 (Virtue System)

VirtueType: FORTITUDE(勇气>最大生命), PRUDENCE(谨慎>最大理智), TEMPERANCE(自律>挖掘/击退/工作), JUSTICE(正义>
移动/游泳/攻击速度), COMPOSITE(综合)

VirtueRating: I(1), II(30), III(45), IV(65), V(85), EX(101)

核心: AbstractVirtue — Attachment 持久化 + Attribute Modifier 影响玩家属性。
VirtueAttributeModifier — 物品属性加成，通过 getAttributeModifiers() 返回 Multimap。

### 3.3 理智系统 (Rationality)

- MAX_RATIONALITY — MinAttribute，默认20.0，最小0.0
- RATIONALITY_NATURAL_RECOVERY_WAIT_TIME
- RATIONALITY_RECOVERY_AMOUNT

---

## 4. 物品系统 (Item System)

### 4.1 E.G.O. 物品接口层次

`
IEgoItem — 所有E.G.O.物品顶级接口
  +-- IEgoWeaponItem — 武器（继承 IItemUsageReq + IItemLcDamageType）
        +-- IMeleeEgoWeaponItem — 近战武器
        +-- IRemoteEgoWeaponItem — 远程武器
        +-- IGunWeapon — 枪械武器接口
`

### 4.2 Builder 模式

`
IEgoWeaponItem.Builder()
    .damage(6f)
    .meleeLcDamageType(LcDamageType.PHYSICS)
    .attackSpeed(1.6f).attackDistance(3.0f)
    .virtueUsageReq(fortitude=III, prudence=II, temperance=null, justice=null, composite=III)
`

virtueUsageReq 支持 Int 和 VirtueRating 两种重载。

### 4.3 武器注册模式 (EGOEquipment)

`
val WINGBEAT = onMelee<MeleeEgoWeaponItem>()
    .id("wingbeat_weapon").zhName("翅振")
    .lcLevelType(LcLevel.ZAYIN).type(MeleeTemplateType.SWORDS)
    .properties(Item.Properties())
    .properties { it.damage(6f).meleeLcDamageType(LcDamageType.PHYSICS).virtueUsageReq(...) }
    .model(EGOEquipment.modRl("weapon/wingbeat"))
    .buildAndRegister()
`

MeleeTemplateType: AXE, FIST, HAMMER, KNIFE, MACE, SPEAR, SWORDS
RemoteTemplateType: CANNON, PISTOL, RIFLE, CROSSBOW

### 4.4 护甲系统 (EgoArmorItem)

`
EgoArmorItem.Builder()
    .virtueUsageReqBuilder(...)
    .vulnerable(physics=0.0, spirit=0.0, erosion=0.0, soul=0.0)
    // 配合 ArmorMaterial + Type + GeoRenderProvider
`

### 4.5 饰品系统 (EgoCurioItem)

`
EgoCurioItem.of<EgoCurioItem>()
    .fortitude(2).prudence(3).temperance(0).justice(6)
    .model(EGOCurios.modRl("curios/item_name"))
    .addTooltip("饰品描述文本")
    .properties(Item.Properties())
`

15个槽位: HEADWEAR, CHEEK, HEAD, HINDBRAIN, EYE, FACE, MASK, MOUTH, NECK, BROOCH, HAND, GLOVE, BACK

### 4.6 数据组件

- ITEM_VIRTUE_USAGE_REQ — 美德需求
- IS_RESTRAIN — 压制状态 (Boolean)

---

## 5. AI 系统 — 行为树

### 5.1 节点类型

Composite: SequenceNode, SelectorNode, ParallelNode, WeightNode
Decoration: ConditionNode, InverterNode, RepeaterNode, RepeatUntilNode, InterruptNode, F2TNode, TimeControlNode
Condition: AndCondition, OrCondition, NotCondition, DistanceLowerThanCondition, AngleLowerThanCondition,
HealthLowerThanCondition, TargetExistCondition, EntityDataCondition, NavigationCondition, TimeCondition
Leaf: MoveToTargetAction, JumpAttackAction, DashAction, AnimTriggerAction, ShootAction, WaitAction, RandomStrollAction,
SyncAction 等

### 5.2 工厂方法 (BTFactory)

`
BTFactory.sequence() / BTFactory.selector() / BTFactory.parallel(policy)
BTFactory.wait(ticks) / BTFactory.infinite(node) / BTFactory.repeat(node, n) / BTFactory.inverter(node)
`

### 5.3 接口

- IBehaviorTreeMob<T> — createBehaviorTree() 返回 BTRoot
- IStateChangeableMob — 可切换状态
- ISkillExpand — 技能扩展
- IBlackboardHolder + KeyType + Blackboard — 黑板系统

### 5.4 目标 (Goals)

ModHurtByTargetGoal, CampHurtByTargetGoal, ModMeleeAttackGoal, DashComponent

---

## 6. 战斗框架 (RCF)

- IPlayerRcf / IEntityRcf — 玩家/实体 RCF 接口
- AnimationController — 抽象动画控制器
- IAnimationMapper / PlayerAnimationMapper — 动画映射
- ProxyModel / ProxyBoneState — 代理骨骼模型
- RcfFirstPersonRender — 第一人称渲染

---

## 7. HUD/UI 系统 (GoldenBoughsLib)

- IHudLayer / BasicHudLayer / CompositeHudLayer
- 状态栏: StatusBarLayer, NewHealthBarLayer, RationalityBarLayer, LeftBarLayer
- 护盾层: ShieldBarLayer, PhysicShieldLayer, SpiritShieldLayer, ErosionShieldLayer, SoulShieldLayer
- 滤镜层: LcDamageScreenFilterLayer, RationalityScreenFilterLayer
- 组件: HorizontalStatusBar, ImageProgressBar
- 粒子: DamageTextParticle, TextParticle, LcDamageIconParticle
- 渲染: GeoItemRendererExpand, GeoArmourRenderProvider, AutoGlowingRenderLayer

---

## 8. 渲染系统

- GeoItemModel / GuiGeoItemModel / GeoEntityModel / GeoCurioModel / ModGeoArmorModel
- GeoItemRenderProvider / GeoArmourRenderProvider / RoughAndFineArmorRenderer
- EmptyEntityModel / EmptyMobRenderer / EmptyLivingEntityRenderer

---

## 9. 事件系统

GoldenBoughsLib 定义了自定义事件：

- ChopFlavorLayerEvent — 风味层事件
- AddItemDataComponentTooltipEvent — 数据组件 Tooltip
- RationalityEvent — 理智变更事件

mod 通过 eventexecute 包处理事件逻辑，events 包注册 NeoForge 事件监听器。

---

## 10. 代码规范与命名约定

### 10.1 包结构

`
architecture.<module_id>/
    +-- core/           — 主类 + 常量 + 事件钩子
    +-- init/           — 注册（物品/方块/实体/属性/音效）
    |   +-- tag/        — 标签定义
    +-- api/            — 公开接口 + 构建器
    +-- common/         — 通用实现
    |   +-- item/       — 物品实现
    |   +-- entity/     — 实体实现
    |   +-- payload/    — 网络包
    |   +-- mobeffect/  — 状态效果
    +-- client/         — 客户端代码
    |   +-- renderer/   — 渲染器
    |   +-- model/      — 模型
    |   +-- gui/        — HUD/UI
    |   +-- particle/   — 粒子
    +-- datagen/        — 数据生成
    |   +-- i18n/       — 国际化
    |   +-- tag/        — 标签生成
    +-- events/         — 事件注册
    +-- eventexecute/   — 事件执行逻辑
    +-- event/          — 自定义事件定义
    +-- config/         — 配置
    +-- util/           — 工具
    +-- mixed/          — Mixin 接口（Kotlin，用于实现注入）
    +-- mixin/          — Mixin 类（Java）
`

### 10.2 命名规范

- 接口: I 前缀（IEgoItem, IPlayerRcf）
- Mixin 唯一字段/方法: $ 前缀
- 包名: 全小写 snake_case
- 常量/枚举值: UPPER_SNAKE_CASE
- 模组主类: object（Kotlin 单例）
- 注册类: LibXxx / EGOXxx / ModXxx 命名

### 10.3 模块主类模式

所有模块主类 follow 相同模板：

- @Mod(ID) 标注
- object 单例
- 包含 ID, NAME, LOGGER, modRl(name), modRlText(name), modRegister(registry) 静态方法
- init 块中通过 LOADING_CONTEXT / MOD_BUS 注册各类 DeferredRegister
- @EventBusSubscriber 标注

### 10.4 Mixin 规范

- Java 文件放在 src/main/java/
- Mixin 接口（Interface Injection）放在 Kotlin 的 mixed/ 包
- 接口命名: I<目标类>Lib / I<模块> 模式
- 使用 ksp + kmixin 进行 Kotlin Mixin 编译支持

### 10.5 国际化模式

每个模块的 datagen 包中通过静态 MAP 收集 i18n 文本，DataGen 运行时批量输出。

### 10.6 AllOpe 注解

@AllOpe — 自定义 Kotlin all-open 注解，使标记的类和接口在编译时保持 open。

### 10.7 Interface Injection

每个模块提供 META-INF/interfaces.json，用于 Mixin Interface Injection。

---

## 10.8 MoLang 表达式系统

MoLang 是 RCF 的表达式求值引擎，用于动画控制器中的变量计算和条件判断。

### 核心类：MolangData

MolangData 是实体级变量仓库 + 求值上下文。作为 AttachmentType 附加到 Entity 和 Level。

`kotlin
class MolangData(variables: MutableMap<String, DoubleSupplier> = HashMap())
    val vars: MutableMap<String, DoubleSupplier>
    var thisValue: DoubleSupplier?
    fun resolve(name: String): Double
    fun assign(name: String, value: Double)
    fun updateAnimQueries(entity, animTime, deltaTime)
    companion object
        67 个 query.* 常量（含中文注释）
        fun of(holder: Any?): MolangData
`

### 查询初始化策略

所有实体状态查询通过 initEntityQueries() 一次性初始化，在 of() 首次调用时执行。
查询以 DoubleSupplier lambda 形式存储——闭包捕获实体引用，每次 getAsDouble() 时从实体实时读取，无需每帧重建。

updateAnimQueries() 仅处理动画专属值（anim_time, delta_time），这些值由控制器每帧更新。

### 求值流程

`
AnimationControllerManager.tickAnimations(mapper)
  -> MolangData.of(holder)  -> 获取/初始化实体 MolangData
  -> ctrl.currentData = data -> 传给控制器
  -> tickBackend -> data.vars[ANIM_TIME] = ...
  -> expr.get(data) -> Variable.get(data) -> data.resolve(name)
`

### 变量作用域

| 类型   | 前缀             | 存储                      |
|------|----------------|-------------------------|
| 实体变量 | variable. / v. | MolangData.vars         |
| 临时变量 | temp. / t.     | MolangData.vars         |
| 查询值  | query. / q.    | MolangData.vars（惰性求值）   |
| 上下文  | context.       | MolangData.contexts（只读） |

### MolangValue 多态求值

MolangValue 接口提供多个便捷 get() 重载：

- get(context: MolangData?) -- 显式传参求值
- get(entity: Entity) -- 从实体获取 MolangData 后求值
- get(proxyProvider) -- 从动画代理提供者获取求值
- get(controller) -- 从控制器获取 currentData 后求值
- get(controllerManager) -- 从管理器获取实体后求值

### AST 节点

Constant, Variable, VariableAssignment, Calculation, BooleanNegate, Negative, Ternary, Group, CompoundValue, BlockExpr,
ReturnExpr, LoopExpr, ForEachExpr, This, BreakExpr, ContinueExpr + 29 个 math.* 函数

### 动画事件系统

BrBedrockAnimation 包含三个事件列表，按时间触发：

| 事件类型 | JSON 字段          | 类                   | 触发时机                               |
|------|------------------|---------------------|------------------------------------|
| 音效   | sound_effects    | BrAnimationSound    | animTime >= time，播放 SoundEvent     |
| 粒子   | particle_effects | BrAnimationParticle | animTime >= time，在客户端生成粒子          |
| 时间线  | timeline         | BrAnimationTimeline | animTime >= time，执行 Molang/命令/实体事件 |

#### 职责分离

事件触发分为"收集"和"执行"两个阶段：

1. **收集**：`BedrockAnimationController.collectEventsAt(anim)` — 遍历 sounds/particles/timelines，对比 `animTime`，查
   `firedEvents` 集合防重复，返回 `AnimationEventsToFire` 容器
2. **入队**：`AnimationControllerManager.queueEvents(events)` — 控制器收集后入队到 `pendingEvents` 列表
3. **执行**：`AnimationControllerManager.firePendingEvents()` — 在 `tickAnimations` 的 `remerge()` 后统一调用：
  - 时间线事件：双端直接执行 `it.apply(entity, data)`
  - 音效/粒子事件：仅客户端，通过 `IEntityAnimationMapper` 解析骨骼位置后执行

#### ProxyBone 变换分离

ProxyBone 区分两类变换字段：

| 字段                           | 类型     | 说明                                 |
|------------------------------|--------|------------------------------------|
| localPos/localRot/localScale | 局部     | 动画系统写入的原始值（keyframe 插值、remerge 合并） |
| pos/rotation/scale           | 模型空间累积 | 继承计算后，从父到子累加的结果，供渲染使用              |

### 继承变换计算

`computeInheritedTransforms(enableInheritance, target, vararg noInheritNames): ProxyModel`

- 在 `remerge()` 后调用，将 local 值累积为模型空间值
- `target` 指定计算目标，默认 `mergedProxy`
- `noInheritNames` 指定不继承父变换的骨骼名（vararg）
- 返回 `target`，累加规则：`pos = parent.pos + localPos`，`rotation = parent.rotation + localRot`，
  `scale = parent.scale * localScale`

### 服务端/客户端更新分离

| 方法                       | 调用方           | 行为                                       |
|--------------------------|---------------|------------------------------------------|
| `tickAnimations()`       | 实体 tick（仅服务端） | 控制器 tick → remerge → 完整继承 → 事件触发         |
| `tickAnimationsClient()` | 渲染帧（仅客户端）     | remerge → root 过滤继承 → 事件触发               |
| `tickAndRender()`        | 渲染帧（仅客户端）     | tickRender → tickAnimationsClient → 直接渲染 |

`tickAnimations()` 现在有 `if (!isClient)` 守卫，仅在服务端执行。客户端渲染帧通过 `tickAnimationsClient()` 处理骨骼合并和继承计算，无需
`prevMergedProxy` 插值。

### 额外骨骼系统

| 概念                                        | 说明                                       |
|-------------------------------------------|------------------------------------------|
| ProxyBoneConfigData.extraBones            | JSON xtra_bones 段定义的额外 BrBone 几何骨骼      |
| BedrockAnimationController.extraBones     | 当前动画的额外骨骼，	rigger() 时加载，orceClear() 时清除 |
| AnimationControllerManager.rebuildBones() | 合并所有控制器的额外骨骼到 manager.bones              |
| AnimationControllerManager.bones          | 最终使用的骨骼映射（brModel 基础 + 所有控制器额外骨骼，同名后覆盖前） |

额外骨骼仅为几何定义（name/parent/pivot/cubes），不含动画变换（localPos/Rot/Scale 不会被修改）。

### 骨骼位置解析

`AnimationControllerManager.resolveBonePos()` — 从 `mergedProxy` 中按 `boneName`（JSON 中的 `locator` 字段）查找：

1. 查骨骼下的定位器位置
2. 查骨骼自身位置
3. 都找不到返回 `Vector3d()`

#### IEntityAnimationMapper 客户端事件方法

接口中提供两套方法（默认实现检查 `if (!isClient) return`）：

**坐标基方法**（传入已解析的 Vector3d）：

- `playSoundEffect(sound, bonePos, molangData)` — 调用 `sound.apply(entity, bonePos, data)`
- `playParticleEffect(particle, bonePos, molangData)` — 调用 `particle.apply(entity, bonePos, data)`

**模型方法**（从 EntityModel 查找骨骼位置）：

- `playSoundEffect(sound, model, molangData)` — 调用 `resolveBoneWorldPos(boneName, model)` 获取位置后执行
- `playParticleEffect(particle, model, molangData)` — 同上
- `resolveBoneWorldPos(boneName, model)` — 默认返回实体位置。`EntityAnimationMapper` 覆写为从 `mergedProxy` 查找

#### 完整调用链

```
// 服务端（实体 tick）— tickAnimations()
  控制器 ticks
      → tickBackend → collectEventsAt(anim) → queueEvents
  → remerge()：合并各控制器的 localPos/localRot/localScale
  → computeInheritedTransforms(true, mergedProxy)：完整继承
  → firePendingEvents()
      → timelines: 双端执行
      → sounds/particles: 仅客户端
          mapper.playSoundEffect(it, resolveBonePos(it), data)

// 客户端（渲染帧）— tickAndRender()
  各控制器 tickRender(deltaSec)
  → tickAnimationsClient()
      → remerge()
      → computeInheritedTransforms(true, mergedProxy, "root")  // root 不过滤
      → firePendingEvents()
  → applyRootTransform(mergedProxy, poseStack)
  → applyProxyToModel(mergedProxy, model)
```

#### 事件数据类

- `BrAnimationSound.Effect.apply()`：播放音效，支持 `bindToActor` 绑定到实体位置，`boneName` 取自 JSON `locator` 字段
- `BrAnimationParticle.Effect.apply()`：生成粒子，仅客户端，支持 `locator` 骨骼定位、`pre_effect_script` 预执行脚本
- `BrAnimationTimeline.apply()`：评估 MoLang 表达式、执行以 `/` 开头的命令、处理以 `@` 开头的实体事件

#### 事件追踪

`BedrockAnimationController.firedEvents` 集合防止同一事件在单次播放中被重复触发。`trigger()` 或循环重播
`resetAnimAndRestart()` 时自动清空。

### Data Attachment

- RcfAttachmentTypes.MOLANG_DATA -> AttachmentType<MolangData>，附加到 Entity 和 Level
- 注册在 Rcf.kt init 块中通过 REGISTRY.register(modBus)
- 获取方式：MolangData.of(entity) 或 entity.getData(...)
## 11. 关键工具类

| 工具类              | 所在模块            | 功能          |
|------------------|-----------------|-------------|
| ItemBuilderUtil  | GoldenBoughsLib | 属性修改器快速构造   |
| LcLevelUtil      | GoldenBoughsLib | LcLevel 工具  |
| LcDamageTypeUtil | GoldenBoughsLib | 伤害类型工具      |
| VirtueUtil       | GoldenBoughsLib | 美德工具        |
| RationalityUtil  | GoldenBoughsLib | 理智工具        |
| GunWeaponUtil    | GoldenBoughsLib | 枪械武器工具      |
| LivingEntityUtil | GoldenBoughsLib | 实体工具        |
| WorldUtil        | GoldenBoughsLib | 世界工具        |
| TextUtil         | GoldenBoughsLib | 文本工具        |
| GeckoLibUtil     | GoldenBoughsLib | GeckoLib 工具 |
| PayloadUtil      | GoldenBoughsLib | 网络包工具       |



