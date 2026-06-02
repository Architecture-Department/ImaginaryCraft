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



