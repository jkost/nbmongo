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
    "CollectionStats.serverUsed.name=Server Used",
    "CollectionStats.serverUsed.displayname=Server Used",
    "CollectionStats.serverUsed.shortdesc=Server Used",
    "CollectionStats.ns.name=Namespace",
    "CollectionStats.ns.displayname=Namespace",
    "CollectionStats.ns.shortdesc=Namespace",
    "CollectionStats.count.name=Count",
    "CollectionStats.count.displayname=Count",
    "CollectionStats.count.shortdesc=Number of documents",
    "CollectionStats.capped.name=Capped",
    "CollectionStats.capped.displayname=Capped",
    "CollectionStats.capped.shortdesc=Shows whether or not this collection is capped",
    "CollectionStats.size.name=Size",
    "CollectionStats.size.displayname=Size",
    "CollectionStats.size.shortdesc=Collection size in bytes",
    "CollectionStats.storageSize.name=Storage Size",
    "CollectionStats.storageSize.displayname=Storage Size",
    "CollectionStats.storageSize.shortdesc=(Pre)allocated space in bytes",
    "CollectionStats.avgObjSize.name=Avg Object Size",
    "CollectionStats.avgObjSize.displayname=Avg Object Size",
    "CollectionStats.avgObjSize.shortdesc=Average object size in bytes",
    "CollectionStats.numExtents.name=Number Of Extents",
    "CollectionStats.numExtents.displayname=Number Of Extents",
    "CollectionStats.numExtents.shortdesc=Number of extents (contiguously allocated chunks of datafile space)",
    "CollectionStats.nindexes.name=Number Of Indexes",
    "CollectionStats.nindexes.displayname=Number Of Indexes",
    "CollectionStats.nindexes.shortdesc=Number of indexes",
    "CollectionStats.lastExtentSize.name=Last Extent Size",
    "CollectionStats.lastExtentSize.displayname=Last Extent Size",
    "CollectionStats.lastExtentSize.shortdesc=size of the most recently created extent in bytes",
    "CollectionStats.paddingFactor.name=Padding Factor",
    "CollectionStats.paddingFactor.displayname=Padding Factor",
    "CollectionStats.paddingFactor.shortdesc=Padding can speed up updates if documents grow",
    "CollectionStats.systemFlags.name=System Flags",
    "CollectionStats.systemFlags.displayname=System Flags",
    "CollectionStats.systemFlags.shortdesc=System Flags",
    "CollectionStats.userFlags.name=User Flags",
    "CollectionStats.userFlags.displayname=User Flags",
    "CollectionStats.userFlags.shortdesc=User Flags",
    "CollectionStats.totalIndexSize.name=Total Index Size",
    "CollectionStats.totalIndexSize.displayname=Total Index Size",
    "CollectionStats.totalIndexSize.shortdesc=total index size in bytes",
    "CollectionStats.ok.name=OK",
    "CollectionStats.ok.displayname=OK",
    "CollectionStats.ok.shortdesc=OK",
    "DatabaseStats.serverUsed.name=Server Used",
    "DatabaseStats.serverUsed.displayname=Server Used",
    "DatabaseStats.serverUsed.shortdesc=Server Used",
    "DatabaseStats.db.name=DB",
    "DatabaseStats.db.displayname=DB",
    "DatabaseStats.db.shortdesc=Database",
    "DatabaseStats.collections.name=Collections",
    "DatabaseStats.collections.displayname=Collections",
    "DatabaseStats.collections.shortdesc=The number of collections in that database.",
    "DatabaseStats.objects.name=Objects",
    "DatabaseStats.objects.displayname=Objects",
    "DatabaseStats.objects.shortdesc=The number of objects in the database across all collections.",
    "DatabaseStats.avgObjSize.name=Avg Object Size",
    "DatabaseStats.avgObjSize.displayname=Avg Object Size",
    "DatabaseStats.avgObjSize.shortdesc=The average size of each document in bytes.",
    "DatabaseStats.dataSize.name=Data Size",
    "DatabaseStats.dataSize.displayname=Data Size",
    "DatabaseStats.dataSize.shortdesc=The total size in bytes of the data held in this database including the padding factor.",
    "DatabaseStats.storageSize.name=Storage Size",
    "DatabaseStats.storageSize.displayname=Storage Size",
    "DatabaseStats.storageSize.shortdesc=The total amount of space in bytes allocated to collections in this database for document storage.",
    "DatabaseStats.numExtents.name=Number of extents",
    "DatabaseStats.numExtents.displayname=Number of extents",
    "DatabaseStats.numExtents.shortdesc=Contains a count of the number of extents in the database across all collections.",
    "DatabaseStats.indexes.name=Indexes",
    "DatabaseStats.indexes.displayname=Indexes",
    "DatabaseStats.indexes.shortdesc=Contains a count of the total number of indexes across all collections in the database.",
    "DatabaseStats.indexSize.name=Index Size",
    "DatabaseStats.indexSize.displayname=Index Size",
    "DatabaseStats.indexSize.shortdesc=The total size in bytes of all indexes created on this database.",
    "DatabaseStats.fileSize.name=File Size",
    "DatabaseStats.fileSize.displayname=File Size",
    "DatabaseStats.fileSize.shortdesc=The total size in bytes of the data files that hold the database.",
    "DatabaseStats.nsSizeMB.name=NS Size MB",
    "DatabaseStats.nsSizeMB.displayname=NS Size MB",
    "DatabaseStats.nsSizeMB.shortdesc=The total size of the namespace files (i.e. that end with .ns) for this database.",
    "DatabaseStats.dataFileVersion.name=Data File Version",
    "DatabaseStats.dataFileVersion.displayname=Data File Version",
    "DatabaseStats.dataFileVersion.shortdesc=Data File Version",
    "DatabaseStats.ok.name=OK",
    "DatabaseStats.ok.displayname=OK",
    "DatabaseStats.ok.shortdesc=OK"
})
package org.netbeans.modules.mongodb.ui.explorer;

import org.openide.util.NbBundle.Messages;
