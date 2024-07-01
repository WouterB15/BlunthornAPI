package net.wouterb.blunthornapi.core.mixin.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.wouterb.blunthornapi.api.context.ItemActionContext;
import net.wouterb.blunthornapi.api.event.ItemUseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Equipment.class)
public interface EquipmentMixin {
    @Inject(method = "equipAndSwap", at = @At("HEAD"), cancellable = true)
    default public void equipAndSwap(Item item, World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
        ItemActionContext context = new ItemActionContext(world, user, hand, item.getDefaultStack());
        ActionResult result = ItemUseEvent.emit(context);
        if (result == ActionResult.FAIL)
            ci.setReturnValue(new TypedActionResult<>(result, context.getItemStack()));
    }
}
