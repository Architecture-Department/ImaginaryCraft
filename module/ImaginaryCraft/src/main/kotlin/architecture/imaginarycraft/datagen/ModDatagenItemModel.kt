package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.mixed.client.IModelBuilder
import architecture.imaginarycraft.core.ImaginaryCraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.resources.ResourceLocation.parse
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.Objects

/**
 * 物品模型数据生成器
 * 用于为模组中的物品生成对应的模型文件
 */
class ModDatagenItemModel(output: PackOutput, existingFileHelper: ExistingFileHelper) :
	ItemModelProvider(output, ImaginaryCraft.ID, existingFileHelper) {

	override fun registerModels() {
	}

	private fun extendWithFolder(rl: ResourceLocation): ResourceLocation {
		return if (rl.path.contains("/")) rl
		else ResourceLocation.fromNamespaceAndPath(rl.namespace, "$folder/${rl.path}")
	}

	/**
	 * 为所有物品生成模型
	 * 遍历所有注册的物品条目并为其创建基础的generated模型
	 *
	 * @param registry   物品注册表
	 * @param pathPrefix 模型路径前缀
	 */
	private fun withExistingParent(registry: DeferredRegister.Items, pathPrefix: String) {
		registry.entries.stream().map(DeferredHolder<Item>::getId).forEach { itemId ->
			IModelBuilder.of(this.withExistingParent(itemId.path, "item/generated"))
				.goldenboughs_lib$getTexture()
			.put("layer0", itemId.withPrefix(pathPrefix).toString())
		}
	}

	/**
	 * 获取指定名称的父模型文件
	 *
	 * @param name 父模型名称
	 * @return 父模型文件
	 */
	private fun getParent(name: String): ModelFile.UncheckedModelFile {
		return ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace(name))
	}

	/**
	 * 为物品创建带有不同纹理的模型文件
	 * 根据提供的纹理映射和谓词创建多个模型变体
	 *
	 * @param item       物品实例
	 * @param prefix     前缀
	 * @param textures   纹理映射，键为浮点数值，值为纹理名称
	 * @param parent     父模型文件，如果为null则使用默认的"item/generated"
	 * @param predicates 谓词资源位置数组，用于确定何时使用哪个模型变体
	 */
	fun createModelFile(
		item: Item,
		prefix: String,
		textures: Map<Float, String>,
		parent: ModelFile?,
		vararg predicates: ResourceLocation
	) {
		val resourceLocation = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item))
		val itemModId = resourceLocation.namespace
		val itemRl = "item/$prefix${resourceLocation.path}"

		// 如果没有提供父模型，则使用默认的"item/generated"
		val actualParent = parent ?: ModelFile.UncheckedModelFile("item/generated")
		val modelBuilder = getBuilder(item.toString())
			.parent(actualParent)
			.texture("layer0", fromNamespaceAndPath(itemModId, itemRl))

		var index = 0
		for ((key, value) in textures) {
			val overrideModelRl = getItemResourceLocation(item, value).withPrefix("item/")
			modelBuilder.override()
				.model(ModelFile.UncheckedModelFile(overrideModelRl))
				.predicate(predicates[Math.min(index, predicates.size - 1)], key)
				.end()

			getBuilder(overrideModelRl.toString())
				.parent(actualParent)
				.texture("layer0", fromNamespaceAndPath(itemModId, itemRl + "_" + value))
			index++
		}
	}

	/**
	 * 为物品创建带有不同纹理的模型文件
	 * 根据提供的纹理映射和谓词创建多个模型变体
	 * 使用默认的"item/generated"作为父模型
	 *
	 * @param item       物品实例
	 * @param prefix     前缀
	 * @param textures   纹理映射，键为浮点数值，值为纹理名称
	 * @param predicates 谓词资源位置数组，用于确定何时使用哪个模型变体
	 */
	fun createModelFile(
		item: Item,
		prefix: String,
		textures: Map<Float, String>,
		vararg predicates: ResourceLocation
	) {
		createModelFile(item, prefix, textures, null, *predicates)
	}

	/**
	 * 创建模型文件
	 *
	 * @param item 物品实例
	 * @param name 模型名称
	 * @return 模型文件
	 */
	fun createModelFile(item: Item, name: String): ModelFile.UncheckedModelFile {
		return ModelFile.UncheckedModelFile(getItemResourceLocation(item, name).withPrefix("item/"))
	}

	/**
	 * 获取物品的资源位置
	 *
	 * @param item 物品实例
	 * @param name 名称后缀
	 * @return 资源位置
	 */
	private fun getItemResourceLocation(item: Item, name: String): ResourceLocation {
		return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).withSuffix("_$name")
	}

	/**
	 * 创建特殊物品模型
	 *
	 * @param item 物品实例
	 * @param name 模型名称
	 * @return 物品模型构建器
	 */
	fun specialItem(item: Item, name: String): ItemModelBuilder {
		return basicItem(getItemResourceLocation(item, name))
	}

	/**
	 * 创建模型物品
	 *
	 * @param item   物品实例
	 * @param parent 父模型文件
	 * @return 物品模型构建器
	 */
	fun createModelItem(item: Item, parent: ModelFile): ItemModelBuilder {
		val resourceLocation = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item))
		return getBuilder(item.toString())
			.parent(parent)
			.texture("layer0", fromNamespaceAndPath(resourceLocation.namespace, "item/" + resourceLocation.path))
	}

	/**
	 * 用于给 GEO 模型生成的
	 */
	fun geoItem(item: Item) {
		getBuilder(item.toString()).parent(ModelFile.UncheckedModelFile(parse("builtin/entity")))
	}

	/**
	 * 创建基础物品模型
	 *
	 * @param item 物品实例
	 * @param name 模型名称
	 * @return 物品模型构建器
	 */
	fun basicItem(item: Item, name: String): ItemModelBuilder {
		return basicItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)), name)
	}

	/**
	 * 创建基础物品模型
	 *
	 * @param item 物品资源位置
	 * @param name 模型名称
	 * @return 物品模型构建器
	 */
	fun basicItem(item: ResourceLocation, name: String): ItemModelBuilder {
		return getBuilder(item.toString())
			.parent(customModelFile("models/item/$name"))
			.texture("layer0", fromNamespaceAndPath(item.namespace, "item/" + item.path))
	}

	/**
	 * 创建自定义模型文件
	 *
	 * @param name 模型名称
	 * @return 模型文件
	 */
	fun customModelFile(name: String): ModelFile {
		return ModelFile.UncheckedModelFile(ImaginaryCraft.modRl(name))
	}
}
