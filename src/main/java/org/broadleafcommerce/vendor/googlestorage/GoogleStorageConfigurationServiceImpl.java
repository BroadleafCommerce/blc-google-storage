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

import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blGoogleStorageConfigurationService")
public class GoogleStorageConfigurationServiceImpl implements GoogleStorageConfigurationService {

    @Resource(name = "blSystemPropertiesService")
    protected SystemPropertiesService systemPropertiesService;

    @Override
    public GoogleStorageConfiguration lookupGoogleStorageConfiguration() {
        GoogleStorageConfiguration cloudConfig = new GoogleStorageConfiguration();

        String projectId = lookupProperty("broadleaf.google.storage.projectId");
        String bucket = lookupProperty("broadleaf.google.storage.bucket");
        String pathToJsonKeyFile = lookupProperty("broadleaf.google.storage.pathToJsonKeyFile");
        String containerSubDir = lookupProperty("broadleaf.google.storage.subdirectory");

        Assert.hasLength(projectId, "Google Storage projectId can not be empty.");
        Assert.hasLength(bucket, "Google Storage bucket can not be empty.");
        Assert.hasLength(pathToJsonKeyFile, "Google Storage pathToJsonKeyFile can not be empty.");

        cloudConfig.setProjectId(projectId);
        cloudConfig.setBucket(bucket);
        cloudConfig.setPathToJsonKeyFile(pathToJsonKeyFile);
        cloudConfig.setContainerSubdirectory(containerSubDir);
        return cloudConfig;
    }

    protected String lookupProperty(String propertyName) {
        return systemPropertiesService.resolveSystemProperty(propertyName);
    }

    protected void setSystemPropertiesService(SystemPropertiesService systemPropertiesService) {
        this.systemPropertiesService = systemPropertiesService;
    }
}
