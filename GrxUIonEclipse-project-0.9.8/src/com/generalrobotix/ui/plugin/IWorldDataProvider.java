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

import org.eclipse.swt.graphics.Image;

public interface IWorldDataProvider {
    public String getWorldDataURL();
    public Image getIcon();
}
