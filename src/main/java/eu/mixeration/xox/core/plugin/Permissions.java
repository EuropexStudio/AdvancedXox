package eu.mixeration.xox.core.plugin;

import org.bukkit.command.CommandSender;
public class Permissions {
    public static final String COMMAND_ADMIN = "xox.admin";
    public static boolean tryPerm(CommandSender commandSender, String perm) {
        if (commandSender.hasPermission(perm)) {
            return true;
        }

        Locale.sendMessage(commandSender, Locale.ERROR_PERMISSION_DENIED);
        return false;
    }
}
