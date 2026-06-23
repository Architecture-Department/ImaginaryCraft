# ImaginaryCraft

Mod ID: `imaginarycraft`

聚合模块。通过 jarJar 将所有子模块打包为单一 NeoForge mod 产物。

数据生成调度中心 + 中文本地化中心 + 访问转换器托管。

## 包结构

- `core/` — `ImaginaryCraft.kt`(@Mod), `ImaginaryCraftClient.kt`, 常量
- `init/` — 音效注册
- `common/item/` — 物品
- `common/particle/` — 粒子
- `datagen/` — 数据生成调度（GatherDataEvent, BlockState, ItemModel, Particle, Sounds, DatapackBuiltin, i18n）
- `events/` — 事件监听器
- `util/` — 工具类
- `mixin/java/` — `LivingEntityMixin`, `PlayerMixin`

## JarJar 聚合

```
ImaginaryCraft.jar
├── GoldenBoughsLib
├── ResonatorCombatFramework
├── EGOCurios
├── EGOEquipment
└── Abnormalities
```

- `accesstransformer.cfg` 全局生效
- `ModZhCn` 集中式中文本地化中心

## 依赖

- 所有 5 个子模块（jarJar）
- Curios API, JEI, Jade（可选）
- `../../额外资源/mods` 目录的 JAR
