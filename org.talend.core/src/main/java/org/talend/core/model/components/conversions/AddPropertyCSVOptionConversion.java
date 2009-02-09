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
package org.talend.core.model.components.conversions;

import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.components.ComponentUtilities;
import org.talend.designer.core.model.utils.emf.talendfile.ColumnType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40 +0000 (ven., 29 sept. 2006) nrousseau $
 * 
 */
public class AddPropertyCSVOptionConversion implements IComponentConversion {

	  private String field="CHECK"; //$NON-NLS-1$

	    private String name = "CSV_OPTION"; //$NON-NLS-1$

	    public AddPropertyCSVOptionConversion() {
	        super();
	    }

	    public void transform(NodeType node) {
	        ComponentUtilities.addNodeProperty(node, name, field);
	        ComponentUtilities.setNodeValue(node, name, "true"); //$NON-NLS-1$
	    }

}
