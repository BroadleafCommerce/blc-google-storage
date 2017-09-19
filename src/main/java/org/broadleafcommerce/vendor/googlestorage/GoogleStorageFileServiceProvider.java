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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.file.FileServiceException;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.broadleafcommerce.common.file.service.FileServiceProvider;
import org.broadleafcommerce.common.file.service.type.FileApplicationType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

/**
 * {@link FileServiceProvider} implementation that deals with Google Storage
 *
 */
@Service("blGoogleStorageFileServiceProvider")
public class GoogleStorageFileServiceProvider implements FileServiceProvider {

    @Resource(name = "blGoogleStorageConfigurationService")
    protected GoogleStorageConfigurationService googleStorageConfigurationService;

    @Resource(name = "blFileService")
    protected BroadleafFileService fileService;
    
    @Override
    public File getResource(String name) {
        return getResource(name, FileApplicationType.ALL);
    }
    
    @Override
    public File getResource(String name, FileApplicationType fileApplicationType) {
        File returnFile = fileService.getLocalResource(buildResourceName(name));
        OutputStream outputStream = null;
        try {
            GoogleStorageConfiguration googleConfig = googleStorageConfigurationService.lookupGoogleStorageConfiguration();
            Storage storage = StorageOptions.newBuilder().setProjectId(googleConfig.getProjectId()).setCredentials(GoogleCredentials.fromStream(
                    new FileInputStream(googleConfig.getPathToJsonKeyFile()))).build().getService();

            Blob object = storage.get(BlobId.of(googleConfig.getBucket(), buildResourceName(name)));

            if (object != null) {
                byte[] content = object.getContent();


                if (!returnFile.getParentFile().exists()) {
                    if (!returnFile.getParentFile().mkdirs()) {
                        // Other thread could have created - check one more time.
                        if (!returnFile.getParentFile().exists()) {
                            throw new RuntimeException("Unable to create parent directories for file: " + name);
                        }
                    }
                }
                outputStream = new FileOutputStream(returnFile);
                new PrintStream(outputStream).write(content);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error writing CloudFiles file to local file system", ioe);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error closing output stream while writing CloudFiles file to file system", e);
                }

            }
        }
        return returnFile;
	}

    @Override
    public void addOrUpdateResources(FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        addOrUpdateResourcesForPaths(workArea, files, removeFilesFromWorkArea);
    }

    @Override
    public List<String> addOrUpdateResourcesForPaths(FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) {
        GoogleStorageConfiguration googleConfig = googleStorageConfigurationService.lookupGoogleStorageConfiguration();
        try {
            Storage storage = StorageOptions.newBuilder().setProjectId(googleConfig.getProjectId()).setCredentials(GoogleCredentials.fromStream(
                    new FileInputStream(googleConfig.getPathToJsonKeyFile()))).build().getService();
            return addOrUpdateResourcesInternal(googleConfig, storage, workArea, files, removeFilesFromWorkArea);
        } catch (IOException ioe) {
            throw new RuntimeException("Error uploading file to Google Storage", ioe);
        }
    }

    protected List<String> addOrUpdateResourcesInternal(GoogleStorageConfiguration googleConfig, Storage storage, FileWorkArea workArea, List<File> files, boolean removeFilesFromWorkArea) throws IOException {
        List<String> resourcePaths = new ArrayList<String>();
        for (File srcFile : files) {
            if (!srcFile.getAbsolutePath().startsWith(workArea.getFilePathLocation())) {
                throw new FileServiceException("Attempt to update file " + srcFile.getAbsolutePath() +
                        " that is not in the passed in WorkArea " + workArea.getFilePathLocation());
            }

            String fileName = srcFile.getAbsolutePath().substring(workArea.getFilePathLocation().length());
            String resourceName = buildResourceName(fileName);
            Blob blob = storage.create(BlobInfo.newBuilder(googleConfig.getBucket(), resourceName).build(),
                    Files.readAllBytes(srcFile.toPath()));
            resourcePaths.add(fileName);
        }

        return resourcePaths;
    }

    @Override
    public boolean removeResource(String name) {
        GoogleStorageConfiguration googleConfig = googleStorageConfigurationService.lookupGoogleStorageConfiguration();
        try {
            Storage storage = StorageOptions.newBuilder().setProjectId(googleConfig.getProjectId()).setCredentials(GoogleCredentials.fromStream(
                    new FileInputStream(googleConfig.getPathToJsonKeyFile()))).build().getService();
            storage.delete(BlobId.of(googleConfig.getBucket(),buildResourceName(name)));
            File returnFile = fileService.getLocalResource(buildResourceName(name));
            if (returnFile != null) {
                returnFile.delete();
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error uploading file to Google Storage", ioe);
        }
        return true;

    }

    /**
     * hook for overriding name used for resource in Cloud Files
     * @param name
     * @return
     */
    protected String buildResourceName(String name) {
        // Strip the starting slash to prevent empty directories in CloudFiles as well as required references by // in the
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        String baseDirectory = googleStorageConfigurationService.lookupGoogleStorageConfiguration().getContainerSubdirectory();
        if (StringUtils.isNotEmpty(baseDirectory)) {
            if (baseDirectory.startsWith("/")) {
                baseDirectory = baseDirectory.substring(1);
            }
        } else {
            // ensure subDirectory is non-null
            baseDirectory = "";
        }

        String siteSpecificResourceName = getSiteSpecificResourceName(name);
        return FilenameUtils.concat(baseDirectory, siteSpecificResourceName);
    }

    protected String getSiteSpecificResourceName(String resourceName) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            Site site = brc.getNonPersistentSite();
            if (site != null) {
                String siteDirectory = getSiteDirectory(site);
                if (resourceName.startsWith("/")) {
                    resourceName = resourceName.substring(1);
                }
                return FilenameUtils.concat(siteDirectory, resourceName);
            }
        }

        return resourceName;
    }

    protected String getSiteDirectory(Site site) {
        String siteDirectory = "site-" + site.getId();
        return siteDirectory;
    }

     public void setGoogleStorageConfigurationService(GoogleStorageConfigurationService googleStorageConfigurationService) {
        this.googleStorageConfigurationService = googleStorageConfigurationService;
    }

    public void setFileService(BroadleafFileService fileService) {
        this.fileService = fileService;
    }
}