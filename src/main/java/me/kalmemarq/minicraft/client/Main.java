/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.client;

import me.kalmemarq.argoption.ArgOption;
import me.kalmemarq.argoption.ArgOptionParser;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from client");

        ArgOptionParser optionParser = new ArgOptionParser();
        ArgOption<Path> saveDirArg = optionParser.add("saveDir", Path.class).defaultsTo(Path.of("").toAbsolutePath());
        optionParser.parseArgs(args);

        new Client(optionParser.getValue(saveDirArg)).run();
    }
}