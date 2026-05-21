#!/usr/bin/env bash
# ============================================================
# 快速新建模块脚本
# 用法: ./create_module.sh <ModName> <mod_id> <mod_name> <mod_group_id>
# 示例: ./create_module.sh TestMod test_mod "Test Mod" architecture.test_mod
# ============================================================
set -e

if [ "$#" -ne 4 ]; then
    echo "用法: $0 <ModName> <mod_id> <mod_name> <mod_group_id>"
    echo "示例: $0 TestMod test_mod \"Test Mod\" architecture.test_mod"
    exit 1
fi

MOD_NAME="$1"
MOD_ID="$2"
MOD_DISPLAY_NAME="$3"
MOD_GROUP_ID="$4"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
MODULE_DIR="${SCRIPT_DIR}/module/${MOD_NAME}"

echo "============================================"
echo "创建模块: ${MOD_NAME}"
echo "  mod_id     : ${MOD_ID}"
echo "  mod_name   : ${MOD_DISPLAY_NAME}"
echo "  mod_group  : ${MOD_GROUP_ID}"
echo "============================================"

# --------------------------------------------------
# 1. 检查模块是否已存在
# --------------------------------------------------
if [ -d "${MODULE_DIR}" ]; then
    echo "[错误] 模块目录已存在: ${MODULE_DIR}"
    exit 1
fi

# --------------------------------------------------
# 2. 添加到 settings.gradle
# --------------------------------------------------
SETTINGS_FILE="${SCRIPT_DIR}/settings.gradle"

# 检查是否已在 settings.gradle 中存在
if grep -q "\"${MOD_NAME}\"" "${SETTINGS_FILE}"; then
    echo "[错误] ${MOD_NAME} 已存在于 settings.gradle 中"
    exit 1
fi

# 在 projectName 列表的 ] 之前插入新模块名，并在末尾添加 include
if grep -q '^\]' "${SETTINGS_FILE}"; then
    # 多行 projectName 列表：在 ] 前插入
    sed -i "/^\]/i\\\t\"${MOD_NAME}\"," "${SETTINGS_FILE}"
else
    # 单行列表
    sed -i "s/\[/\[\n\t\"${MOD_NAME}\",\n/" "${SETTINGS_FILE}"
fi

echo "  [✓] 已添加到 settings.gradle"

# --------------------------------------------------
# 3. 创建模块目录结构
# --------------------------------------------------
mkdir -p "${MODULE_DIR}/src/main/java"
mkdir -p "${MODULE_DIR}/src/main/kotlin"
mkdir -p "${MODULE_DIR}/src/main/resources/META-INF"
mkdir -p "${MODULE_DIR}/src/main/templates"
mkdir -p "${MODULE_DIR}/src/generated/resources"

echo "  [✓] 已创建目录结构"

# --------------------------------------------------
# 4. 创建 gradle.properties
# --------------------------------------------------
cat > "${MODULE_DIR}/gradle.properties" << EOF
mod_id          = ${MOD_ID}
mod_name        = ${MOD_DISPLAY_NAME}
mod_license     = GNU LESSER GENERAL PUBLIC LICENSE
mod_version     = 1.0.0
mod_group_id    = ${MOD_GROUP_ID}
mod_authors     = Architecture
mod_description =
EOF

echo "  [✓] 已创建 gradle.properties"

# --------------------------------------------------
# 5. 创建 build.gradle
# --------------------------------------------------
cat > "${MODULE_DIR}/build.gradle" << 'BUILD_EOF'
plugins {
}

if (rootProject.file('build-conventions.gradle').exists()) {
    apply from: rootProject.file('build-conventions.gradle')
}

dependencies {
    // 上游模块依赖（按需取消注释）
    // addModuleDeps('GoldenBoughsLib')
    // addModuleDeps('GoldenBoughsLib', 'ResonatorCombatFramework')

    // 可选依赖（按需取消注释）
    // addEyelib()
    // addJade()
    // addJEI()
    // addMixinSquared()
    // addCurios()
    // addDummyMobs()
    // addLocalLibs()
}
BUILD_EOF

echo "  [✓] 已创建 build.gradle"

# --------------------------------------------------
# 6. 创建 interfaces.json
# --------------------------------------------------
cat > "${MODULE_DIR}/src/main/resources/META-INF/interfaces.json" << 'EOF'
[
]
EOF

echo "  [✓] 已创建 interfaces.json"

# --------------------------------------------------
# 7. 创建 neoforge.mods.toml 模板
# --------------------------------------------------
cat > "${MODULE_DIR}/src/main/templates/META-INF/neoforge.mods.toml" << 'MODS_EOF'
[[mixins]]
config = "${mod_id}.mixins.json"
MODS_EOF

echo "  [✓] 已创建 neoforge.mods.toml 模板"

# --------------------------------------------------
# 8. 完成
# --------------------------------------------------
echo ""
echo "============================================"
echo "模块创建完成！"
echo ""
echo "下一步:"
echo "  1. 编辑 module/${MOD_NAME}/build.gradle 配置模块依赖"
echo "  2. 编辑 module/${MOD_NAME}/src/main/templates/META-INF/neoforge.mods.toml 添加模块特有依赖"
echo "  3. 在 src/main/resources/META-INF/interfaces.json 中添加接口注入"
echo "  4. 刷新 Gradle: ./gradlew :module:${MOD_NAME}:build"
echo "============================================"
