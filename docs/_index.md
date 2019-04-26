# Broadleaf Commerce Community Module

[Check it out on GitHub](https://github.com/BroadleafCommerce/blc-google-storage)

## Google Storage

The broadleaf-google-storage module adds support for Google Storage functionality. Currently, this includes a Google Storage compatible `FileServiceProvider` implementation and registers that file provider as the default for the system.

The BroadleafFileService uses providers when working with files that are created by the system. This includes images and other media uploaded through the Broadleaf Admin as well as files generated by the system (e.g. site maps).

Distributed storage is required when running in a multi-server environment to ensure that images loaded on one server are available to the others in the cluster.  Other common alternatives are to use a shared drive via an NFS mount and configuring the `asset.server.file.system.path` property.

## Module Configuration
This module is compatible with BroadleafCommerce **5.1.0-GA**.

For step-by-step instructions on how to integrate the rackspace module into your project, see [[Module Installation]].