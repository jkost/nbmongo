NetBeans MongoDB
================

[NetBeans](http://netbeans.org) plugin for accessing [MongoDB](http://mongodb.org). It adds
a node to the Services tab. Right click it to add connections.

Current release: [8.1.2](https://github.com/le-yams/netbeans-mongodb/releases/tag/nbmongo-8.1.2)

Next version (master) status: ![](https://le-yams.ci.cloudbees.com/buildStatus/icon?job=NBMongo%20master%20build)

Main features
-------------

 * Connect to MongoDB using mongo standard uri
 * Browse/Create/Drop databases
 * Browse/Create/Rename/Clear/Delete collections
 * Manage collections indexes
 * Query documents (json criteria/projection/sort can be specified)
 * Perform Map/Reduce
 * Add/Edit/Delete documents as json
 * Import/Export json
 * Use some mongo [native tools](https://github.com/le-yams/netbeans-mongodb/wiki/MongoNativeTools) as mongo shell, mongodump, mongorestore, etc. (mongo bin folder path must be configured in options)

See [release notes](https://github.com/le-yams/netbeans-mongodb/wiki/ReleaseNotes) for more informations.

![NBMongo explorer](https://raw.githubusercontent.com/le-yams/netbeans-mongodb/master/screenshots/screen-explorer.png "NBMongo explorer")

![NBMongo result tree](https://raw.githubusercontent.com/le-yams/netbeans-mongodb/master/screenshots/screen-query-result-tree.png "NBMongo result tree")

![NBMongo map reduce](https://raw.githubusercontent.com/le-yams/netbeans-mongodb/master/screenshots/screen-mapreduce.png "NBMongo map reduce")

Get and Install
---------------

NBMongo is directly available through the NetBeans Plugin Portal Update Center. 
In NetBeans, go to **_Tools > Plugins_** and on the **_Available Plugins_** tab search for _NBMongo_. Select the plugin and hit the _Install_ button.
See also [NBMongo Plugin Portal page](http://plugins.netbeans.org/plugin/52638).

If you want to try the development version, as it's a Maven project built using the NBM Maven Plugin, just check out and build.
In NetBeans, install using Tools | Plugins on the Downloaded tab.


Licenses
-------
 * GPLv2
 * MIT


Other
-----
![](https://www.cloudbees.com/sites/default/files/styles/large/public/Button-Powered-by-CB.png)
