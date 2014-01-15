/*
 * The MIT License
 *
 * Copyright 2014 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.netbeans.modules.nbmongo;

import java.awt.Image;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Yann D'Isanto
 */
public interface Images {
    
    
    @StaticResource
    String MONGO_ICON_PATH = "org/netbeans/modules/nbmongo/images/mongo-small.png"; //NOI18N
    
    @StaticResource
    String COLLECTION_ICON_PATH = "org/netbeans/modules/nbmongo/images/table.gif"; //NOI18N

    @StaticResource
    String SYSTEM_COLLECTION_ICON_PATH = "org/netbeans/modules/nbmongo/images/tableSystem.gif"; //NOI18N

    @StaticResource
    String CONNECTION_ICON_PATH = "org/netbeans/modules/nbmongo/images/connection.gif"; //NOI18N

    @StaticResource
    String CONNECTION_DISCONNECTED_ICON_PATH = "org/netbeans/modules/nbmongo/images/connectionDisconnected.gif"; //NOI18N

    @StaticResource
    String DB_ICON_PATH = "org/netbeans/modules/nbmongo/images/database.gif"; //NOI18N

    Image COLLECTION_ICON = ImageUtilities.loadImage(COLLECTION_ICON_PATH);

    Image SYSTEM_COLLECTION_ICON = ImageUtilities.loadImage(SYSTEM_COLLECTION_ICON_PATH);

    Image CONNECTION_ICON = ImageUtilities.loadImage(CONNECTION_ICON_PATH); //NOI18N

    Image CONNECTION_DISCONNECTED_ICON = ImageUtilities.loadImage(CONNECTION_DISCONNECTED_ICON_PATH);

}
