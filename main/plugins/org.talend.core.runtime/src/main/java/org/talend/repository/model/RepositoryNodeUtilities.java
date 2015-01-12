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
package org.talend.repository.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.gmf.util.DisplayUtils;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.hadoop.IHadoopClusterService;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.RoutineItem;
import org.talend.core.model.properties.SAPConnectionItem;
import org.talend.core.model.relationship.Relation;
import org.talend.core.model.relationship.RelationshipItemBuilder;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.utils.RepositoryManagerHelper;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.core.runtime.i18n.Messages;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.nodes.IProjectRepositoryNode;
import org.talend.repository.ui.views.IRepositoryView;

/**
 * Utility class to manage RepositoryNode.<br/>
 * 
 * $Id: RepositoryNodeUtilities.java 1368 2007-01-10 09:50:53Z smallet $
 * 
 */
public class RepositoryNodeUtilities {

    private final static String[] METADATA_LABELS = new String[] {};

    private static IHadoopClusterService hadoopClusterService = null;
    static {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IHadoopClusterService.class)) {
            hadoopClusterService = (IHadoopClusterService) GlobalServiceRegister.getDefault().getService(
                    IHadoopClusterService.class);
        }
    }

    public static IPath getPath(RepositoryNode node) {
        if (node == null) {
            return null;
        }
        if (node.isBin()) {
            return new Path(""); //$NON-NLS-1$
        }
        if ((node.getType() == ENodeType.STABLE_SYSTEM_FOLDER && node.getContentType() != ERepositoryObjectType.JOBS && node
                .getContentType() != ERepositoryObjectType.JOBLETS) || node.getType() == ENodeType.SYSTEM_FOLDER) {
            return new Path(""); //$NON-NLS-1$
        }
        if (node.getType() == ENodeType.SIMPLE_FOLDER) {
            String label = node.getObject().getProperty().getLabel();
            return getPath(node.getParent()).append(label);
        }

        String label = node.getLabel();
        // checks if node is under Documentations/Generatated/Jobs
        if (node.getType() == ENodeType.STABLE_SYSTEM_FOLDER
                && (node.getContentType() == ERepositoryObjectType.JOBS || node.getContentType() == ERepositoryObjectType.JOBLETS)) {

            Object nodeLabel = node.getProperties(EProperties.LABEL);

            if (nodeLabel == ERepositoryObjectType.JOBS) {
                return new Path(""); //$NON-NLS-1$
            } else {
                return getPath(node.getParent()).append(label);
            }
        } else {
            if (/* !isMetadataLabel(label) && */node.getType() != ENodeType.REPOSITORY_ELEMENT) {
                return getPath(node.getParent()).append(label);
            } else {
                return getPath(node.getParent());
            }
        }

    }

    public static IPath getFolderPath(EObject obj) {
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof FolderItem)) {
            return new Path("");
        }
        FolderItem folderItem = (FolderItem) obj;

        if (folderItem.getType().getValue() == FolderType.FOLDER) {
            String label = folderItem.getProperty().getLabel();
            return getFolderPath(folderItem.getParent()).append(label);
        }
        return new Path("");

    }

    public static IRepositoryView getRepositoryView() {
        return RepositoryManagerHelper.getRepositoryView();
    }

    // /**
    // * Gather all view's metadata children nodes dynamic and get their label.
    // * <p>
    // * DOC YeXiaowei Comment method "isMetadataLabel".
    // *
    // * @param label
    // * @return
    // */
    // private static boolean isMetadataLabel(final String label) {
    //
    // if (!PluginChecker.isOnlyTopLoaded() && !CoreRuntimePlugin.getInstance().isDataProfilePerspectiveSelected()) {
    // IRepositoryView view = getRepositoryView();
    // if (view == null) {
    // return false;
    // }
    //
    // String[] metadataLabels = view.gatherMetadataChildenLabels();
    // if (metadataLabels == null || metadataLabels.length <= 0) {
    // return false;
    // }
    //
    // for (String mlabel : metadataLabels) {
    // if (mlabel.equals(label)) {
    // return true;
    // }
    // }
    // }
    //
    // return false;
    //
    // }

    /**
     * 
     * ggu Comment method "getPath".
     * 
     * get path by repository item id. can't get the folders.
     */
    public static IPath getPath(final String id) {
        if (id == null || "".equals(id) || RepositoryNode.NO_ID.equals(id)) { //$NON-NLS-1$
            return null;
        }
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        try {
            final IRepositoryViewObject lastVersion = factory.getLastVersion(id);
            return getPath(lastVersion);
        } catch (PersistenceException e) {
            //
        }
        return null;
    }

    public static IPath getPath(IRepositoryViewObject curNode) {
        if (curNode == null) {
            return null;
        }
        final RepositoryNode repositoryNode = getRepositoryNode(curNode);
        if (repositoryNode != null) {
            return getPath(repositoryNode);
        }
        return null;
    }

    /**
     * 
     * ggu Comment method "getRepositoryNode".
     * 
     * @param id
     * @return the repository node by id
     * @see RepositorySeekerManager.searchRepoViewNode
     * @deprecated
     */
    @Deprecated
    public static RepositoryNode getRepositoryNode(final String id) {
        return getRepositoryNode(id, true);
    }

    /**
     * @see RepositorySeekerManager.searchRepoViewNode
     * @deprecated
     */
    @Deprecated
    public static RepositoryNode getRepositoryNode(final String id, boolean expanded) {
        if (id == null || "".equals(id) || RepositoryNode.NO_ID.equals(id)) { //$NON-NLS-1$
            return null;
        }
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        try {
            final IRepositoryViewObject lastVersion = factory.getLastVersion(id);
            if (lastVersion != null) {
                return getRepositoryNode(lastVersion, expanded);
            }
        } catch (PersistenceException e) {
            //
        }
        return null;
    }

    /**
     * 
     * ggu Comment method "getRepositoryNode".
     * 
     * get the repository node by a IRepositoryObject.
     */
    public static RepositoryNode getRepositoryNode(IRepositoryViewObject curNode) {
        return getRepositoryNode(curNode, true);
    }

    public static RepositoryNode getRepositoryNode(IRepositoryViewObject curNode, boolean expanded) {
        if (curNode == null) {
            return null;
        }
        IRepositoryView view = getRepositoryView();
        if (view == null) {
            return null;
        }
        return getRepositoryNode((IRepositoryNode) view.getRoot(), curNode, view, expanded);
    }

    private static IRepositoryNode viewRootNode;

    public static RepositoryNode getRepositoryNode(IRepositoryViewObject curNode, IProgressMonitor monitor) {
        if (curNode == null) {
            return null;
        }
        viewRootNode = null;
        DisplayUtils.getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                IRepositoryView view = getRepositoryView();
                if (view != null) {
                    viewRootNode = (IRepositoryNode) view.getRoot();
                }
            }
        });
        if (viewRootNode == null) {
            return null;
        }
        RepositoryNode toReturn = getRepositoryNode(viewRootNode, curNode, monitor);

        // try to find the node from the root type of node.
        if (toReturn == null && viewRootNode instanceof IProjectRepositoryNode) {
            IRepositoryNode typeNode = ((IProjectRepositoryNode) viewRootNode).getRootRepositoryNode(curNode
                    .getRepositoryObjectType());
            toReturn = getRepositoryNode(typeNode, curNode, monitor);
        }
        viewRootNode = null;

        return toReturn;
    }

    private static RepositoryNode getRepositoryNode(IRepositoryNode rootNode, IRepositoryViewObject curNode,
            IProgressMonitor monitor) {
        if (rootNode == null || curNode == null) {
            return null;
        }
        if (monitor != null && monitor.isCanceled()) {
            return null;
        }

        List<IRepositoryNode> children = rootNode.getChildren();

        if (children != null) {
            if (children.isEmpty()) {
                rootNode.getRoot().initNode(rootNode);
                children = rootNode.getChildren();
            }
            // in the first, search the current folder
            List<IRepositoryNode> folderChild = new ArrayList<IRepositoryNode>();

            for (IRepositoryNode childNode : children) {
                RepositoryNode node = (RepositoryNode) childNode;
                if (isRepositoryFolder(node) || node.getType() == ENodeType.REFERENCED_PROJECT) {
                    folderChild.add(node);
                } else if (hadoopClusterService != null && hadoopClusterService.isHadoopClusterNode(node)) {
                    if (node.getId().equals(curNode.getId()) && node.getObjectType() == curNode.getRepositoryObjectType()) {
                        return node;
                    } else {
                        folderChild.add(node);
                    }
                } else if (node.getId().equals(curNode.getId()) && node.getObjectType() == curNode.getRepositoryObjectType()) {
                    return node;
                }

            }
            for (IRepositoryNode folderNode : folderChild) {
                final RepositoryNode repositoryNode = getRepositoryNode(folderNode, curNode, monitor);
                if (repositoryNode != null) {
                    return repositoryNode;
                }
            }
        }

        return null;
    }

    private static RepositoryNode getRepositoryNode(IRepositoryNode rootNode, IRepositoryViewObject curNode,
            IRepositoryView view, boolean expanded) {
        if (rootNode == null || curNode == null || view == null) {
            return null;
        }
        if (expanded) {
            // expande the unvisible node
            expandNode((RepositoryNode) rootNode, curNode, view);
        }

        final List<IRepositoryNode> children = rootNode.getChildren();

        if (children != null) {
            // in the first, search the current folder
            List<IRepositoryNode> folderChild = new ArrayList<IRepositoryNode>();

            for (IRepositoryNode childNode : children) {
                RepositoryNode node = (RepositoryNode) childNode;
                if (isRepositoryFolder(node) || node.getType() == ENodeType.REFERENCED_PROJECT) {
                    folderChild.add(node);
                } else if (isHadoopClusterNode(node)) {
                    if (node.getId().equals(curNode.getId()) && node.getObjectType() == curNode.getRepositoryObjectType()) {
                        return node;
                    } else {
                        folderChild.add(node);
                    }
                } else if (node.getId().equals(curNode.getId()) && node.getObjectType() == curNode.getRepositoryObjectType()) {
                    return node;
                }

            }
            for (IRepositoryNode folderNode : folderChild) {
                final RepositoryNode repositoryNode = getRepositoryNode(folderNode, curNode, view, expanded);
                if (repositoryNode != null) {
                    return repositoryNode;
                }
            }
        }

        return null;
    }

    private static boolean isHadoopClusterNode(IRepositoryNode node) {
        return hadoopClusterService != null && hadoopClusterService.isHadoopClusterNode(node);
    }

    public static void expandNode(IRepositoryView view, RepositoryNode curNode, Set<RepositoryNode> nodes) {
        getRepositoryCheckedNode((IRepositoryNode) view.getRoot(), curNode.getObject(), view, true, nodes);
    }

    private static RepositoryNode getRepositoryCheckedNode(IRepositoryNode rootNode, IRepositoryViewObject curNode,
            IRepositoryView view, boolean expanded, Set<RepositoryNode> nodes) {
        if (rootNode == null || curNode == null || view == null) {
            return null;
        }
        if (expanded) {
            // expande the unvisible node
            expandNode((RepositoryNode) rootNode, curNode, view);
        }
        final List<IRepositoryNode> children = rootNode.getChildren();

        if (children != null) {
            // in the first, search the current folder
            List<IRepositoryNode> folderChild = new ArrayList<IRepositoryNode>();

            for (IRepositoryNode childNode : children) {
                if (isRepositoryFolder(childNode) || childNode.getType() == ENodeType.REFERENCED_PROJECT) {
                    if (hasCheckedChild(childNode, nodes)) {
                        folderChild.add(childNode);
                    }

                } else if (childNode.getId().equals(curNode.getId())
                        && childNode.getObjectType() == curNode.getRepositoryObjectType()) {
                    return (RepositoryNode) childNode;
                }

            }
            for (IRepositoryNode folderNode : folderChild) {
                final RepositoryNode repositoryNode = getRepositoryCheckedNode(folderNode, curNode, view, expanded, nodes);
                if (repositoryNode != null) {
                    return repositoryNode;
                }
            }
        }

        return null;
    }

    private static boolean hasCheckedChild(IRepositoryNode fatherNode, Set<RepositoryNode> nodes) {
        if (!fatherNode.getChildren().isEmpty()) {
            for (IRepositoryNode node : fatherNode.getChildren()) {
                for (IRepositoryNode pnode : nodes) {
                    if (node.equals(pnode)) {
                        return true;
                    }
                }
                boolean flag = hasCheckedChild(node, nodes);
                if (flag) {
                    return true;
                }
            }
        } else {
            for (IRepositoryNode node : nodes) {
                if (node.equals(fatherNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void expandNode(RepositoryNode rootNode, IRepositoryViewObject curNode, IRepositoryView view) {
        if (rootNode == null || curNode == null || view == null) {
            return;
        }
        final ERepositoryObjectType rootContextType = rootNode.getContentType();
        final ERepositoryObjectType curType = curNode.getRepositoryObjectType();

        // for referenced project
        if (rootContextType == ERepositoryObjectType.REFERENCED_PROJECTS || rootNode.getType() == ENodeType.REFERENCED_PROJECT) {
            expandParentNode(view, rootNode);
        }
        if (rootContextType != null) {

            ERepositoryObjectType tmpType = null;
            if (curType == ERepositoryObjectType.METADATA_CON_TABLE || curType == ERepositoryObjectType.METADATA_CON_VIEW
                    || curType == ERepositoryObjectType.METADATA_CON_SYNONYM
                    || curType == ERepositoryObjectType.METADATA_CON_QUERY
                    || curType == ERepositoryObjectType.METADATA_CONNECTIONS
                    || curType == ERepositoryObjectType.METADATA_FILE_DELIMITED
                    || curType == ERepositoryObjectType.METADATA_FILE_POSITIONAL
                    || curType == ERepositoryObjectType.METADATA_FILE_REGEXP
                    || curType == ERepositoryObjectType.METADATA_FILE_XML || curType == ERepositoryObjectType.METADATA_FILE_LDIF
                    || curType == ERepositoryObjectType.METADATA_FILE_EXCEL
                    || curType == ERepositoryObjectType.METADATA_GENERIC_SCHEMA
                    || curType == ERepositoryObjectType.METADATA_LDAP_SCHEMA
                    || curType == ERepositoryObjectType.METADATA_SALESFORCE_SCHEMA
                    || curType == ERepositoryObjectType.METADATA_WSDL_SCHEMA
                    || curType == ERepositoryObjectType.METADATA_FILE_EBCDIC
                    || curType == ERepositoryObjectType.METADATA_FILE_HL7 || curType == ERepositoryObjectType.METADATA_FILE_FTP
                    || curType == ERepositoryObjectType.METADATA_FILE_BRMS
                    || curType == ERepositoryObjectType.METADATA_MDMCONNECTION
                    || curType == ERepositoryObjectType.METADATA_FILE_RULES
                    || curType == ERepositoryObjectType.METADATA_FILE_LINKRULES
                    || curType == ERepositoryObjectType.METADATA_SAPCONNECTIONS
                    || curType == ERepositoryObjectType.METADATA_HEADER_FOOTER) {
                tmpType = ERepositoryObjectType.METADATA;
            } else if (curType == ERepositoryObjectType.ROUTINES || curType == ERepositoryObjectType.SNIPPETS) {
                tmpType = ERepositoryObjectType.ROUTINES;
            } else if (curType == ERepositoryObjectType.DOCUMENTATION || curType == ERepositoryObjectType.JOB_DOC
                    || curType == ERepositoryObjectType.JOBLET_DOC) {
                tmpType = ERepositoryObjectType.DOCUMENTATION;
            }

            if (tmpType != null && tmpType == rootContextType) {
                expandParentNode(view, rootNode);
            }
            // expand the parent node

            if (curType == rootContextType && isRepositoryFolder(rootNode)) {
                if (rootContextType == ERepositoryObjectType.SQLPATTERNS
                        && !(rootNode.getParent() instanceof IProjectRepositoryNode)) {
                    // sql pattern
                } else {
                    expandParentNode(view, rootNode);
                    view.getViewer().refresh();
                }
            }

        }
    }

    public static void expandParentNode(IRepositoryView view, RepositoryNode node) {
        if (view == null || node == null) {
            return;
        }
        expandParentNode(view, node.getParent());
        view.expand(node, true);
        // for db
        StructuredViewer viewer = view.getViewer();
        if (viewer instanceof TreeViewer) {
            TreeViewer treeViewer = (TreeViewer) viewer;
            ERepositoryObjectType objectType = node.getObjectType();
            if (objectType != null) {
                if (objectType == ERepositoryObjectType.METADATA_CONNECTIONS) {
                    treeViewer.expandToLevel(node, TreeViewer.ALL_LEVELS);
                } else if (objectType == ERepositoryObjectType.ROUTINES) {
                    treeViewer.expandToLevel(node, 2);
                }
            }
        }

    }

    private static boolean isRepositoryFolder(IRepositoryNode node) {
        if (node == null) {
            return false;
        }
        final ENodeType type = node.getType();
        if (type == ENodeType.SIMPLE_FOLDER || type == ENodeType.STABLE_SYSTEM_FOLDER || type == ENodeType.SYSTEM_FOLDER) {
            return true;
        }
        return false;
    }

    public static RepositoryNode getMetadataTableFromConnection(String schemaValue) {
        String[] values = schemaValue.split(" - "); //$NON-NLS-1$
        String repositoryID = values[0];
        String tableName = values[1];

        try {
            final RepositoryNode realNode = getRepositoryNode(repositoryID);
            if (realNode.getObject() != null && realNode.getObject().getProperty() != null) {
                Item item = realNode.getObject().getProperty().getItem();
                if (item instanceof SAPConnectionItem) {
                    return getSAPSchemaFromConnection(realNode, schemaValue);
                } else {
                    return getSchemeFromConnection(realNode, tableName, ERepositoryObjectType.METADATA_CON_TABLE);
                }
            }

        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    private static RepositoryNode getSAPSchemaFromConnection(RepositoryNode realNode, String name) {
        String[] values = name.split(" - "); //$NON-NLS-1$
        if (values.length != 3) {
            return null;
        }
        String metadataName = values[2];
        String repositoryId = name.substring(0, name.lastIndexOf(" - ")); //$NON-NLS-1$
        RepositoryNode functionNode = getSAPFunctionFromConnection(repositoryId);
        for (IRepositoryNode node : functionNode.getChildren()) {
            if (metadataName.equals(node.getProperties(EProperties.LABEL))) {
                return (RepositoryNode) node;
            }
        }
        return null;
    }

    public static RepositoryNode getQueryFromConnection(String schemaValue) {
        String[] values = schemaValue.split(" - "); //$NON-NLS-1$
        String repositoryID = values[0];
        String tableName = values[1];

        try {
            final RepositoryNode realNode = getRepositoryNode(repositoryID);
            return getSchemeFromConnection(realNode, tableName, ERepositoryObjectType.METADATA_CON_QUERY);
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    public static RepositoryNode getSAPFunctionFromConnection(String id) {
        String[] values = id.split(" - "); //$NON-NLS-1$
        String repositoryID = values[0];
        String functionName = values[1];

        try {
            final RepositoryNode realNode = getRepositoryNode(repositoryID);
            if (realNode.getObject() != null) {
                if (ERepositoryObjectType.METADATA_SAPCONNECTIONS.equals(realNode.getObject().getRepositoryObjectType())) {
                    for (IRepositoryNode node : realNode.getChildren()) {
                        if (Messages.getString("RepositoryContentProvider.repositoryLabel.sapFunction").equals(node.getLabel())) { //$NON-NLS-1$
                            for (IRepositoryNode function : node.getChildren()) {
                                if (functionName.equals(function.getProperties(EProperties.LABEL))) {
                                    return (RepositoryNode) function;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    private static RepositoryNode getSchemeFromConnection(RepositoryNode connection, String tableName,
            ERepositoryObjectType repType) {
        ERepositoryObjectType type = connection.getObject().getRepositoryObjectType();
        if (repType == ERepositoryObjectType.METADATA_CON_QUERY) {
            for (IRepositoryNode node : connection.getChildren()) {
                if (Messages.getString("RepositoryContentProvider.repositoryLabel.Queries").equals(node.getLabel())) { //$NON-NLS-1$
                    for (IRepositoryNode query : node.getChildren()) {
                        if (tableName.equals(query.getProperties(EProperties.LABEL))) {
                            return (RepositoryNode) query;
                        }
                    }
                }
            }
        } else {
            if (type == ERepositoryObjectType.METADATA_CONNECTIONS) {
                for (IRepositoryNode child : connection.getChildren()) {
                    if (Messages.getString("RepositoryContentProvider.repositoryLabel.Queries").equals(child.getLabel())) { //$NON-NLS-1$
                        continue;
                    }
                    for (IRepositoryNode node : child.getChildren()) {
                        if (tableName.equals(node.getProperties(EProperties.LABEL))) {
                            return (RepositoryNode) node;
                        }
                    }
                }
            } else {
                for (IRepositoryNode child : connection.getChildren()) {
                    if (tableName.equals(child.getProperties(EProperties.LABEL))) {
                        return (RepositoryNode) child;
                    }
                }

            }

        }
        return null;
    }

    /**
     * 
     * ggu Comment method "getParentRepositoryNodeFromSelection".
     * 
     */
    public static RepositoryNode getParentRepositoryNodeFromSelection(IRepositoryViewObject object) {
        if (object.getRepositoryNode() != null && ((RepositoryNode) object.getRepositoryNode()).getParent() != null) {
            return ((RepositoryNode) object.getRepositoryNode()).getParent();
        }

        // "old" code bellow should never be called, unless the repository object is a new created and not from the
        // repository.

        IRepositoryView viewPart = getRepositoryView();
        if (viewPart != null) {
            ISelection repositoryViewSelection = viewPart.getViewer().getSelection();

            if (repositoryViewSelection instanceof IStructuredSelection) {
                RepositoryNode selectedRepositoryNode = (RepositoryNode) ((IStructuredSelection) repositoryViewSelection)
                        .getFirstElement();
                // fixed for the opened job and lost the selected node.
                if (object != null) {

                    selectedRepositoryNode = getRepositoryNode(object, false);

                }
                if (selectedRepositoryNode != null) {
                    return selectedRepositoryNode.getParent();
                }
            }
        }
        return null;

    }

    public static void checkItemDependencies(Item item, List<IRepositoryViewObject> repositoryObjects) {
        checkItemDependencies(item, repositoryObjects, true, true);
    }

    public static void checkItemDependencies(Item item, List<IRepositoryViewObject> repositoryObjects,
            boolean includeSystemItems, boolean includeReferenceProjectItems) {
        if (item == null) {
            return;
        }
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        RelationshipItemBuilder builder = RelationshipItemBuilder.getInstance();
        List<Relation> relations = builder.getItemsRelatedTo(item.getProperty().getId(), item.getProperty().getVersion(),
                RelationshipItemBuilder.JOB_RELATION);
        relations.addAll(builder.getItemsRelatedTo(item.getProperty().getId(), item.getProperty().getVersion(),
                RelationshipItemBuilder.JOBLET_RELATION));
        try {
            for (Relation relation : relations) {
                IRepositoryViewObject obj = null;
                String id = relation.getId();
                if (RelationshipItemBuilder.ROUTINE_RELATION.equals(relation.getType())) {
                    // TDI-20915
                    Project mainProject = ProjectManager.getInstance().getCurrentProject();
                    obj = getRoutineFromName(mainProject, id, includeSystemItems);
                    if (obj == null) {
                        List<Project> refProjects = ProjectManager.getInstance().getReferencedProjects(mainProject);
                        for (Project refPro : refProjects) {
                            obj = getRoutineFromName(refPro, id, includeSystemItems);
                            if (obj != null) {
                                break;
                            }
                        }
                    }
                } else {
                    if (id != null && id.indexOf(" - ") != -1) { //$NON-NLS-1$
                        id = id.substring(0, id.lastIndexOf(" - ")); //$NON-NLS-1$
                    }
                    if (includeReferenceProjectItems) {
                        obj = factory.getLastVersion(id);
                    } else {
                        obj = factory.getLastVersion(ProjectManager.getInstance().getCurrentProject(), id);
                    }
                }
                checkItemDependencies(obj, repositoryObjects, includeSystemItems, includeReferenceProjectItems);
            }

            // fix for TDI-19548 , and should be removed after implement add connection and context relationship to
            // RelationshipItemBuilder
            if (item instanceof ConnectionItem) {
                ConnectionItem connectionItem = (ConnectionItem) item;
                if (connectionItem.getConnection().isContextMode()) {
                    String id = connectionItem.getConnection().getContextId();
                    if (id != null) {
                        IRepositoryViewObject object = null;
                        if (includeReferenceProjectItems) {
                            object = factory.getLastVersion(id);
                        } else {
                            object = factory.getLastVersion(ProjectManager.getInstance().getCurrentProject(), id);
                        }
                        checkItemDependencies(object, repositoryObjects, includeSystemItems, includeReferenceProjectItems);
                    }
                }
            }
        } catch (PersistenceException et) {
            ExceptionHandler.process(et);
        }

    }

    private static void checkItemDependencies(IRepositoryViewObject obj, List<IRepositoryViewObject> repositoryObjects,
            boolean includeSystemItems, boolean includeReferenceProjectItems) {
        if (obj != null && !repositoryObjects.contains(obj)) {
            repositoryObjects.add(obj);
            checkAllVersionLatest(repositoryObjects, obj, includeSystemItems, includeReferenceProjectItems);
            checkItemDependencies(obj.getProperty().getItem(), repositoryObjects, includeSystemItems,
                    includeReferenceProjectItems);
        }
    }

    private static void checkAllVersionLatest(List<IRepositoryViewObject> repositoryObjects, IRepositoryViewObject object,
            boolean includeSystemItems, boolean includeReferenceProjectItems) {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        RelationshipItemBuilder builder = RelationshipItemBuilder.getInstance();
        if (object.getRepositoryNode() != null) {
            List<Relation> relations = builder.getItemsJobRelatedTo(object.getId(), object.getVersion(),
                    RelationshipItemBuilder.JOB_RELATION);
            for (Relation relation : relations) {
                try {
                    IRepositoryViewObject obj = null;
                    if (includeReferenceProjectItems) {
                        obj = factory.getLastVersion(relation.getId());
                    } else {
                        obj = factory.getLastVersion(ProjectManager.getInstance().getCurrentProject(), relation.getId());
                    }
                    if (obj != null && !repositoryObjects.contains(obj)) {
                        repositoryObjects.add(obj);
                        checkAllVersionLatest(repositoryObjects, obj, includeSystemItems, includeReferenceProjectItems);
                    }
                } catch (PersistenceException et) {
                    ExceptionHandler.process(et);
                }
            }
        }
    }

    public static IRepositoryViewObject getRoutineFromName(Project tempProject, String name, boolean includeSystem) {
        if (name == null) {
            return null;
        }

        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        try {
            List<IRepositoryViewObject> all = factory.getAll(tempProject, ERepositoryObjectType.ROUTINES);
            if (PluginChecker.isPigudfPluginLoaded()) {
                all.addAll(factory.getAll(tempProject, ERepositoryObjectType.PIG_UDF));
            }
            for (IRepositoryViewObject obj : all) {
                if (obj != null && obj.getProperty() != null) {
                    Item item = obj.getProperty().getItem();
                    String label = obj.getProperty().getLabel();
                    if (item != null && item instanceof RoutineItem) {
                        RoutineItem routineItem = (RoutineItem) item;
                        if (name.equals(label)) {
                            if (!includeSystem && routineItem.isBuiltIn()) {
                                continue;
                            } else {
                                return obj;
                            }
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }

        return null;
    }
}
