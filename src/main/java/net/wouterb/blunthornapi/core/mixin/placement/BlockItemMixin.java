package net.wouterb.blunthornapi.core.mixin.placement;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.wouterb.blunthornapi.api.context.BlockActionContext;
import net.wouterb.blunthornapi.api.event.BlockPlaceEvent;
import net.wouterb.blunthornapi.api.permission.LockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wouterb.blunthornapi.api.Util.getBlockId;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    public void place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> ci) {
        BlockActionContext blockActionContext = new BlockActionContext(
                context.getWorld(), context.getPlayer(), context.getBlockPos(), getBlockId(context.getWorld(), context.getBlockPos()), LockType.PLACEMENT);
        ActionResult result = BlockPlaceEvent.emit(blockActionContext);

        if (result == ActionResult.FAIL) {
            ci.setReturnValue(result);
        }
    }
}
