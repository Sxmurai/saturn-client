package cope.saturn.core.features.module.combat;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.settings.Setting;
import cope.saturn.util.entity.EntityUtil;
import cope.saturn.util.entity.player.inventory.InventoryUtil;
import cope.saturn.util.entity.player.inventory.task.Task;
import cope.saturn.util.entity.player.inventory.task.TaskHandler;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem", Category.COMBAT, "Manages your offhand");
    }

    public static final Setting<Mode> mode = new Setting<>("Mode", Mode.Crystal);
    public static final Setting<Integer> delay = new Setting<>("Delay", 1, 0, 10);
    public static final Setting<Float> health = new Setting<>("Health", 16.0f, 1.0f, 20.0f);
    public static final Setting<Boolean> offhandGap = new Setting<>("OffhandGap", true);

    // handles all our inventory interactions
    private final TaskHandler handler = new TaskHandler();

    @Override
    protected void onDisable() {
        handler.clear();
    }

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (mc.currentScreen == null && handler.getStopwatch().passedTicks(delay.getValue()) && !handler.isEmpty()) {
            handler.getStopwatch().reset();
            handler.run(1);

            return;
        }

        Item item;

        if (willDieOnFall() || EntityUtil.getHealth(mc.player) <= health.getValue()) {
            item = Items.TOTEM_OF_UNDYING;
        } else {
            if (InventoryUtil.isHolding(SwordItem.class, false) && offhandGap.getValue() && mc.options.keyUse.isPressed()) {
                item = Items.ENCHANTED_GOLDEN_APPLE;
            } else {
                item = mode.getValue().item;
            }
        }

        if (item != null) {
            addItemsToQueue(item);
        }
    }

    private void addItemsToQueue(Item item) {
        // if any of the items in the array is something we're holding
        if (InventoryUtil.isHolding(item, true)) {
            return;
        }

        int slot = InventoryUtil.getSlot(item, false);
        if (slot == -1) {

            // try searching for a regular gapple instead
            if (item == Items.ENCHANTED_GOLDEN_APPLE) {
                slot = InventoryUtil.getSlot(Items.GOLDEN_APPLE, false);
                if (slot == -1) {
                    return;
                }
            }

            return;
        }

        slot = slot < 9 ? slot + 36 : slot;

        handler.add(new Task(SlotActionType.PICKUP, slot, false));
        handler.add(new Task(SlotActionType.PICKUP, InventoryUtil.OFFHAND_SLOT, false));

        // if we had an item in the offhand, we'll have a floating item once the above two tasks finish
        if (!mc.player.getOffHandStack().isEmpty()) {
            handler.add(new Task(SlotActionType.PICKUP, slot, false));
        }
    }

    /**
     * Checks if the current fall distance will kill you
     * @return read description of method
     */
    private boolean willDieOnFall() {
        return ((mc.player.fallDistance - 3.0f) / 2.0f) + 3.5f >= EntityUtil.getHealth(mc.player);
    }

    public enum Mode {
        Totem(Items.TOTEM_OF_UNDYING),
        Crystal(Items.END_CRYSTAL),
        GApple(Items.ENCHANTED_GOLDEN_APPLE);

        private final Item item;

        Mode(Item item) {
            this.item = item;
        }
    }
}
