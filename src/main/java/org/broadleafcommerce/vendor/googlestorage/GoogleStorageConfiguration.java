/*
 * #%L
 * BroadleafCommerce Google Storage
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.vendor.googlestorage;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class GoogleStorageConfiguration {

    private String projectId;
    private String bucket;
    private String pathToJsonKeyFile;
    private String containerSubdirectory;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPathToJsonKeyFile() {
        return pathToJsonKeyFile;
    }

    public void setPathToJsonKeyFile(String pathToJsonKeyFile) {
        this.pathToJsonKeyFile = pathToJsonKeyFile;
    }

    public String getContainerSubdirectory() {
        return containerSubdirectory;
    }

    public void setContainerSubdirectory(String containerSubdirectory) {
        this.containerSubdirectory = containerSubdirectory;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(projectId)
                .append(bucket)
                .append(pathToJsonKeyFile)
                .append(containerSubdirectory)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GoogleStorageConfiguration) {
            GoogleStorageConfiguration that = (GoogleStorageConfiguration) obj;
            return new EqualsBuilder()
                    .append(this.projectId, that.projectId)
                    .append(this.bucket, that.bucket)
                    .append(this.pathToJsonKeyFile, that.pathToJsonKeyFile)
                    .append(this.containerSubdirectory, that.containerSubdirectory)
                    .build();
        }
        return false;
    }

}
