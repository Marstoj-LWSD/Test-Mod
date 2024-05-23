package net.marstoj.testmod.entity;

import net.marstoj.testmod.TestMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;


public record ElectricFurnacePayload(BlockPos blockPos) implements CustomPayload {
    public static final Id<ElectricFurnacePayload> ID = CustomPayload.id(TestMod.MOD_ID + ":electric_furnace_payload");
    public static final PacketCodec<PacketByteBuf, ElectricFurnacePayload> CODEC = PacketCodec.of(
            ((value, buf) -> buf.writeBlockPos(value.blockPos)),
            buf -> new ElectricFurnacePayload(buf.readBlockPos())
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
