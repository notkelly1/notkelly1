package com.imapotatoes11.wmd.item.custom;

import com.imapotatoes11.wmd.item.custom.lib.ParticleInfo;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.Queue;

public class ItemDematerializer extends Item {
    private Queue<BlockPos> BlockQ = new LinkedList<>();

    private Queue<ParticleInfo> ParticleQ = new LinkedList<>();

    public ItemDematerializer(Settings settings){
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        HitResult hitResult = user.raycast(512d, 0.0f, false);
        if (hitResult.getType() == HitResult.Type.BLOCK ||
        hitResult.getType() == HitResult.Type.ENTITY){
            try {
                BlockQ.removeAll(BlockQ);
                ParticleQ.removeAll(ParticleQ);
            } catch (Exception ignored){}
            int radius=100; // 50
            for (int x=-radius; x < radius; x++){
                for (int y=-radius; y < radius; y++){
                    for (int z=-radius; z < radius; z++){
                        BlockPos pos = new BlockPos(
                                (int) Math.floor(hitResult.getPos().x) + x,
                                (int) Math.floor(hitResult.getPos().y) + y,
                                (int) Math.floor(hitResult.getPos().z) + z
                        );
                        if (!world.isAir(pos) &&
                                world.getBlockState(pos).getBlock()!= Blocks.BEDROCK &&
                                world.getBlockState(pos).getBlock()!= Blocks.END_PORTAL_FRAME &&
                                world.getBlockState(pos).getBlock()!= Blocks.END_PORTAL &&
                                world.getBlockState(pos).getBlock()!= Blocks.END_GATEWAY &&
                                world.getBlockState(pos).getBlock()!= Blocks.BARRIER &&
                                world.getBlockState(pos).getBlock()!= Blocks.COMMAND_BLOCK &&
                                world.getBlockState(pos).getBlock()!= Blocks.CHAIN_COMMAND_BLOCK &&
                                world.getBlockState(pos).getBlock()!= Blocks.REPEATING_COMMAND_BLOCK &&
                                world.getBlockState(pos).getBlock()!= Blocks.STRUCTURE_BLOCK &&
                                world.getBlockState(pos).getBlock()!= Blocks.JIGSAW &&
                                world.getBlockState(pos).getBlock()!= Blocks.STRUCTURE_VOID &&
                                world.getBlockState(pos).getBlock()!= Blocks.NETHER_PORTAL &&
                                world.getBlockState(pos).getBlock()!= Blocks.SPAWNER
                        ) BlockQ.add(
                                    new BlockPos(
                                            (int) Math.floor(hitResult.getPos().x) + x,
                                            (int) Math.floor(hitResult.getPos().y) + y,
                                            (int) Math.floor(hitResult.getPos().z) + z
                                    )
                            );
//                        user.playSound(SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        ParticleQ.add(new ParticleInfo(hitResult.getPos().x+x, hitResult.getPos().y+y, hitResult.getPos().z+z, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE));
                    }
                }
            }

            user.sendMessage(Text.of( "(" +
                            hitResult.getPos().getX() + ", " +
                            hitResult.getPos().y + ", " +
                            hitResult.getPos().z + ")"
                    ), true);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) {
            for (int i=0; i< 2500; i++) { // for loop cuz its too slow, default 2500
                try {
                    if (!BlockQ.isEmpty()) {
//                    world.breakBlock(BlockQ.remove(), false); // making the blocks drop make way too many items
                        world.setBlockState(BlockQ.remove(), Blocks.AIR.getDefaultState()); // more efficient
//                        try {
//                            if (i % 100 == 0)
//                                world.getPlayers().get(0).playSound(SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
//                        } catch (Exception ignored){}
                    }
                    if (!ParticleQ.isEmpty()) {
                        ParticleInfo particle = ParticleQ.remove();
                        world.addImportantParticle(particle.particle, particle.vec.getX(), particle.vec.getY(), particle.vec.getZ(),
                                0, 0, 0);
                    }
                } catch (Exception ignored){}
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
