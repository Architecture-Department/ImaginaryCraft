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
- **eyelib** — 玩家动画计算引擎
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
