/* 
 * Copyright (C) 2015 Yann D'Isanto
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.netbeans.modules.mongodb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Yann D'Isanto
 */
public final class ProcessCreator implements Callable<Process> {

    private final List<String> commandLine;

    public ProcessCreator(List<String> commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public Process call() throws Exception {
        return new ProcessBuilder(commandLine).start();
    }

    public final static class Builder {

        private String command;

        private final Map<String, String> options = new HashMap<>();
        
        private final List<String> args = new ArrayList<>();

        public Builder() {
        }

        public Builder(String command) {
            this.command = command;
        }

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder option(String optionName) {
            options.put(optionName, "");
            return this;
        }

        public Builder option(String optionName, String optionValue) {
            options.put(optionName, optionValue);
            return this;
        }

        public Builder options(Map<String, String> options) {
            this.options.putAll(options);
            return this;
        }

        public Builder arg(String arg) {
            args.add(arg);
            return this;
        }

        public Builder args(String... args) {
            return args(Arrays.asList(args));
        }

        public Builder args(List<String> args) {
            this.args.addAll(args);
            return this;
        }

        public ProcessCreator build() {
            final List<String> commandLine = new ArrayList<>();
            commandLine.add(command);
            for (Map.Entry<String, String> option : options.entrySet()) {
                commandLine.add(option.getKey());
                final String optionValue = option.getValue();
                if (optionValue.isEmpty() == false) {
                    commandLine.add(optionValue);
                }
            }
            commandLine.addAll(args);
            return new ProcessCreator(commandLine);
        }
    }

}
