package com.miketheshadow.ouchmybones;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class InjureTabComplete implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> possibleValues = new ArrayList<>();
        if(command.getName().equals("injure")) {
            if(args.length == 1) {
                possibleValues.add("add");
                possibleValues.add("remove");
            }
            if(args.length == 2) {
                return null;
            }
            if(args.length == 3) {
                for(InjuryType type : InjuryType.values()) {
                    possibleValues.add(type.toString());
                }
            }
        }
        return possibleValues;
    }
}
