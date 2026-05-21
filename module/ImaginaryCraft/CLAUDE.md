# ImaginaryCraft

聚合模块。通过 jarJar 将所有子模块打包为单一 NeoForge mod 产物。

## 项目结构

```
src/main/
├── java/architecture/imaginarycraft/
│   └── mixin/                      — 预留（当前无 mixin 实现）
│
├── kotlin/architecture/imaginarycraft/
│   ├── core/
│   │   ├── ImaginaryCraft.kt       — @Mod("imaginarycraft") 入口
│   │   ├── ImaginaryCraftClient.kt — 客户端入口
│   │   └── ImaginaryCraftConstants.kt
│   ├── init/
│   │   └── ModSoundEvents.kt       — 音效注册
│   ├── util/
│   │   └── WorldUtil.kt            — 工具（预留）
│   └── datagen/
│       ├── Datagen.kt              — 数据生成调度（GatherDataEvent）
│       ├── ModDatagenBlockState.kt
│       ├── ModDatagenItemModel.kt  — 物品模型 + Geo 模型辅助
│       ├── ModDatagenParticle.kt
│       ├── ModDatagenSoundDefinitionsProvider.kt
│       ├── ModDatagenDatapackBuiltinEntries.kt
│       └── i18n/ModZhCn.kt         — 集中式中文本地化中心
│
└── resources/
    ├── META-INF/
    │   ├── accesstransformer.cfg   — 公开 MC 内部类（全局生效）
    │   └── interfaces.json
    ├── imaginarycraft.mixins.json  — Mixin 配置（预留）
    └── pack.mcmeta
```

## 聚合方式

通过 `jarJar` 将所有子模块打包进单一 JAR：

```
ImaginaryCraft.jar
├── GoldenBoughsLib (jarJar)
├── ResonatorCombatFramework (jarJar)
├── EGOCurios (jarJar)
├── EGOEquipment (jarJar)
└── Abnormalities (jarJar)
```

## 角色

- **唯一发布产物** — 所有子模块本身不独立发布，全部嵌入 ImaginaryCraft
- **数据生成调度中心** — Datagen.kt 挂接所有数据提供者到 GatherDataEvent
- **中文本地化中心** — ModZhCn 提供 `addI18nItemText` / `addI18nEntityTypeText` / `addI18nSoundEventText`
  等静态方法，子模块在开发模式下向此注册翻译
- **访问转换器托管** — accesstransformer.cfg 在此维护，公开 MC 内部类供所有子模块使用

## 现状

大部分功能由子模块提供。本模块自身代码极少，主要集中在：

- 数据生成骨架（具体内容由子模块注册）
- 音效注册框架
- 本地化收集器

## 上游依赖

- 所有 5 个子模块（jarJar）
- Curios API, JEI, Jade, Eyelib（可选）
- `../../额外资源/mods` 目录的 JAR（fileTree）
