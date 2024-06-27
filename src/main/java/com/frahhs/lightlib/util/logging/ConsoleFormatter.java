package com.frahhs.lightlib.util.logging;

import com.frahhs.lightlib.LightPlugin;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class ConsoleFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        String message = formatMessage(record);

        String plugin_name = LightPlugin.getInstance().getName();

        Level level = record.getLevel();
        if(Level.WARNING.equals(level)) {
            builder .append(ConsoleColor.YELLOW)
                    .append("[")
                    .append(plugin_name)
                    .append("] ");
        } else if(Level.SEVERE.equals(level)) {
            builder .append(ConsoleColor.DARK_RED)
                    .append("[")
                    .append(plugin_name)
                    .append("] ");
        } else if(Level.CONFIG.equals(level)) {
            builder .append(ConsoleColor.DARK_BLUE)
                    .append("[")
                    .append(plugin_name)
                    .append("] ")
                    .append("[CONFIG] ");
        } else if(Level.FINE.equals(level)) {
            builder .append(ConsoleColor.BLUE)
                    .append("[")
                    .append(plugin_name)
                    .append("] ")
                    .append("[FINE] ");
        } else if(Level.FINER.equals(level)) {
            builder .append(ConsoleColor.BLUE)
                    .append("[")
                    .append(plugin_name)
                    .append("] ")
                    .append("[FINER] ");
        } else if(Level.FINEST.equals(level)) {
            builder .append(ConsoleColor.BLUE)
                    .append("[")
                    .append(plugin_name)
                    .append("] ")
                    .append("[FINEST] ");
        } else {
            builder .append("[")
                    .append(plugin_name)
                    .append("] ");
        }

        builder .append(message)
                .append(ConsoleColor.RESET);

        return builder.toString();
    }
}