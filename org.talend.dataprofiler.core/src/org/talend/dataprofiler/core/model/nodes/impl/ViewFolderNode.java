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
package org.talend.dataprofiler.core.model.nodes.impl;

import java.util.List;

import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.NeedSaveDataProviderHelper;
import org.talend.dataprofiler.core.model.nodes.AbstractFolderNode;


/**
 * @author rli
 *
 */
public class ViewFolderNode extends AbstractFolderNode {

    /**
     * @param name
     */
    public ViewFolderNode() {
        super("Views");
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.model.nodes.AbstractFolderNode#loadChildren()
     */
    @Override
    public void loadChildren() {
        TdCatalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(this.getParent());
        if (catalog != null) {
            List<TdView> viewList = CatalogHelper.getViews(catalog);

            if (viewList.size() > 0) {
                this.setLoaded(true);
                return;
            }

            TdDataProvider provider = DataProviderHelper.getTdDataProvider(catalog);

            List<TdView> views = DqRepositoryViewService.getViews(provider, catalog, null, true);
            // store views in catalog
            CatalogHelper.addViews(views, catalog);
            NeedSaveDataProviderHelper.register(provider.getName(), provider);
            this.setLoaded(true);
        }
    }

}
