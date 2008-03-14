// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.model.nodes;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.softwaredeployment.TdDataProvider;


/**
 * @author rli
 *
 */
public abstract class AbstractFolderNode implements IFolderNode {
    
    private String name;
    
    private EObject children;
    
    private EObject parent;
    
    private boolean isLoaded;

    private TdDataProvider dataProvider;
    
    public AbstractFolderNode(String name) {
        this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    
    /**
     * @return the children
     */
    public EObject getChildren() {
        return children;
    }

    
    /**
     * @param children the children to set
     */
    public void setChildren(EObject children) {
        this.children = children;
    }


    public void setParent(EObject parent) {
        this.parent = parent;
    }

    public EObject getParent() {
        return parent;
    }


    
    /**
     * @return the isLoaded
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    
    /**
     * @param isLoaded the isLoaded to set
     */
    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }
    
    public abstract void loadChildren();

    
    /**
     * @return the dataProvider
     */
    public TdDataProvider getDataProvider() {
        return dataProvider;
    }

    
    /**
     * @param dataProvider the dataProvider to set
     */
    public void setDataProvider(TdDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

}
