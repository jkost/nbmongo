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
package org.netbeans.modules.mongodb.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Yann D'Isanto
 */
public class TopComponentUtils {

    public static <T extends TopComponent> boolean isNotActivated(Class<T> topComponentType, Object data) {
        return isActivated(topComponentType, data) == false;
    }
    
    public static <T extends TopComponent> boolean isActivated(Class<T> topComponentType, Object data) {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        if(tc == null) {
            return false;
        }
        if(topComponentType.isAssignableFrom(tc.getClass())) {
            return tc.getLookup().lookup(data.getClass()) == data;
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends TopComponent> T find(Class<T> topComponentType, Object data) {
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if(topComponentType.isAssignableFrom(tc.getClass())) {
                if (tc.getLookup().lookup(data.getClass()) == data) {
                    return (T) tc;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends TopComponent> Collection<T> findAll(Class<T> topComponentType, Object data) {
        final List<T> result = new ArrayList<>();
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if(topComponentType.isAssignableFrom(tc.getClass())) {
                if (tc.getLookup().lookup(data.getClass()) == data) {
                    result.add((T) tc);
                }
            }
        }
        return result;
    }

    @SafeVarargs
    public static Collection<TopComponent> findAll(Object data, Class<? extends TopComponent>... topComponentTypes) {
        final List<TopComponent> result = new ArrayList<>();
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if(checkType(tc.getClass(), topComponentTypes) && tc.getLookup().lookup(data.getClass()) == data) {
                result.add(tc);
            }
        }
        return result;
    }

    private static boolean checkType(Class<?> c, Class<?>... types) {
        if(types.length == 0) {
            return true;
        }
        for (Class<?> type : types) {
            if(type.isAssignableFrom(c)) {
                return true;
            }
        }
        return false;
    }
    
    private TopComponentUtils() {
    }

}
