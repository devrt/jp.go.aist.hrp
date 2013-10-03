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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.emf.common.util.URI;

public class DefaultModelDataProvider implements IModelDataProvider {
    
    @Override
    public String getModelDataURL() {
        FileDialog fdlg = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN);
        String[] fe = { "*.wrl" }; //$NON-NLS-1$
        fdlg.setFilterExtensions(fe);
        
        String fPath = fdlg.open();
        if( fPath != null ) {
            return URI.createFileURI(fPath).toString();
        }
        return null;
    }
    
    @Override
    public Image getIcon() {
        return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
    }
    
}
