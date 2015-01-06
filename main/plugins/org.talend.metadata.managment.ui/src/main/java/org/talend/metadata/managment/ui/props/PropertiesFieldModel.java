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
package org.talend.metadata.managment.ui.props;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.commons.ui.swt.extended.table.ExtendedTableModel;

/**
 * 
 * created by ycbai on 2015年1月4日 Detailled comment
 *
 */
public class PropertiesFieldModel extends ExtendedTableModel<Map<String, Object>> {

    public PropertiesFieldModel(String name) {
        super(name);
        setProperties(new ArrayList<Map<String, Object>>());
    }

    public PropertiesFieldModel(List<Map<String, Object>> propertiesTypeList, String name) {
        super(name);
        setProperties(propertiesTypeList);
    }

    public void setProperties(List<Map<String, Object>> properties) {
        registerDataList(properties);
    }

    public Map<String, Object> createHadoopPropertiesType() {
        return new HashMap<String, Object>();
    }
}
