package architecture.imaginarycraft.datagen.i18n

import architecture.goldenboughs_lib.datagen.i18n.DatagenI18n
import architecture.imaginarycraft.core.ImaginaryCraft
import net.minecraft.data.PackOutput
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.item.Item
import net.neoforged.fml.loading.FMLEnvironment
import org.jetbrains.annotations.ApiStatus
import java.util.function.Supplier

@ApiStatus.Internal
class ModZhCn(output: PackOutput) : DatagenI18n(output, ImaginaryCraft.ID, "zh_cn") {

	override fun addTranslations() {
		addPackDescription(ImaginaryCraft.ID, "异想工艺")
		addItemList(ITEMS)
		addEntityList(ENTITY)
		addMobEffectList(MOB_EFFECT)
		addAttributeList(ATTRIBUTE)
		addSoundEventList(SOUND_EVENT)
		MAP.forEach { (key, value) -> add(key, value) }
	}

	companion object {
		private val ITEMS: MutableMap<Supplier<out Item>, String> = HashMap()
		private val ENTITY: MutableMap<Supplier<out EntityType<*>>, String> = HashMap()
		private val MOB_EFFECT: MutableMap<Supplier<out MobEffect>, String> = HashMap()
		private val ATTRIBUTE: MutableMap<Supplier<out Attribute>, String> = HashMap()
		private val SOUND_EVENT: MutableMap<Supplier<out SoundEvent>, String> = HashMap()
		private val MAP: MutableMap<String, String> = HashMap()

		@JvmStatic
		fun addI18nText(zhCn: String, key: String) {
			if (!FMLEnvironment.production) {
				MAP[key] = zhCn
			}
		}

		@JvmStatic
		fun addI18nItemText(zhName: String, deferredItem: Supplier<out Item>) {
			if (!FMLEnvironment.production) {
				ITEMS[deferredItem] = zhName
			}
		}

		@JvmStatic
		fun addI18nEntityTypeText(zhName: String, supplier: Supplier<out EntityType<*>>) {
			if (!FMLEnvironment.production) {
				ENTITY[supplier] = zhName
			}
		}

		@JvmStatic
		fun addI18nMobEffectText(zhName: String, supplier: Supplier<out MobEffect>) {
			if (!FMLEnvironment.production) {
				MOB_EFFECT[supplier] = zhName
			}
		}

		@JvmStatic
		fun addI18nAttributeText(zhName: String, supplier: Supplier<out Attribute>) {
			if (!FMLEnvironment.production) {
				ATTRIBUTE[supplier] = zhName
			}
		}

		@JvmStatic
		fun addI18nSoundEventText(zhName: String, supplier: Supplier<out SoundEvent>) {
			if (!FMLEnvironment.production) {
				SOUND_EVENT[supplier] = zhName
			}
		}
	}
}
