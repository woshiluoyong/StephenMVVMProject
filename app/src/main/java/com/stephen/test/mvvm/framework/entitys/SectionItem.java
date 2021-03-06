package com.stephen.test.mvvm.framework.entitys;

import com.stephen.test.mvvm.framework.beans.MainAccInfoBean;
import com.stephen.test.mvvm.framework.qmui.QMUISection;

public class SectionItem implements QMUISection.Model<SectionItem> {
    private MainAccInfoBean.Data.Node mainBeanData;

    public SectionItem(MainAccInfoBean.Data.Node mainBeanData){
        this.mainBeanData = mainBeanData;
    }

    public MainAccInfoBean.Data.Node getMainBeanData() {
        return mainBeanData;
    }

    @Override
    public SectionItem cloneForDiff() {
        return new SectionItem(getMainBeanData());
    }

    @Override
    public boolean isSameItem(SectionItem other) {
        return (null != other && null != other.getMainBeanData() && null != mainBeanData && other.getMainBeanData().getBelong_country().equals(mainBeanData.getBelong_country())
                && other.getMainBeanData().getIp().equals(mainBeanData.getIp()));
    }

    @Override
    public boolean isSameContent(SectionItem other) {
        return true;
    }
}
