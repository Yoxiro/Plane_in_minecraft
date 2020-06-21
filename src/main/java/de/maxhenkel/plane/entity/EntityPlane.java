package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class EntityPlane extends EntityPlaneSoundBase {

    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityPlane.class, DataSerializers.VARINT);

    public EntityPlane(World world) {
        this(Main.PLANE_ENTITY_TYPE, world);
    }

    public EntityPlane(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Type", getPlaneType().getTypeName());
    }

    @Override
    public float getPlayerScaleFactor() {
        return 0.8F;
    }

    @Override
    public void openGUI(PlayerEntity player, boolean outside) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return getName();
                }

                @Nullable
                @Override
                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    return new ContainerPlane(i, EntityPlane.this, playerInventory);
                }
            }, packetBuffer -> {
                packetBuffer.writeUniqueId(getUniqueID());
            });
        } else {
            Main.SIMPLE_CHANNEL.sendToServer(new MessagePlaneGui(player, outside));
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setPlaneType(Type.fromTypeName(compound.getString("Type")));
    }

    @Override
    public ResourceLocation getLootTable() {
        return new ResourceLocation(Main.MODID, "entities/plane_" + getPlaneType().getTypeName());
    }

    @Override
    public float getMaxFuelUsage() {
        return 5F;
    }

    @Override
    public int getMaxFuel() {
        return 5000;
    }

    @Override
    public double getFallSpeed() {
        return 0.1D;
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(TYPE, 0);
    }

    @Override
    public Vec3d[] getPlayerOffsets() {
        return new Vec3d[]{new Vec3d(0D, 0D, 1D), new Vec3d(0D, 0D, 0.5D), new Vec3d(0D, 0D, 0D)};
    }

    public Type getPlaneType() {
        return Type.values()[dataManager.get(TYPE)];
    }

    public void setPlaneType(Type type) {
        dataManager.set(TYPE, type.ordinal());
    }

    public static enum Type {
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getTypeName() {
            return name;
        }

        public static Type fromTypeName(String name) {
            for (Type type : values()) {
                if (type.getTypeName().equals(name)) {
                    return type;
                }
            }
            return OAK;
        }
    }
}
