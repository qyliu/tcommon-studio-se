// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.core.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.IService;
import org.talend.core.model.general.Project;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.properties.tab.IDynamicProperty;

/**
 * nma class global comment. Detailled comment
 */
public interface ISVNProviderService extends IService {

    public boolean isProjectInSvnMode();

    public IDynamicProperty createProcessSVNHistoryComposite(Composite parent, TabbedPropertySheetWidgetFactory factory,
            IRepositoryViewObject obj);

    public boolean isSVNHistoryComposite(IDynamicProperty dp);

    public ISelection getSVNHistorySelection(IDynamicProperty dp);

    public String getLastSVNRevision(Object process);

    public String getCurrentSVNRevision(Object process);

    public String[] getBranchList(Project project);

    public boolean isSVNProject(Project p) throws PersistenceException;

    public void svnEclipseHandlerDelete(IProject eclipseProject, Project currentProject, String filePath);

}