// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.localprovider.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IPath;

/**
 */
public class ZipFileManager extends ResourcesManager {

    private ZipFile zipFile;

    public ZipFileManager(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    public InputStream getStream(IPath path) throws IOException {
        return zipFile.getInputStream((ZipEntry) path2Object.get(path));
    }

    public boolean collectPath2Object(Object root) {
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            add(entry.getName(), entry);
        }
        return true;
    }
}
