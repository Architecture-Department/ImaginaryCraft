package architecture.imaginarycraft.datagen;

import architecture.goldenboughs_lib.mixed.client.IModelBuilder;
import architecture.imaginarycraft.core.ImaginaryCraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;
import static net.minecraft.resources.ResourceLocation.parse;


/**
 * 物品模型数据生成器
 * 用于为模组中的物品生成对应的模型文件
 */
public final class ModDatagenItemModel extends ItemModelProvider {
	/**
	 * 构造函数
	 *
	 * @param output             数据包输出路径
	 * @param existingFileHelper 已存在文件助手，用于检查资源是否存在
	 */
	public ModDatagenItemModel(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, ImaginaryCraft.ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
	}


	private ResourceLocation extendWithFolder(ResourceLocation rl) {
		return rl.getPath().contains("/") ? rl : ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), folder + "/" + rl.getPath());
	}

	/**
	 * 为所有物品生成模型
	 * 遍历所有注册的物品条目并为其创建基础的generated模型
	 *
	 * @param registry   物品注册表
	 * @param pathPrefix 模型路径前缀
	 */
	private void withExistingParent(DeferredRegister.Items registry, String pathPrefix) {
		registry.getEntries().stream().map(DeferredHolder::getId).forEach(itemId ->
			IModelBuilder.of(this.withExistingParent(itemId.getPath(), "item/generated"))
				.goldenboughs_lib$getTexture()
				.put("layer0", itemId.withPrefix(pathPrefix).toString()));
	}

	/**
	 * 获取指定名称的父模型文件
	 *
	 * @param name 父模型名称
	 * @return 父模型文件
	 */
	private ModelFile.@NotNull UncheckedModelFile getParent(String name) {
		return new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace(name));
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
	public void createModelFile(Item item, String prefix, @NotNull Map<Float, String> textures, ModelFile parent,
	                            ResourceLocation... predicates) {
		ResourceLocation resourceLocation = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
		String itemModId = resourceLocation.getNamespace();
		String itemRl = "item/" + prefix + resourceLocation.getPath();

		// 如果没有提供父模型，则使用默认的"item/generated"
		ModelFile actualParent = parent != null ? parent : new ModelFile.UncheckedModelFile("item/generated");
		ItemModelBuilder modelBuilder = getBuilder(item.toString())
			.parent(actualParent)
			.texture("layer0", fromNamespaceAndPath(itemModId, itemRl));

		int index = 0;
		for (Map.Entry<Float, String> entry : textures.entrySet()) {
			String value = entry.getValue();

			ResourceLocation overrideModelRl = getItemResourceLocation(item, value).withPrefix("item/");
			modelBuilder.override()
				.model(new ModelFile.UncheckedModelFile(overrideModelRl))
				.predicate(predicates[Math.min(index, predicates.length - 1)], entry.getKey())
				.end();

			getBuilder(overrideModelRl.toString())
				.parent(actualParent)
				.texture("layer0", fromNamespaceAndPath(itemModId, itemRl + "_" + value));
			index++;
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
	public void createModelFile(Item item, String prefix, @NotNull Map<Float, String> textures, ResourceLocation... predicates) {
		createModelFile(item, prefix, textures, null, predicates);
	}

	/**
	 * 创建模型文件
	 *
	 * @param item 物品实例
	 * @param name 模型名称
	 * @return 模型文件
	 */
	public ModelFile.UncheckedModelFile createModelFile(Item item, String name) {
		return new ModelFile.UncheckedModelFile(getItemResourceLocation(item, name).withPrefix("item/"));
	}

	/**
	 * 获取物品的资源位置
	 *
	 * @param item 物品实例
	 * @param name 名称后缀
	 * @return 资源位置
	 */
	private @NotNull ResourceLocation getItemResourceLocation(Item item, String name) {
		return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).withSuffix("_" + name);
	}

	/**
	 * 创建特殊物品模型
	 *
	 * @param item 物品实例
	 * @param name 模型名称
	 * @return 物品模型构建器
	 */
	public ItemModelBuilder specialItem(Item item, String name) {
		return basicItem(getItemResourceLocation(item, name));
	}

	/**
	 * 创建模型物品
	 *
	 * @param item   物品实例
	 * @param parent 父模型文件
	 * @return 物品模型构建器
	 */
	public ItemModelBuilder createModelItem(Item item, ModelFile parent) {
		ResourceLocation resourceLocation = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
		return getBuilder(item.toString())
			.parent(parent)
			.texture("layer0", fromNamespaceAndPath(resourceLocation.getNamespace(), "item/" + resourceLocation.getPath()));
	}

	/**
	 * 用于给 GEO 模型生成的
	 */
	public void geoItem(Item item) {
		getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile(parse("builtin/entity")));
	}

	/**
	 * 创建基础物品模型
	 *
	 * @param item 物品实例
	 * @param name 模型名称
	 * @return 物品模型构建器
	 */
	public ItemModelBuilder basicItem(Item item, String name) {
		return basicItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)), name);
	}

	/**
	 * 创建基础物品模型
	 *
	 * @param item 物品资源位置
	 * @param name 模型名称
	 * @return 物品模型构建器
	 */
	public ItemModelBuilder basicItem(ResourceLocation item, String name) {
		return getBuilder(item.toString())
			.parent(customModelFile("models/item/" + name))
			.texture("layer0", fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
	}

	/**
	 * 创建自定义模型文件
	 *
	 * @param name 模型名称
	 * @return 模型文件
	 */
	public ModelFile customModelFile(String name) {
		return new ModelFile.UncheckedModelFile(ImaginaryCraft.modRl(name));
	}
}
