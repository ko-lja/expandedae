package lu.kolja.expandedae.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lu.kolja.expandedae.Expandedae;
import lu.kolja.expandedae.item.cards.ItemAdvancedBlockingCard;
import lu.kolja.expandedae.item.cards.ItemAutoCompleteCard;
import lu.kolja.expandedae.item.cards.ItemPatternRefillerCard;
import lu.kolja.expandedae.item.cards.ItemStickyCard;
import lu.kolja.expandedae.item.misc.ExpPatternProviderUpgradeItem;
import lu.kolja.expandedae.item.part.ExpPatternProviderPart;
import lu.kolja.expandedae.item.part.ExpPatternProviderPartItem;
import lu.kolja.expandedae.item.part.FilterTerminalPart;
import lu.kolja.expandedae.item.part.FilterTerminalPartItem;
import lu.kolja.expandedae.storage.ExpandedStorageCell;
import lu.kolja.expandedae.storage.ExpandedStorageComponentItem;
import lu.kolja.expandedae.storage.ExpandedStorageTier;
import appeng.api.ids.AECreativeTabIds;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.api.stacks.AEKeyType;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

@SuppressWarnings("ALL")
public class ExpItems {

    public static void init() {
        // controls static load order
        Expandedae.LOGGER.info("Initialised items.");
    }

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static final ItemDefinition<ExpPatternProviderPartItem> EXP_PATTERN_PROVIDER_PART = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(ExpPatternProviderPart.class));
        return item("Expanded Pattern Provider", "exp_pattern_provider_part", ExpPatternProviderPartItem::new);
    });

    public static final ItemDefinition<FilterTerminalPartItem> FILTER_TERMINAL_PART = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(FilterTerminalPart.class));
        return item("Filter Terminal", "filter_terminal", FilterTerminalPartItem::new);
    });

    public static final ItemDefinition<ExpPatternProviderUpgradeItem> EXP_PATTERN_PROVIDER_UPGRADE = item(
            "Expanded Pattern Provider Upgrade",
            "exp_pattern_provider_upgrade",
            ExpPatternProviderUpgradeItem::new
    );

    public static final ItemDefinition<ItemAutoCompleteCard> AUTO_COMPLETE_CARD = item(
            "Auto Complete Card",
            "auto_complete_card",
            ItemAutoCompleteCard::new
    );
    public static final ItemDefinition<ItemAdvancedBlockingCard> ADVANCED_BLOCKING_CARD = item(
            "Advanced Blocking Card",
            "advanced_blocking_card",
            ItemAdvancedBlockingCard::new
    );
    public static final ItemDefinition<ItemStickyCard> STICKY_CARD = item(
            "Sticky Card",
            "sticky_card",
            ItemStickyCard::new
    );
    public static final ItemDefinition<ItemPatternRefillerCard> PATTERN_REFILLER_CARD = item(
            "Pattern Refiller Card",
            "pattern_refiller_card",
            ItemPatternRefillerCard::new
    );

    public static final ItemDefinition<ExpandedStorageComponentItem> UNIVERSE_COMPONENT = component("Artificial Universe Storage Component", "artificial_universe_component", Integer.MAX_VALUE);
    public static final ItemDefinition<MaterialItem> ARTIFICIAL_UNIVERSE_CELL_HOUSING = item(
            "Artificial Universe Cell Housing",
            "artificial_universe_cell_housing",
            MaterialItem::new
    );
    public static final ExpandedStorageTier TIER_UNIVERSE = tier(12, "DEV", Integer.MAX_VALUE, UNIVERSE_COMPONENT);
    /*public static final ItemDefinition<ExpandedStorageCell> ARTIFICIAL_UNIVERSE_CELL = itemCell(
            "Artificial Universe ME Storage Cell",
            "artificial_universe_cell",
            TIER_UNIVERSE,
            ARTIFICIAL_UNIVERSE_CELL_HOUSING,
            Long.MAX_VALUE / 16,
            (Long.MAX_VALUE / 16) / 128,
            63
    );*/
    public static final ItemDefinition<ExpandedStorageCell> ARTIFICIAL_UNIVERSE_CELL = item(
            "Artificial Universe ME Storage Cell",
            "artificial_universe_cell",
            p -> new ExpandedStorageCell(
                    p.stacksTo(1),
                    UNIVERSE_COMPONENT,
                    ARTIFICIAL_UNIVERSE_CELL_HOUSING,
                    TIER_UNIVERSE.idleDrain(),
                    Integer.MAX_VALUE,
                    Integer.MAX_VALUE / 128,
                    63,
                    AEKeyType.items()
            )
    );

    public static List<ItemDefinition<?>> getItemCells() {
        return List.of(ARTIFICIAL_UNIVERSE_CELL);
    }

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(englishName, id, p -> new PartItem<>(p, partClass, factory));
    }

    public static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, Expandedae.makeId(id), factory.apply(new Item.Properties()));
        ITEMS.add(definition);
        return definition;
    }

    private static ItemDefinition<ExpandedStorageCell> itemCell(String englishName, String id, ExpandedStorageTier tier, ItemLike housingItem, long bytes, long bytesPerType, int totalTypes) {
        return item(
                englishName,
                id,
                p -> new ExpandedStorageCell(
                        p.stacksTo(1),
                        tier.componentSupplier().get(),
                        housingItem,
                        tier.idleDrain(),
                        bytes,
                        bytesPerType,
                        totalTypes,
                        AEKeyType.items())
        );

    }
    private static ExpandedStorageTier tier(int index, String namePrefix, long storageInBytes, ItemDefinition<ExpandedStorageComponentItem> component) {
        return new ExpandedStorageTier(index, namePrefix, storageInBytes, 0.5 * index, component::asItem);
    }
    private static ItemDefinition<ExpandedStorageComponentItem> component(String englishName, String id, long storageInBytes) {
        return item(
                englishName,
                id,
                properties -> new ExpandedStorageComponentItem(properties, storageInBytes)
        );
    }

    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id,
                                                   Function<Item.Properties, T> factory) {
        return item(name, id, factory, AECreativeTabIds.MAIN);
    }

    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id,
                                                   Function<Item.Properties, T> factory,
                                                   ResourceKey<CreativeModeTab> group) {

        Item.Properties p = new Item.Properties();
        T item = factory.apply(p);
        ItemDefinition<T> definition = new ItemDefinition<>(name, id, item);

        ITEMS.add(definition);
        return definition;
    }

    public static void orderInit() {}
}
