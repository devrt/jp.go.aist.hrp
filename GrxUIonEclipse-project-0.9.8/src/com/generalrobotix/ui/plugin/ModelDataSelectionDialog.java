/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * General Robotix Inc.
 * National Institute of Advanced Industrial Science and Technology (AIST) 
 * MID Academic Promotions Inc.
 */
package com.generalrobotix.ui.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.generalrobotix.ui.util.MessageBundle;

public class ModelDataSelectionDialog {
    
    public String open() {
        IConfigurationElement elements[] = Platform.getExtensionRegistry().getConfigurationElementsFor("com.generalrobotics.ui.grxui.modeldataprovider"); //$NON-NLS-1$
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new ModelDataProviderLabelProvider());
        dialog.setSize(20, 5);
        dialog.setTitle(MessageBundle.getString("ModelDataSelectionDialog_1")); //$NON-NLS-1$
        dialog.setElements(elements);
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length > 0) {
                IModelDataProvider mp;
                try {
                    mp = (IModelDataProvider)((IConfigurationElement)result[0]).createExecutableExtension("provider"); //$NON-NLS-1$
                    String url = mp.getModelDataURL();
                    if (url != null) {
                        return url;
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private class ModelDataProviderLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((IConfigurationElement)element).getAttribute("description"); //$NON-NLS-1$
        }
        
        public Image getImage(Object element) {
            IModelDataProvider mp;
            try {
                mp = (IModelDataProvider)((IConfigurationElement)element).createExecutableExtension("provider"); //$NON-NLS-1$
                return mp.getIcon();
            } catch (CoreException e) {
                e.printStackTrace();
            }
            return null;
        }
        
    }
}
