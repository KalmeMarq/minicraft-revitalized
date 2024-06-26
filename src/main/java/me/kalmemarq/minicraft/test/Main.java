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

package me.kalmemarq.minicraft.test;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Main {
	public static final Predicate<String> PATTERN = Pattern.compile("[a-zA-Z0-9_]+").asMatchPredicate();

    public static void main(String[] args) {
//        try {
//            var interf = NetworkInterface.getNetworkInterfaces();
//
//            while (interf.hasMoreElements()) {
//                var inter = interf.nextElement();
//                System.out.println(inter);
//            }
//        } catch (SocketException e) {
//            throw new RuntimeException(e);
//        }
//		new Registry.Identifier("apple;");

		{
			long d = System.currentTimeMillis();
			String namespace = "minicraft";
			for (int i = 0; i < 10000; ++i) {
				matches0(namespace);
			}
			long e = System.currentTimeMillis();
			System.out.println(e - d);
		}

		{
			long d = System.currentTimeMillis();
			String namespace = "minicraft";
			for (int i = 0; i < 10000; ++i) {
				matches1(namespace);
			}
			long e = System.currentTimeMillis();
			System.out.println(e - d);
		}

		{
			long d = System.currentTimeMillis();
			String namespace = "minicraft";
			for (int i = 0; i < 10000; ++i) {
				matches2(namespace);
			}
			long e = System.currentTimeMillis();
			System.out.println(e - d);
		}
    }

	private static boolean matches0(String namespace) {
		return namespace.matches("[a-zA-Z0-9_]+");
	}

	private static boolean matches1(String namespace) {
		for (int j = 0; j < namespace.length(); ++j) {
			if (!isValidNamespaceChar(namespace.charAt(j))) {
				return false;
			}
		}
		return true;
	}

	private static boolean matches2(String namespace) {
		return PATTERN.test(namespace);
	}

	private static boolean isValidNamespaceChar(char chr) {
		return (chr >= '0' && chr <= '9') || (chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z') || chr == '_';
	}
}
