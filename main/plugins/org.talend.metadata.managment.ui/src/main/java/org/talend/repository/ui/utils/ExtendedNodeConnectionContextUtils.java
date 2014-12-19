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
package org.talend.repository.ui.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.properties.ContextItem;
import org.talend.core.ui.context.model.table.ConectionAdaptContextVariableModel;
import org.talend.repository.model.IConnParamName;

/**
 * created by ldong on Dec 18, 2014 Detailled comment
 * 
 */
public class ExtendedNodeConnectionContextUtils {

    public enum ENoSQLParamName implements IConnParamName {
        Server,
        Port,
        Keyspace,
        Database,
        Databasepath,
        UserName,
        Password,
    }

    static List<IContextParameter> getContextVariables(final String prefixName, Connection conn, Set<IConnParamName> paramSet) {
        List<IContextParameter> varList = new ArrayList<IContextParameter>();
        for (IRepositoryContextHandler handler : RepositoryContextManager.getHandlers()) {
            if (handler.isRepositoryConType(conn)) {
                varList = handler.createContextParameters(prefixName, conn, paramSet);
            }
        }
        return varList;
    }

    static void setConnectionPropertiesForContextMode(String prefixName, Connection conn, Set<IConnParamName> paramSet) {
        if (conn == null || prefixName == null) {
            return;
        }
        for (IRepositoryContextHandler handler : RepositoryContextManager.getHandlers()) {
            if (handler.isRepositoryConType(conn)) {
                handler.setPropertiesForContextMode(prefixName, conn, paramSet);
            }
        }
    }

    static void setConnectionPropertiesForExistContextMode(Connection conn, Set<IConnParamName> paramSet,
            Map<ContextItem, List<ConectionAdaptContextVariableModel>> adaptMap) {
        if (conn == null) {
            return;
        }
        for (IRepositoryContextHandler handler : RepositoryContextManager.getHandlers()) {
            if (handler.isRepositoryConType(conn)) {
                handler.setPropertiesForExistContextMode(conn, paramSet, adaptMap);
            }
        }
    }
}
