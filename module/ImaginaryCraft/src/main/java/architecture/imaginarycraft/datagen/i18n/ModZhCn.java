package architecture.imaginarycraft.datagen.i18n;

import architecture.goldenboughs_lib.datagen.i18n.DatagenI18n;
import architecture.imaginarycraft.core.ImaginaryCraft;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class ModZhCn extends DatagenI18n {
	private static final Map<Supplier<? extends Item>, String> ITEMS = new HashMap<>();
	private static final Map<Supplier<? extends EntityType<?>>, String> ENTITY = new HashMap<>();
	private static final Map<Supplier<? extends MobEffect>, String> MOB_EFFECT = new HashMap<>();
	private static final Map<Supplier<? extends Attribute>, String> ATTRIBUTE = new HashMap<>();
	private static final Map<Supplier<? extends SoundEvent>, String> SOUND_EVENT = new HashMap<>();
	private static final Map<String, String> MAP = new HashMap<>();

	public ModZhCn(final PackOutput output) {
		super(output, ImaginaryCraft.ID, "zh_cn");
	}

	public static void addI18nText(String zhCn, String key) {
		if (!FMLEnvironment.production) {
			ModZhCn.MAP.put(key, zhCn);
		}
	}

	public static void addI18nItemText(String zhName, Supplier<? extends Item> deferredItem) {
		if (!FMLEnvironment.production) {
			ModZhCn.ITEMS.put(deferredItem, zhName);
		}
	}

	public static void addI18nEntityTypeText(String zhName, Supplier<? extends EntityType<?>> supplier) {
		if (!FMLEnvironment.production) {
			ModZhCn.ENTITY.put(supplier, zhName);
		}
	}

	public static void addI18nMobEffectText(String zhName, Supplier<? extends MobEffect> supplier) {
		if (!FMLEnvironment.production) {
			ModZhCn.MOB_EFFECT.put(supplier, zhName);
		}
	}

	public static void addI18nAttributeText(String zhName, Supplier<? extends Attribute> supplier) {
		if (!FMLEnvironment.production) {
			ModZhCn.ATTRIBUTE.put(supplier, zhName);
		}
	}

	public static void addI18nSoundEventText(String zhName, Supplier<? extends SoundEvent> supplier) {
		if (!FMLEnvironment.production) {
			ModZhCn.SOUND_EVENT.put(supplier, zhName);
		}
	}

	@Override
	protected void addTranslations() {
		addPackDescription(ImaginaryCraft.ID, "异想工艺");
		addItemList(ITEMS);
		addEntityList(ENTITY);
		addMobEffectList(MOB_EFFECT);
		addAttributeList(ATTRIBUTE);
		addSoundEventList(SOUND_EVENT);
		MAP.forEach(this::add);
	}
}
