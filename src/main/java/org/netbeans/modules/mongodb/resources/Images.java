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
package org.netbeans.modules.mongodb.resources;

import java.awt.Image;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Yann D'Isanto
 */
public interface Images {
    
    
    @StaticResource
    String MONGO_ICON_PATH = "org/netbeans/modules/mongodb/images/mongo-small.png"; //NOI18N
    
    @StaticResource
    String COLLECTION_ICON_PATH = "org/netbeans/modules/mongodb/images/collection.png"; //NOI18N

    @StaticResource
    String SYSTEM_COLLECTION_ICON_PATH = "org/netbeans/modules/mongodb/images/systemCollection.png"; //NOI18N

    @StaticResource
    String CONNECTION_ICON_PATH = "org/netbeans/modules/mongodb/images/connection.gif"; //NOI18N

    @StaticResource
    String CONNECTION_DISCONNECTED_ICON_PATH = "org/netbeans/modules/mongodb/images/connectionDisconnected.gif"; //NOI18N

    @StaticResource
    String DB_ICON_PATH = "org/netbeans/modules/mongodb/images/database.gif"; //NOI18N

    @StaticResource
    String REFRESH_ICON_PATH = "org/netbeans/modules/mongodb/images/refresh.png"; //NOI18N

    @StaticResource
    String NAV_FIRST_ICON_PATH = "org/netbeans/modules/mongodb/images/navigate_beginning.png"; //NOI18N

    @StaticResource
    String NAV_LEFT_ICON_PATH = "org/netbeans/modules/mongodb/images/navigate_left.png"; //NOI18N

    @StaticResource
    String NAV_RIGHT_ICON_PATH = "org/netbeans/modules/mongodb/images/navigate_right.png"; //NOI18N

    @StaticResource
    String NAV_LAST_ICON_PATH = "org/netbeans/modules/mongodb/images/navigate_end.png"; //NOI18N

    @StaticResource
    String ADD_DOCUMENT_ICON_PATH = "org/netbeans/modules/mongodb/images/row_add.png"; //NOI18N

    @StaticResource
    String DELETE_DOCUMENT_ICON_PATH = "org/netbeans/modules/mongodb/images/row_delete.png"; //NOI18N

    @StaticResource
    String EDIT_DOCUMENT_ICON_PATH = "org/netbeans/modules/mongodb/images/row_edit.png"; //NOI18N

    @StaticResource
    String EXPORT_COLLECTION_ICON_PATH = "org/netbeans/modules/mongodb/images/export_collection.png"; //NOI18N

    @StaticResource
    String TREE_TABLE_VIEW_ICON_PATH = "org/netbeans/modules/mongodb/images/chart_organisation.png"; //NOI18N

    @StaticResource
    String FLAT_TABLE_VIEW_ICON_PATH = "org/netbeans/modules/mongodb/images/flat_table.png"; //NOI18N

    @StaticResource
    String EXPAND_TREE_PATH = "org/netbeans/modules/mongodb/images/expandTree.png"; //NOI18N

    @StaticResource
    String COLLAPSE_TREE_PATH = "org/netbeans/modules/mongodb/images/collapseTree.png"; //NOI18N

    Image COLLECTION_ICON = ImageUtilities.loadImage(COLLECTION_ICON_PATH);

    Image SYSTEM_COLLECTION_ICON = ImageUtilities.loadImage(SYSTEM_COLLECTION_ICON_PATH);

    Image CONNECTION_ICON = ImageUtilities.loadImage(CONNECTION_ICON_PATH);

    Image CONNECTION_DISCONNECTED_ICON = ImageUtilities.loadImage(CONNECTION_DISCONNECTED_ICON_PATH);

    Image REFRESH_ICON = ImageUtilities.loadImage(REFRESH_ICON_PATH);

    Image NAV_FIRST_ICON = ImageUtilities.loadImage(NAV_FIRST_ICON_PATH);

    Image NAV_LEFT_ICON = ImageUtilities.loadImage(NAV_LEFT_ICON_PATH);

    Image NAV_RIGHT_ICON = ImageUtilities.loadImage(NAV_RIGHT_ICON_PATH);

    Image NAV_LAST_ICON = ImageUtilities.loadImage(NAV_LAST_ICON_PATH);

    Image ADD_DOCUMENT_ICON = ImageUtilities.loadImage(ADD_DOCUMENT_ICON_PATH);

    Image DELETE_DOCUMENT_ICON = ImageUtilities.loadImage(DELETE_DOCUMENT_ICON_PATH);

    Image EDIT_DOCUMENT_ICON = ImageUtilities.loadImage(EDIT_DOCUMENT_ICON_PATH);

    Image EXPORT_COLLECTION_ICON = ImageUtilities.loadImage(EXPORT_COLLECTION_ICON_PATH);

    Image TREE_TABLE_VIEW_ICON = ImageUtilities.loadImage(TREE_TABLE_VIEW_ICON_PATH);

    Image FLAT_TABLE_VIEW_ICON = ImageUtilities.loadImage(FLAT_TABLE_VIEW_ICON_PATH);

    Image EXPAND_TREE_ICON = ImageUtilities.loadImage(EXPAND_TREE_PATH);

    Image COLLAPSE_TREE_ICON = ImageUtilities.loadImage(COLLAPSE_TREE_PATH);

}
