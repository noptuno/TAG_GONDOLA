package com.desarrollo.myapplication.apiadapter;

import com.desarrollo.myapplication.register.cd.CashDrawer;

/**
 * Created by elo on 14/9/17.
 */

public class CashDrawerAdapter1_0 implements CashDrawerAdapter {

    private CashDrawer cashDrawer;

    CashDrawerAdapter1_0() {
        this.cashDrawer = new CashDrawer();
    }

    @Override
    public boolean isCashDrawerOpen() {
        return cashDrawer.isDrawerOpen();
    }

    @Override
    public void openCashDrawer() {
        cashDrawer.openCashDrawer();
    }
}
