package net.wouterb.blunthornapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.wouterb.blunthornapi.api.event.*;
import net.wouterb.blunthornapi.api.permission.LockType;
import net.wouterb.blunthornapi.api.permission.Permission;
import net.wouterb.blunthornapi.core.data.IEntityDataSaver;
import net.wouterb.blunthornapi.core.event.RegisteredFabricEvents;
import net.wouterb.blunthornapi.core.util.ClientServerLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlunthornAPI implements ModInitializer {
	public static final String MOD_ID = "blunthornapi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting Blunthorn API");
		registerFabricEvents();
		setupTestLocks();
		registerTestEvents();
	}

	private static void registerFabricEvents() {
		AttackBlockCallback.EVENT.register(RegisteredFabricEvents::onBlockAttack);
		PlayerBlockBreakEvents.BEFORE.register(RegisteredFabricEvents::onBlockBreakBefore);
		PlayerBlockBreakEvents.AFTER.register(RegisteredFabricEvents::onBlockBreakAfter);
		UseItemCallback.EVENT.register(RegisteredFabricEvents::onUseItem);
		UseBlockCallback.EVENT.register(RegisteredFabricEvents::onUseBlock);
		AttackEntityCallback.EVENT.register(RegisteredFabricEvents::onAttackEntity);
		UseEntityCallback.EVENT.register(RegisteredFabricEvents::onUseEntity);
	}

	private static void setupTestLocks() {
		String[] lockedValues = {"minecraft:grass_block", "minecraft:oak_*", "#minecraft:leaves"};
		NbtCompound nbtData = new NbtCompound();
		NbtList nbtList = new NbtList();
		for (String id : lockedValues) {
			nbtList.add(NbtString.of(id));
		}
		nbtData.put(LockType.BREAKING.toString(), nbtList);

		ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, sender, server) -> {
			ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
			((IEntityDataSaver) player).blunthornapi$setPersistentData("test", nbtData);
			System.out.println(((IEntityDataSaver) player).blunthornapi$getPersistentData("test"));
		});


	}

	private static void registerTestEvents() {
		BlockBreakEvent.ATTACK.register(blockActionContext -> {
			ClientServerLogger.info("Attack block event!", blockActionContext.isClient());
			if (Permission.isObjectLocked(blockActionContext, "test"))
				return ActionResult.FAIL;
			return ActionResult.PASS;
		});

		BlockBreakEvent.BEFORE.register(blockActionContext -> {
			ClientServerLogger.info("Before block break event!", blockActionContext.isClient());
			if (Permission.isObjectLocked(blockActionContext, "test"))
				return ActionResult.FAIL;
			return ActionResult.PASS;
		});

		BlockBreakEvent.AFTER.register(blockActionContext -> {
			ClientServerLogger.info("After block break event!", blockActionContext.isClient());
			if (Permission.isObjectLocked(blockActionContext, "test"))
				return ActionResult.FAIL;
			return ActionResult.PASS;
		});

		BlockPlaceEvent.EVENT.register(blockActionContext -> {
			ClientServerLogger.info("Block placement event!", blockActionContext.isClient());
			return ActionResult.PASS;
		});

		BlockUseEvent.EVENT.register(blockActionContext -> {
			ClientServerLogger.info("Block use event!", blockActionContext.isClient());
			return ActionResult.PASS;
		});

		ItemUseEvent.EVENT.register(itemActionContext -> {
			ClientServerLogger.info("Item use event!", itemActionContext.isClient());
			return ActionResult.PASS;
		});

		EntityUseEvent.EVENT.register(entityActionContext -> {
			ClientServerLogger.info("Entity use event!", entityActionContext.isClient());
			return ActionResult.PASS;
		});

		ObjectCraftedEvent.EVENT.register(itemActionContext -> {
			ClientServerLogger.info("Object crafted event!", itemActionContext.isClient());
			return ActionResult.PASS;
		});

		EntityItemDropEvent.EVENT.register(entityActionContext -> {
			ClientServerLogger.info("Entity item drop event!", entityActionContext.isClient());
			return ActionResult.PASS;
		});

	}
}