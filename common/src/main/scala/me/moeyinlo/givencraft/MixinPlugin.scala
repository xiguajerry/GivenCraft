package me.moeyinlo.givencraft

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.{IMixinConfigPlugin, IMixinInfo}

import java.util

class MixinPlugin extends IMixinConfigPlugin {
    override def onLoad(mixinPackage: String): Unit = return

    override def getRefMapperConfig: String | Null = null

    override def shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = true

    override def acceptTargets(myTargets: util.Set[String], otherTargets: util.Set[String]): Unit = return

    override def getMixins: util.List[String] | Null = null

    override def preApply(targetClassName: String, targetClass: ClassNode, mixinClassName: String,
                          mixinInfo: IMixinInfo): Unit = return

    override def postApply(targetClassName: String, targetClass: ClassNode, mixinClassName: String,
                           mixinInfo: IMixinInfo): Unit = return
}
