/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.core.features.module.movement;

import cope.saturn.core.events.ClientTickEvent;
import cope.saturn.core.events.ItemSlowdownEvent;
import cope.saturn.core.events.PacketEvent;
import cope.saturn.core.features.module.Category;
import cope.saturn.core.features.module.Module;
import cope.saturn.core.managers.InventoryManager;
import cope.saturn.core.settings.Setting;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class NoSlow extends Module {
    public NoSlow() {
        super("NoSlow", Category.MOVEMENT, "Stops slowdown with items");
    }

    public static final Setting<Boolean> inventoryMove = new Setting<>("InventoryMove", true);
    public static final Setting<Boolean> stopSprint = new Setting<>(inventoryMove, "StopSprint", true);
    public static final Setting<Boolean> look = new Setting<>(inventoryMove, "Look", true);

    public static final Setting<Boolean> strict = new Setting<>("Strict", false);
    public static final Setting<Boolean> shields = new Setting<>("Shields", true);
    public static final Setting<Boolean> bows = new Setting<>("Bows", true);
    public static final Setting<Boolean> potions = new Setting<>("Potions", true);

    @EventListener
    public void onClientTick(ClientTickEvent event) {
        if (validateNoSlow() && strict.getValue()) {
            getSaturn().getInventoryManager().swap(mc.player.getInventory().selectedSlot, InventoryManager.Swap.PACKET);
        }

        if (inventoryMove.getValue() && mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) {
            long handle = mc.getWindow().getHandle();

            for (KeyBinding binding : new KeyBinding[]
                    { mc.options.keyForward, mc.options.keyBack, mc.options.keyRight, mc.options.keyLeft }) {

                binding.setPressed(InputUtil.isKeyPressed(handle, binding.getDefaultKey().getCode()));
            }

            if (look.getValue()) {
                // yandere dev
                if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_UP)) {
                    mc.player.setPitch(mc.player.getPitch() - 5.0f);
                } else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_DOWN)) {
                    mc.player.setPitch(mc.player.getPitch() + 5.0f);
                } else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT)) {
                    mc.player.setYaw(mc.player.getYaw() + 5.0f);
                } else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT)) {
                    mc.player.setYaw(mc.player.getYaw() - 5.0f);
                }

                // do not allow pitch to go past vanilla values
                // will flag some anticheats if this isnt here
                mc.player.setPitch(MathHelper.clamp(mc.player.getPitch(), -90.0f, 90.0f));
            }
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket packet &&
                inventoryMove.getValue() && stopSprint.getValue() &&
                mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) {
            if (packet.getMode().equals(ClientCommandC2SPacket.Mode.START_SPRINTING)) {
                mc.player.setSprinting(false);
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onItemSlowdown(ItemSlowdownEvent event) {
        if (validateNoSlow()) {
            event.getInput().movementForward *= 5.0f;
            event.getInput().movementSideways *= 5.0f;
        }
    }

    private boolean validateNoSlow() {
        if (mc.player.isBlocking() && !shields.getValue()) {
            return false;
        }

        ItemStack activeStack = mc.player.getActiveItem();
        if (activeStack.getItem() instanceof BowItem && !bows.getValue()) {
            return false;
        }

        if (activeStack.getItem() instanceof PotionItem && !potions.getValue()) {
            return false;
        }

        return true;
    }
}
