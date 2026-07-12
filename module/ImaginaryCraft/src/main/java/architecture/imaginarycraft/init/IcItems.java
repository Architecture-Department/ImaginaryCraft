package architecture.imaginarycraft.init;

import architecture.goldenboughs_lib.common.item.FoodItem;
import architecture.goldenboughs_lib.util.FoodPropertiesBuilder;
import architecture.imaginarycraft.datagen.i18n.IcZhCn;
import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class IcItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.Items.createItems(IcUtil.ID);

    /**
     * 罐装脑啡肽
     */
    public static final DeferredItem<FoodItem> CANNED_ENKEPHALIN = registerFood(
            "canned_enkephalin", "罐装脑啡肽", false,
            new FoodPropertiesBuilder()
                    .eatSeconds(84)
                    .alwaysEdible()
    );

    private IcItems() {
    }

    //region 食物/饮品注册

    /**
     * 注册食物/饮品（使用默认 func）。
     */
    private static DeferredItem<FoodItem> registerFood(
            String name, String nameZh, boolean isEat,
            Item.Properties properties,
            FoodPropertiesBuilder foodPropertiesBuilder
    ) {
        return registerFood(name, nameZh, isEat, properties, foodPropertiesBuilder,
                props -> new FoodItem(props, foodPropertiesBuilder.build(), isEat));
    }

    /**
     * 注册食物/饮品（使用默认 {@link Item.Properties}）。
     */
    private static DeferredItem<FoodItem> registerFood(
            String name, String nameZh, boolean isEat,
            FoodPropertiesBuilder foodPropertiesBuilder
    ) {
        return registerFood(name, nameZh, isEat, new Item.Properties(), foodPropertiesBuilder);
    }

    /**
     * 注册食物/饮品（完整参数）。
     */
    private static DeferredItem<FoodItem> registerFood(
            String name, String nameZh, boolean isEat,
            Item.Properties properties,
            FoodPropertiesBuilder foodPropertiesBuilder,
            Function<Item.Properties, FoodItem> func
    ) {
        return register(name, nameZh, func, properties);
    }

    //endregion

    //region 通用物品注册

    private static <I extends Item> DeferredItem<I> register(
            String name, String nameZh,
            Function<Item.Properties, I> func,
            Item.Properties properties
    ) {
        var registerItem = REGISTRY.registerItem(name, func, properties.stacksTo(1));
        IcZhCn.addI18nItemText(nameZh, registerItem);
        return registerItem;
    }

    //endregion
}
