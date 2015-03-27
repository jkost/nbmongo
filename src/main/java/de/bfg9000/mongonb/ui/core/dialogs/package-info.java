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
@Messages({
    "IndexManagerDialog.btnCancel.text=Cancel",
    "IndexManagerDialog.btnRemoveIndex.text=Remove",
    "IndexManagerDialog.btnAddIndex.text=Add...",
    "IndexManagerTableModel.column.name.name=Name",
    "IndexManagerTableModel.column.sparse.name=Sparse",
    "IndexManagerTableModel.column.unqiue.name=Unique",
    "IndexManagerTableModel.column.dropDuplicates.name=Drop Duplicates",
    "IndexManagerDialog.btnOK.text=OK",
    "IndexManagerDialog.title=Index Manager",
    "IndexManagerDialog.columnHeader.attributes=Attributes",
    "IndexManagerDialog.columnHeader.options=Options",
    "IndexManagerDialog.duplicates.message=Do you want to have them removed automatically?",
    "IndexManagerDialog.duplicates.removed-single=One duplicate has been removed.",
    "IndexManagerDialog.duplicates.removed-multi={0} duplicates have been removed.",
    "IndexManagerDialog.empty.message=Do you want to have it removed automatically?",
    "IndexManagerDialog.empty.removed-single=One index without attributes has been removed.",
    "IndexManager.duplicateError=There are one or more duplicated indexes (e.g. {0} and {1}). ",
    "IndexManager.message.duplicateKey-single=The index on attribute {0} can not be created - it would have duplicate keys.\nYou can activate the \"Drop Duplicates\" option to auto-remove duplicate entries.",
    "IndexManager.message.duplicateKey-multi=The index on attributes {0} can not be created - it would have duplicate keys.\nYou can activate the \"Drop Duplicates\" option to auto-remove duplicate entries.",
    "IndexManager.emptyError-single=There is a index on position {0} that has no attributes assigned.",
    "IndexManager.emptyError-multi=There are indexes on positions {0} that have no attributes assigned."
})
package de.bfg9000.mongonb.ui.core.dialogs;

import org.openide.util.NbBundle.Messages;
