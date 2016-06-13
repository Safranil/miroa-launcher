/**
 * This file is part of Miroa Launcher.
 * Copyright (C) 2016 David Cachau <dev@safranil.fr>
 *
 * Miroa Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Miroa Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Miroa Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.safranil.minecraft.miroa;

/**
 * Memory option that can be displayed
 */
class MemoryOption {
    private final String javaOption;
    private final String displayString;

    MemoryOption(String javaOption, String displayString) {
        this.javaOption = javaOption;
        this.displayString = displayString;
    }

    String getJavaOption() {
        return javaOption;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
