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
package org.talend.core.ui.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.core.i18n.Messages;
import org.talend.core.model.context.JobContext;
import org.talend.core.model.context.JobContextParameter;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.ui.images.ECoreImage;
import org.talend.repository.model.RepositoryConstants;

/**
 * A dialog that config the context value sets.
 * 
 */
public class ContextSetConfigurationDialog extends ObjectSelectionDialog<IContext> {

    private static String defaultMesage = "Configure Contexts for Job. Click to Set Default Context.         "; //$NON-NLS-1$

    IContextModelManager manager = null;

    /**
     * DOC bqian ContextSetConfigurationDialog class global comment. Detailled comment <br/>
     * 
     */
    public class ContextLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object object) {
            return ImageProvider.getImage(ECoreImage.CONTEXT_ICON);
        }

        @Override
        public String getText(Object object) {
            IContext context = (IContext) object;
            IContext defaultContext = manager.getContextManager().getDefaultContext();
            if (context.equals(defaultContext)) {
                return context.getName() + " (Default)"; //$NON-NLS-1$
            } else {
                return context.getName();
            }
        }
    }

    @SuppressWarnings("restriction")
    public ContextSetConfigurationDialog(Shell parentShell, IContextModelManager manager) {
        super(parentShell, "Configure Contexts", defaultMesage, null); //$NON-NLS-1$
        this.manager = manager;
        setLabelProvider(getLabelProvider());
        List<IContext> list = new ArrayList<IContext>(manager.getContextManager().getListContext());
        setData(list);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void initTableInput() {
        super.initTableInput();
        ((CheckboxTableViewer) fTableViewer).setCheckedElements(new Object[] { manager.getContextManager().getDefaultContext() });
        fTableViewer.refresh();
    }

    @Override
    protected void createTableViewer(Composite parent) {
        fTableViewer = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
        initTableViewer(parent);
        ((CheckboxTableViewer) fTableViewer).setCheckedElements(new Object[] { manager.getContextManager().getDefaultContext() });
        fTableViewer.refresh();
        ((CheckboxTableViewer) fTableViewer).addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                Object obj = event.getElement();
                boolean checked = event.getChecked();
                if (obj.equals(manager.getContextManager().getDefaultContext())) {
                    // keep check status
                    int index = getData().indexOf(obj);
                    fTableViewer.getTable().getItem(index).setChecked(true);
                } else {
                    int index = getData().indexOf(manager.getContextManager().getDefaultContext());
                    fTableViewer.getTable().getItem(index).setChecked(false);
                    manager.onContextChangeDefault(manager.getContextManager(), (IContext) obj);
                    fTableViewer.refresh(true);
                }
            }
        });
    }

    public List<IContext> getResultContexts() {
        return getData();
    }

    LabelProvider getLabelProvider() {
        return new ContextLabelProvider();
    }

    private String toValid(String newText) {
        if (newText.equals("") || !newText.matches(RepositoryConstants.CODE_ITEM_PATTERN)) {
            return "the name is not valid";
        }
        for (IContext context : getAllContexts()) {
            if (context.getName().equalsIgnoreCase(newText)) {
                return Messages.getString("ContextProcessSection.30");
            }
        }
        return null;
    }

    @Override
    public void createElement() {

        IInputValidator validator = new IInputValidator() {

            public String isValid(String newText) {
                return toValid(newText);
            }
        };

        InputDialog inputDial = new InputDialog(getShell(), Messages.getString("ContextProcessSection.6"), //$NON-NLS-1$
                Messages.getString("ContextProcessSection.7"), "", validator); //$NON-NLS-1$ //$NON-NLS-2$

        inputDial.open();
        String returnValue = inputDial.getValue();
        if (returnValue == null) {
            return;
        }
        createContext(returnValue);
        refreshViewer();
    }

    private boolean validateContextName(String name) {
        if (name.equals("") || !name.matches(RepositoryConstants.CODE_ITEM_PATTERN)) { //$NON-NLS-1$
            MessageDialog.openWarning(new Shell(), Messages.getString(Messages.getString("ContextProcessSection.50")), Messages //$NON-NLS-1$
                    .getString(Messages.getString("ContextProcessSection.51"))); //$NON-NLS-1$
            return false;
        }

        return !isContextExisting(name);
    }

    public List<IContext> getAllContexts() {
        return getData();
    }

    private boolean isContextExisting(String name) {
        for (IContext context : getAllContexts()) {
            if (context.getName().equalsIgnoreCase(name)) {
                MessageBox mBox = new MessageBox(this.getShell(), SWT.ICON_ERROR);
                mBox.setText(Messages.getString("ContextProcessSection.29")); //$NON-NLS-1$
                mBox.setMessage(Messages.getString("ContextProcessSection.30")); //$NON-NLS-1$
                mBox.open();
                return true;
            }
        }
        return false;
    }

    private void createContext(final String name) {
        IContext context = manager.getContextManager().getDefaultContext();

        JobContext newContext = new JobContext(name);

        List<IContextParameter> newParamList = new ArrayList<IContextParameter>();
        newContext.setContextParameterList(newParamList);
        JobContextParameter param;
        for (int i = 0; i < context.getContextParameterList().size(); i++) {
            param = (JobContextParameter) context.getContextParameterList().get(i).clone();
            param.setContext(newContext);
            newParamList.add(param);
        }
        getAllContexts().add(newContext);
    }

    @Override
    protected void editSelectedElement() {

        IInputValidator validator = new IInputValidator() {

            public String isValid(String newText) {
                return toValid(newText);
            }
        };

        IContext selectedContext = (IContext) (getSelection()).getFirstElement();
        String contextName = selectedContext.getName();
        InputDialog inputDial = new InputDialog(getShell(), Messages.getString("ContextProcessSection.12"), //$NON-NLS-1$
                Messages.getString("ContextProcessSection.13", contextName), "", validator); //$NON-NLS-1$ //$NON-NLS-2$
        inputDial.open();
        String returnValue = inputDial.getValue();
        if (returnValue == null) {
            return;
        }
        if (manager.getProcess() != null && manager.getProcess().getLastRunContext() != null
                && manager.getProcess().getLastRunContext().sameAs(selectedContext)) {
            manager.getProcess().setLastRunContext(selectedContext);
        }
        renameContext(selectedContext, returnValue);
        refreshViewer();
    }

    private void renameContext(IContext context, String newName) {
        context.setName(newName);
    }

    /**
     * Updates the modify buttons' enabled state based on the current seleciton.
     */
    @Override
    protected void updateButtonAvailability() {
        super.updateButtonAvailability();

        boolean selectDefaultContext = false;
        for (Iterator iterator = getSelection().iterator(); iterator.hasNext();) {
            IContext context = (IContext) iterator.next();
            if (context == manager.getContextManager().getDefaultContext()) {
                selectDefaultContext = true;
                break;
            }

        }
        if (selectDefaultContext) {
            fRemoveButton.setEnabled(false);
            String contextNname = manager.getContextManager().getDefaultContext().getName();
            setDisplayMessage(Messages.getString("ContextProcessSection.RemoveInformation", contextNname)); //$NON-NLS-1$
        } else {
            setDisplayMessage(defaultMesage);
        }

    }

    // /**
    // *
    // * DOC chuang ContextSetConfigurationDialog class global comment. Detailled comment
    // */
    // class MyCheckboxTableViewer extends CheckboxTableViewer {
    //
    // MyCheckboxTableViewer(Table table) {
    // super(table);
    // }
    //
    // /*
    // * (non-Javadoc) Method declared on StructuredViewer.
    // */
    // @Override
    // public void handleSelect(SelectionEvent event) {
    // super.handleSelect(event);
    // // if (event.detail == SWT.CHECK) {
    // // TableItem item = (TableItem) event.item;
    // // if (item.getChecked()) {
    // // return;
    // // } else {
    // // Table table = getTable();
    // // table.setRedraw(false);
    // // for (TableItem it : table.getItems()) {
    // // it.setChecked(false);
    // // }
    // // item.setChecked(true);
    // // for (IContext context : manager.getContextManager().getListContext()) {
    // // if (context.equals(item.getData())) {
    // // manager.onContextChangeDefault(manager.getContextManager(), context);
    // // }
    // // }
    // // // manager.getContextManager().setDefaultContext(null);
    // // table.setRedraw(true);
    // // refresh();
    // // }
    // // } else {
    // // super.handleSelect(event);
    // // }
    // }
    //
    // }
}
