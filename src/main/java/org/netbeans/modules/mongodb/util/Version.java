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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Yann D'Isanto
 */
public final class Version implements Comparable<Version> {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(\\.(\\d+))?");

    private final int major;

    private final int minor;

    private final int patch;

    private final String stringValue;

    
    public Version(int major, int minor) {
        this(major, minor, 0);
    }
    
    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        stringValue = new StringBuilder()
            .append(major)
            .append('.')
            .append(minor)
            .append('.')
            .append(patch)
            .toString();
    }

    public Version(String string) {
        final Matcher matcher = PATTERN.matcher(string);
        if (matcher.find() == false) {
            throw new IllegalArgumentException("invalid version string");
        }
        this.major = Integer.parseInt(matcher.group(1));
        this.minor = Integer.parseInt(matcher.group(2));
        final String patchGroup = matcher.group(4);
        this.patch = patchGroup != null
            ? Integer.parseInt(patchGroup)
            : 0;
        this.stringValue = string;
    }

    @Override
    public int compareTo(Version other) {
        int comp = major - other.major;
        if (comp == 0) {
            comp = minor - other.minor;
            if (comp == 0) {
                comp = patch - other.patch;
            }
        }
        return comp;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.major;
        hash = 19 * hash + this.minor;
        hash = 19 * hash + this.patch;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Version other = (Version) obj;
        if (this.major != other.major) {
            return false;
        }
        if (this.minor != other.minor) {
            return false;
        }
        return this.patch == other.patch;
    }
}
