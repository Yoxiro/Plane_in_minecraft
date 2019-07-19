package de.maxhenkel.plane.events;

import de.maxhenkel.plane.entity.EntityPlane;
import de.maxhenkel.plane.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyEvents {

    private boolean wasGuiPressed;

    public KeyEvents() {

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.isCanceled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        PlayerEntity player = minecraft.player;

        if (player == null) {
            return;
        }

        Entity riding = player.getRidingEntity();

        if (!(riding instanceof EntityPlane)) {
            return;
        }

        EntityPlane plane = (EntityPlane) riding;

        if (player.equals(plane.getDriver())) {
            plane.updateControls(Main.UP_KEY.isKeyDown(), Main.DOWN_KEY.isKeyDown(), Main.FORWARD_KEY.isKeyDown(), Main.BACK_KEY.isKeyDown(), Main.LEFT_KEY.isKeyDown(), Main.RIGHT_KEY.isKeyDown(), Main.BRAKE_KEY.isKeyDown(), Main.START_KEY.isKeyDown());
        }

        if (Main.PLANE_KEY.isKeyDown()) {
            if (!wasGuiPressed) {
                plane.openGUI(player);
                wasGuiPressed = true;
            }
        } else {
            wasGuiPressed = false;
        }
    }

}
