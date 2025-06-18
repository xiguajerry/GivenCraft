package me.moeyinlo.givencraft.fabric

import me.moeyinlo.givencraft.GivenCraft
import net.fabricmc.api.ModInitializer

class GivenCraft extends ModInitializer:
    override def onInitialize(): Unit =
        GivenCraft.initialize()