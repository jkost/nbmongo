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

import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "# {0} - collection name",
    "TASK_import_label=import in \"{0}\" collection"
})
public final class ImportTask extends AbstractTask<Importer> {

    private static final RequestProcessor REQUEST_PROCESSOR = new RequestProcessor("import tasks", 1, true);

    public ImportTask(Importer importer) {
        super(REQUEST_PROCESSOR, importer);
    }

    @Override
    public String getLabel() {
        return Bundle.TASK_import_label(getRunnable().getProperties().getCollection());
    }

}
