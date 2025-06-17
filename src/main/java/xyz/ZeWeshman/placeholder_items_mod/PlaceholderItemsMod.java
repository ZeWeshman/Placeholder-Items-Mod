package xyz.ZeWeshman.placeholder_items_mod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(PlaceholderItemsMod.MODID)
public class PlaceholderItemsMod {
    public static final String MODID = "placeholderitemsmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<Item>[] PLACEHOLDER_ITEMS = new DeferredItem[9];

    static {
        for (int i = 0; i < 9; i++) {
            final int index = i;
            PLACEHOLDER_ITEMS[i] = ITEMS.register("placeholder_item_" + (index + 1),
                    () -> new Item(new Item.Properties()));
        }
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PLACEHOLDER_TAB = CREATIVE_MODE_TABS.register("placeholder_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("Placeholder Items"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> PLACEHOLDER_ITEMS[0].get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for (DeferredItem<Item> item : PLACEHOLDER_ITEMS) {
                    output.accept(item.get());
                }
            }).build());

    public PlaceholderItemsMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Common setup for PlaceholderItemsMod");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (DeferredItem<Item> item : PLACEHOLDER_ITEMS) {
                event.accept(item.get());
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server is starting with PlaceholderItemsMod");
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Client setup for PlaceholderItemsMod");
            LOGGER.info("Player: {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
