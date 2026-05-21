# 异想工艺

## 目录

<!-- TOC -->
* [异想工艺](#异想工艺)
  * [目录](#目录)
  * [模块](#模块)
  * [开发人员(不分顺序)](#开发人员不分顺序)
  * [贡献者](#贡献者)
  * [本MOD前置](#本mod前置)
  * [联动MOD](#联动mod)
  * [项目构建](#项目构建)
    * [拉取与初始化](#拉取与初始化)
    * [构建系统](#构建系统)
      * [快速开始](#快速开始)
      * [新建模块](#新建模块)
      * [模块依赖声明](#模块依赖声明)
      * [neoforge.mods.toml](#neoforgemodstoml)
      * [版本管理](#版本管理)
      * [常用命令](#常用命令)
<!-- TOC -->

## 模块

| 名称                                                          | 用途       | 依赖                       |
|-------------------------------------------------------------|----------|--------------------------|
| [ImaginaryCraft](module/ImaginaryCraft)                     | 主模块      | 全部                       |
| [GoldenBoughsLib](module/GoldenBoughsLib)                   | api      | 无                        |
| [ResonatorCombatFramework](module/ResonatorCombatFramework) | 战斗框架     | GoldenBoughsLib          |
| [EGOCurios](module/EGOCurios)                               | EGO饰品    | ResonatorCombatFramework |
| [EGOEquipment](module/EGOEquipment)                         | 武器、盔甲、工具 | ResonatorCombatFramework |
| [Abnormalities](module/Abnormalities)                       | 异想体\生物   | ResonatorCombatFramework |

## 开发人员(不分顺序)

| 名称                                                        | 负责部分                                  |
|-----------------------------------------------------------|---------------------------------------|
| [哈基尽](https://space.bilibili.com/1082533225)              | 所有者/主程序/策划/动画/纹理<br/>（除了程序以外都不是主要进行的） |
| [尘昨喧](https://space.bilibili.com/161342683)               | 程序                                    |
| [ariwdxau](https://space.bilibili.com/3546886078204705)   | 程序                                    |
| [小史龙吖Slime_Dragon](https://space.bilibili.com/569400746)  | 主美                                    |
| [小希 Xiris/是Xiris哦](https://space.bilibili.com/1215603253) | 生物模型美工                                |
| [星零大队长](https://space.bilibili.com/489185984)             | 部分物品纹理                                |
| [原木Log_ym](https://space.bilibili.com/138986403)          | 武器/盔甲/饰品美工                            |
| [壹無貳隨](https://b23.tv/OfkSONt)                            | gui美工                                 |
| [天南野人](https://space.bilibili.com/2145067798)             | 武器/盔甲美工                               |
| [Dashcode](https://space.bilibili.com/51204057)           | 动画美术                                  | 
| "Macedonia"?                                              | 粒子美工                                  |

## 贡献者

| 名称                                                              | 内容          |
|-----------------------------------------------------------------|-------------|
| [FS-时叶](https://space.bilibili.com/3493109621066533)            | 客串          |
| [Zhu_Yii](https://space.bilibili.com/1209621490)                | 客串          |
| [-Yawning喵喵子-](https://space.bilibili.com/1446970336)           | 客串          |
| [一只霜狐Owo](https://space.bilibili.com/1214393496)                | 一些yes模型提供   |
| [Gstbnnnnnn/几点了啊啊](https://space.bilibili.com/3494357850130895) | 部分饰品/物品纹理提供 |
| [AmarokIce](https://space.bilibili.com/171428397)               | 程序          |
| “菜鸽子接动作动画”?                                                     | 玩家动画定制      |
| “宇宙之光”?                                                         | 程序          |

[//]: # (| “wdd”?                                                          | 程序 |)

[//]: # (| [永恒无虚]&#40;https://space.bilibili.com/1590922177&#41;                   | 美术        |)

[//]: # (| “倾城奶茶店店员”?                                                      | 模型        |)

[//]: # (| [涵_AH_han]&#40;https://space.bilibili.com/532826315&#41;                | 美术        |)

[//]: # (| [老旧的枕头与新毯子]&#40;https://space.bilibili.com/543764721&#41;               | 美术        |)

<hr>

## 本MOD前置

<p>
这些MOD链接都是github的但大部分在MCMOD百科或者其他MOD发布平台也有（搜名称）

| 前置链接及名称                                                       | 功能     |
|---------------------------------------------------------------|--------|
| [GeckoLib](https://github.com/bernie-g/geckolib)              | 基岩版模型库 |
| [Curios](https://github.com/TheIllusiveC4/Curios)             | 饰品     |
| [EpicFight](https://github.com/Antikythera-Studios/epicfight) | 史诗战斗   |

<hr>

本mod有部分代码来源来自[AnvilLib](https://github.com/Anvil-Dev/AnvilLib/)欢迎大家使用这个lib

## 联动MOD

| 名称                                      |
|-----------------------------------------|
| [玉 🔍](https://github.com/Snownee/Jade) |

## 项目构建

### 拉取与初始化

子模块采用 monorepo 管理，拉取后确认子目录是否完整：

```bash
git submodule init
git submodule update
```

### 构建系统

所有模块通过 `build-conventions.gradle` 共享通用配置，版本号在根 `gradle.properties` 统一维护。单个模块的 `build.gradle`
极简，仅声明特有依赖。

#### 快速开始

```bash
./gradlew :module:ImaginaryCraft:build     # 构建最终产物
./gradlew :module:ImaginaryCraft:runClient # 运行客户端
./gradlew projects                         # 列出所有模块
```

#### 新建模块

```bash
./create_module.sh <ModName> <mod_id> "显示名" <group>

# 示例
./create_module.sh TestMod test_mod "Test Mod" architecture.test_mod
```

脚本自动创建目录结构、`gradle.properties`、`build.gradle`、`interfaces.json`、`neoforge.mods.toml`，并注册到
`settings.gradle`。

#### 模块依赖声明

在模块 `build.gradle` 的 `dependencies {}` 块中使用辅助方法：

| 方法                         | 用途                      |
|----------------------------|-------------------------|
| `addModuleDeps('A', 'B')`  | 上游模块依赖                  |
| `addJarJarModuleDeps(...)` | jarJar 嵌入上游模块           |
| `addMixinSquared()`        | MixinSquared 支持         |
| `addJEI()`                 | JEI 配方查看                |
| `addEyelib()`              | 动画引擎                    |
| `addJade()`                | Jade 信息显示               |
| `addCurios()`              | Curios API 饰品栏          |
| `addDummyMobs()`           | 试验假人                    |
| `addLocalLibs()`           | 加载 `../../libs/` 本地 JAR |
| `addClientAuthRun()`       | 添加 devLogin 运行配置        |

**典型模块 build.gradle：**

```groovy
plugins {}
apply from: rootProject.file('build-conventions.gradle')

dependencies {
  addModuleDeps('GoldenBoughsLib', 'ResonatorCombatFramework')
  addMixinSquared()
  addEyelib()
  addJade()
  addJEI()
  addDummyMobs()
  addLocalLibs()
}
```

#### neoforge.mods.toml

模块模板只需声明模块特有内容，公共头部和 neoforge/minecraft 依赖由 `templates/mods-base.toml` 自动合并：

```toml
[[mixins]]
config = "${mod_id}.mixins.json"

[[dependencies.${ mod_id }]]
modId = "geckolib"
type = "required"
versionRange = "${geckolib_version}"
ordering = "NONE"
side = "BOTH"
```

#### 版本管理

所有第三方版本号在根 `gradle.properties` 维护，模块特有属性（mod_id、mod_name 等）在模块的 `gradle.properties` 中定义。

#### 常用命令

```bash
./gradlew :module:GoldenBoughsLib:runClient     # 运行单个模块客户端
./gradlew :module:ImaginaryCraft:runClientAuth   # devLogin 客户端
./gradlew :module:ImaginaryCraft:runData         # 数据生成
./gradlew build                                   # 构建全部
```