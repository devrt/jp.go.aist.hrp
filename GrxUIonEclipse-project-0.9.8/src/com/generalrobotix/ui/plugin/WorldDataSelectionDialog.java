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

import java.io.File;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.generalrobotix.ui.grxui.GrxUIPerspectiveFactory;
import com.generalrobotix.ui.util.GrxDebugUtil;
import com.generalrobotix.ui.util.MessageBundle;

public class WorldDataSelectionDialog {
    public String open() {
        IConfigurationElement elements[] = Platform.getExtensionRegistry().getConfigurationElementsFor("com.generalrobotics.ui.grxui.worlddataprovider"); //$NON-NLS-1$
        GrxDebugUtil.print(((Integer)(elements.length)).toString());
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(GrxUIPerspectiveFactory.getCurrentShell(), new WorldDataProviderLabelProvider());
        dialog.setTitle(MessageBundle.getString("WorldDataSelectionDialog.1")); //$NON-NLS-1$
        dialog.setElements(elements);
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length > 0) {
                IWorldDataProvider mp;
                try {
                    mp = (IWorldDataProvider)((IConfigurationElement)result[0]).createExecutableExtension("provider"); //$NON-NLS-1$
                    String fPath = mp.getWorldDataURL();
                    if (fPath != null) {
                        org.eclipse.emf.common.util.URI u = org.eclipse.emf.common.util.URI.createURI(fPath);
                        File f = new File(CommonPlugin.resolve(u).toFileString());
                        return f.getAbsolutePath();
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private class WorldDataProviderLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((IConfigurationElement)element).getAttribute("description"); //$NON-NLS-1$
        }
        
        public Image getImage(Object element) {
            IWorldDataProvider mp;
            try {
                mp = (IWorldDataProvider)((IConfigurationElement)element).createExecutableExtension("provider"); //$NON-NLS-1$
                return mp.getIcon();
            } catch (CoreException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
}
