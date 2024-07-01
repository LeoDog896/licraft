package com.leodog896.licraft;

import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public class FullbrightDimension {

    static DimensionType fullbright = DimensionType.builder().ambientLight(2.0f).build();
    public static DynamicRegistry.Key<DimensionType> key = MinecraftServer.getDimensionTypeRegistry().register(NamespaceID.from("world:full_bright"), fullbright);

}
