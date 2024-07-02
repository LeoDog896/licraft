package com.leodog896.licraft;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Messages {
    public static final String PREFIX = "<b><gradient:green:#34eb89>LICRAFT</gradient></b> <gray>»</gray> ";
    public static final String WARNING = "<b><gradient:red:#f5a142>WARNING</gradient><red>!</red></b> ";
    public static final String ERROR = "<b><gradient:red:#f55442>ERROR</gradient><red>!</red></b> ";

    public static final Component NOT_IN_GAME = MiniMessage.miniMessage().deserialize(
            Messages.ERROR + "You are not in a game!"
    );
}
