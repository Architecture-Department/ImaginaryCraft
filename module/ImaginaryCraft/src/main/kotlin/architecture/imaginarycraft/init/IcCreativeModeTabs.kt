package architecture.imaginarycraft.init

import architecture.ego_equipment.util.EGOEquipmentUtil
import architecture.imaginarycraft.datagen.i18n.IcZhCn
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object IcCreativeModeTabs {
	val REGISTRY: DeferredRegister<CreativeModeTab> =
		EGOEquipmentUtil.modRegister(BuiltInRegistries.CREATIVE_MODE_TAB)

	val FOOD: DeferredHolder<CreativeModeTab, CreativeModeTab> = register(
		"food", "异想工艺 | 食物"
	) { name, zhCn ->
		createCreativeModeTab(name, zhCn, { _, output ->
			output.accept(IcItems.CANNED_ENKEPHALIN)
		}, { IcItems.CANNED_ENKEPHALIN.get().defaultInstance })
	}

	private fun register(
		name: String,
		zhCn: String,
		builder: (String, String) -> CreativeModeTab.Builder
	): DeferredHolder<CreativeModeTab, CreativeModeTab> =
		REGISTRY.register(name) { -> builder(name, zhCn).build() }

	@Suppress("unused")
	private fun createCreativeModeTab(
		name: String,
		zhCn: String,
		displayItemsGenerator: CreativeModeTab.DisplayItemsGenerator,
		icon: () -> ItemStack,
		withTabsBefore: ResourceKey<CreativeModeTab>
	): CreativeModeTab.Builder = createCreativeModeTab(name, zhCn, displayItemsGenerator, icon)
		.withTabsBefore(withTabsBefore)

	private fun createCreativeModeTab(
		name: String,
		zhCn: String,
		displayItemsGenerator: CreativeModeTab.DisplayItemsGenerator,
		icon: () -> ItemStack
	): CreativeModeTab.Builder = createCreativeModeTab(name, zhCn, displayItemsGenerator)
		.icon(icon)

	private fun createCreativeModeTab(
		name: String,
		zhCn: String,
		displayItemsGenerator: CreativeModeTab.DisplayItemsGenerator
	): CreativeModeTab.Builder {
		val key = "itemGroup.${EGOEquipmentUtil.ID}.$name"
		IcZhCn.addI18nText(zhCn, key)
		return CreativeModeTab.builder()
			.title(Component.translatable(key))
			.displayItems(displayItemsGenerator)
	}

	private fun addRegistryItem(registry: DeferredRegister.Items, output: CreativeModeTab.Output) {
		registry.entries.forEach { entry -> output.accept(entry.get()) }
	}
}
