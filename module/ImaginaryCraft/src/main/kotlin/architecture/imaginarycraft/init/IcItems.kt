package architecture.imaginarycraft.init

import architecture.goldenboughs_lib.common.item.FoodItem
import architecture.goldenboughs_lib.util.FoodPropertiesBuilder
import architecture.imaginarycraft.core.ImaginaryCraftConstants
import architecture.imaginarycraft.datagen.i18n.LcZhCn
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Function

object IcItems {
	@JvmField
	val REGISTRY: DeferredRegister.Items = DeferredRegister.Items.createItems(ImaginaryCraftConstants.ID)

	@JvmField
	val CANNED_ENKEPHALIN = registerFood(
		"canned_enkephalin", "罐装脑啡肽", false,
		foodPropertiesBuilder = FoodPropertiesBuilder()
			.eatSeconds(84)
			.alwaysEdible()
	)

	//	DRINK
	private fun registerFood(
		name: String,
		nameZh: String,
		isEat: Boolean = true,
		properties: Item.Properties = Item.Properties(),
		foodPropertiesBuilder: FoodPropertiesBuilder,
		func: Function<Item.Properties, FoodItem> = { FoodItem(it, foodPropertiesBuilder.build(), isEat) }
	): DeferredItem<FoodItem> {
		return register(name, nameZh, func, properties)
	}

	private fun <I : Item> register(
		name: String,
		nameZh: String,
		func: Function<Item.Properties, I>,
		properties: Item.Properties
	): DeferredItem<I> {
		val registerItem = REGISTRY.registerItem(
			name, func, properties
				.stacksTo(1)
		)
		LcZhCn.addI18nItemText(nameZh, registerItem)
		return registerItem
	}
}
