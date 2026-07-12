package architecture.imaginarycraft.init;

import architecture.ego_equipment.util.EgoEquipUtil;
import architecture.imaginarycraft.datagen.i18n.IcZhCn;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class IcCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            EgoEquipUtil.modRegister(BuiltInRegistries.CREATIVE_MODE_TAB);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD = register(
            "food", "异想工艺 | 食物",
            (name, zhCn) -> createCreativeModeTab(name, zhCn,
                    (parameters, output) -> output.accept(IcItems.CANNED_ENKEPHALIN),
                    () -> IcItems.CANNED_ENKEPHALIN.get().getDefaultInstance()
            )
    );

    private IcCreativeModeTabs() {
    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> register(
            String name,
            String zhCn,
            BiFunction<String, String, CreativeModeTab.Builder> builder
    ) {
        return REGISTRY.register(name, () -> builder.apply(name, zhCn).build());
    }

    @SuppressWarnings("unused")
    private static CreativeModeTab.Builder createCreativeModeTab(
            String name,
            String zhCn,
            CreativeModeTab.DisplayItemsGenerator displayItemsGenerator,
            Supplier<ItemStack> icon,
            ResourceKey<CreativeModeTab> withTabsBefore
    ) {
        return createCreativeModeTab(name, zhCn, displayItemsGenerator, icon)
                .withTabsBefore(withTabsBefore);
    }

    private static CreativeModeTab.Builder createCreativeModeTab(
            String name,
            String zhCn,
            CreativeModeTab.DisplayItemsGenerator displayItemsGenerator,
            Supplier<ItemStack> icon
    ) {
        return createCreativeModeTab(name, zhCn, displayItemsGenerator)
                .icon(icon);
    }

    private static CreativeModeTab.Builder createCreativeModeTab(
            String name,
            String zhCn,
            CreativeModeTab.DisplayItemsGenerator displayItemsGenerator
    ) {
        var key = "itemGroup." + EgoEquipUtil.ID + "." + name;
        IcZhCn.addI18nText(zhCn, key);
        return CreativeModeTab.builder()
                .title(Component.translatable(key))
                .displayItems(displayItemsGenerator);
    }

    private static void addRegistryItem(DeferredRegister.Items registry, CreativeModeTab.Output output) {
        registry.getEntries().forEach(entry -> output.accept(entry.get()));
    }
}
