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
package org.talend.core.model.components;

import java.util.List;

import org.talend.core.model.metadata.ColumnNameChanged;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.MetadataTool;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class IODataComponent {

    private int columnOption = IMetadataColumn.OPTIONS_NONE;

    private IConnection connection;

    private IMetadataTable newMetadataTable;

    private List<ColumnNameChanged> columnNameChanged = null;

    /**
     * DOC smallet IODataComponent constructor comment.
     * 
     * @param connection
     */
    public IODataComponent(IConnection connection) {
        super();
        this.connection = connection;
        this.newMetadataTable = connection.getMetadataTable().clone();
    }

    public IODataComponent(IConnection connection, IMetadataTable clonedTable) {
        super();
        this.connection = connection;
        this.newMetadataTable = clonedTable;
    }

    @Override
    public String toString() {
        return "Connection=[" + connection + "], Table=[" + newMetadataTable + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public String getName() {
        return connection.getName();
    }

    public String getUniqueName() {
        return connection.getUniqueName();
    }

    /**
     * Getter for connection.
     * 
     * @return the connection
     */
    public IConnection getConnection() {
        return this.connection;
    }

    public EConnectionType getConnectionType() {
        return connection.getLineStyle();
    }

    public IMetadataTable getTable() {
        return newMetadataTable;
    }

    public void setTable(IMetadataTable table) {
        this.newMetadataTable = table;
    }

    public INode getTarget() {
        return connection.getTarget();
    }

    public INode getSource() {
        return connection.getSource();
    }

    public boolean hasChanged() {
        return !newMetadataTable.sameMetadataAs(connection.getMetadataTable(), getColumnOption());
    }

    public List<ColumnNameChanged> getColumnNameChanged() {
        if (columnNameChanged == null) {
            columnNameChanged = MetadataTool.getColumnNameChanged(connection.getMetadataTable(), newMetadataTable);
        }
        return columnNameChanged;
    }

    private List<ColumnNameChanged> newMetadataColumns;

    public List<ColumnNameChanged> getNewMetadataColumns() {
        if (newMetadataColumns == null) {
            newMetadataColumns = MetadataTool.getNewMetadataColumns(connection.getMetadataTable(), newMetadataTable);
        }
        return newMetadataColumns;
    }

    private List<ColumnNameChanged> removeMetadataColumns;

    public List<ColumnNameChanged> getRemoveMetadataColumns() {
        if (removeMetadataColumns == null) {
            removeMetadataColumns = MetadataTool.getRemoveMetadataColumns(connection.getMetadataTable(),
                    newMetadataTable);
        }
        return removeMetadataColumns;
    }

    public void setColumnNameChanged(List<ColumnNameChanged> columnNameChanged) {
        this.columnNameChanged = columnNameChanged;
    }

    /**
     * Getter for columnOption.
     * 
     * @return the columnOption
     */
    public int getColumnOption() {
        return this.columnOption;
    }

    /**
     * Sets the columnOption.
     * 
     * @param columnOption the columnOption to set
     */
    public void setColumnOption(int columnOption) {
        this.columnOption = columnOption;
    }
}
