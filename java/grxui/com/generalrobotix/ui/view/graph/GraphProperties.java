/*
 * Copyright (c) 2008, AIST, the University of Tokyo and General Robotix Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * General Robotix Inc.
 * National Institute of Advanced Industrial Science and Technology (AIST) 
 */
package com.generalrobotix.ui.view.graph;

import java.util.*;
import java.io.*;
import java.net.URL;

/**
 * ����եץ�ѥƥ�
 *
 * @author Kernel Inc.
 * @version 1.0 (2001/8/20)
 */
public class GraphProperties {

    // -----------------------------------------------------------------
    // ���
    private static final String GRAPH_PROPERTIES = "/resources/graph.properties";
    private static final String SEP = ".";
    private static final String DATA_KIND_NAMES = "dataKindNames";
    private static final String UNIT = "unit";
    private static final String BASE = "base";
    private static final String EXTENT = "extent";
    private static final String FACTOR = "factor";
    private static final String DATA_KIND = "dataKind";

    // -----------------------------------------------------------------
    // ���饹�ѿ�
    private static GraphProperties this_;   // ���󥰥�ȥ��ѥ��֥������ȥۥ��
    private static HashMap<String, DataKind> dataKindMap_;    // �ǡ�������̾�ȥǡ������̤��б�ɽ
    private static HashMap<String, DataKind> attributeMap_;   // ���ȥ�ӥ塼�Ȥȥǡ������̤��б�ɽ

    // -----------------------------------------------------------------
    // ���󥹥ȥ饯��
    /**
     * ���󥹥ȥ饯��
     *   �����
     *
     */
    private GraphProperties() {
        // �ץ�ѥƥ��ե�������ɤ߹���
        URL url = this.getClass().getResource(GRAPH_PROPERTIES);
        Properties prop = new Properties();
        try {
            prop.load(url.openStream());    // �ץ�ѥƥ��ե������ɤ߹���
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }

        // �ǡ������̤��ɤ߹���
        dataKindMap_ = new HashMap<String, DataKind>();
        StringTokenizer dkNames =
            new StringTokenizer(prop.getProperty(DATA_KIND_NAMES), ",");
        while (dkNames.hasMoreTokens()) {
            String dkName = dkNames.nextToken();
            String unit = prop.getProperty(dkName + SEP + UNIT);
            double base = Double.parseDouble(prop.getProperty(dkName + SEP + BASE));
            double extent = Double.parseDouble(prop.getProperty(dkName + SEP + EXTENT));
            double factor = (
                (prop.containsKey(dkName + SEP + FACTOR))
                ? Double.parseDouble((String)prop.getProperty(dkName + SEP + FACTOR))
                : 1
            );
            DataKind dk = new DataKind(dkName, unit, base, extent, factor);
            dataKindMap_.put(dkName, dk);
        }

        // ���ȥ�ӥ塼����Υǡ������̤��ɤ߹���
        attributeMap_ = new HashMap<String, DataKind>();
        String postfix = SEP + DATA_KIND;
        int postfixlen = postfix.length();
        Enumeration elm = prop.propertyNames();
        while (elm.hasMoreElements()) {
            String pname = (String)elm.nextElement();
            if (pname.endsWith(postfix)) {
                String aname = pname.substring(
                    0, pname.length() - postfixlen
                );
                attributeMap_.put(aname, dataKindMap_.get(prop.getProperty(pname)));
            }
        }
    }

    // -----------------------------------------------------------------
    // ���饹�᥽�å�
    /**
     * �ǡ�������̾����ǡ������̼���
     *
     * @param   dataKindName    �ǡ�������̾
     */
    public static DataKind getDataKindFromName(
        String dataKindName
    ) {
        if (this_ == null) {
            this_ = new GraphProperties();
        }
        return (DataKind)dataKindMap_.get(dataKindName);
    }

    /**
     * ���ȥ�ӥ塼��̾����ǡ������̼���
     *
     * @param   attribute   ���ȥ�ӥ塼��̾("Joint.angle"�Τ褦�ʷ����ǻ���)
     */
    public static DataKind getDataKindFromAttr(
        String attribute
    ) {
        if (this_ == null) {
            this_ = new GraphProperties();
        }
        return (DataKind)attributeMap_.get(attribute);
    }
}
