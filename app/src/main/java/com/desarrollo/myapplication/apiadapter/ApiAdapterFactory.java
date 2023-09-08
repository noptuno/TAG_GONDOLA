package com.desarrollo.myapplication.apiadapter;

import android.content.Context;

import com.elo.device.DeviceManager;
import com.elo.device.enums.EloPlatform;
import com.elo.device.exceptions.UnsupportedEloPlatform;
import com.elo.device.inventory.Inventory;

/**
 * Created by elo on 9/8/17.
 */

public class ApiAdapterFactory {

    private static ApiAdapterFactory instance;      // Singleton instance

    Context context;

    // Cache the adapter we create, so we can return the same object if asked for it again.
    // This may happen if the Activity's onCreate() gets called again due to a configuration
    // change.  Adapter may have some state, so allow it to retain that state.
    ApiAdapterImpl adapterPaypoint_1_0;
    ApiAdapterImpl adapterPaypoint_refresh;
    ApiAdapterImpl adapterPaypoint_2_0;
    ApiAdapterImpl adapterISeries;

    private ApiAdapterFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ApiAdapterFactory getInstance(Context context) {
        synchronized (ApiAdapterFactory.class) {
            if (instance == null) {
                instance = new ApiAdapterFactory(context);
            }
        }
        return instance;
    }

    public ApiAdapter getApiAdapter(Inventory inventory) {
        EloPlatform platform = inventory.getProductInfo().eloPlatform;

        try {
            switch (platform) {
            case PAYPOINT_1:
                if (adapterPaypoint_1_0 == null) {
                    adapterPaypoint_1_0 = new ApiAdapterImpl();
                    initApiAdapter1_0(adapterPaypoint_1_0);
                }
                return adapterPaypoint_1_0;
            
            case PAYPOINT_REFRESH:
                if (adapterPaypoint_refresh == null) {
                    adapterPaypoint_refresh = new ApiAdapterImpl();
                    initApiAdapterRefresh(adapterPaypoint_refresh, inventory);
                }
                return adapterPaypoint_refresh;
            
            case PAYPOINT_2:
                if (adapterPaypoint_2_0 == null) {
                    adapterPaypoint_2_0 = new ApiAdapterImpl();
                    initApiAdapter2_0(adapterPaypoint_2_0);
                }
                return adapterPaypoint_2_0;
            
            case I_SERIES_1:
            case I_SERIES_2:
            case PUCK:
                if (adapterISeries == null) {
                    adapterISeries = new ApiAdapterImpl();
                    initApiAdapterISeries(adapterISeries, inventory);
                }
                return adapterISeries;
            
            default:
                return null;
            }
        } catch (UnsupportedEloPlatform e) {
            return null;
        }
    }

    private void initApiAdapter2_0(ApiAdapterImpl apiAdapterImpl) throws UnsupportedEloPlatform {
        DeviceManager deviceManager = DeviceManager.getInstance(EloPlatform.PAYPOINT_2, context);
        CommonUtil2_0 commonUtil = new CommonUtil2_0(context);
        ActivityMonitor activityMonitor = new ActivityMonitor();

        apiAdapterImpl.setActivityMonitor(activityMonitor);
        apiAdapterImpl.setCashDrawerAdapter(new CashDrawerAdapter2_0(deviceManager, commonUtil));
        apiAdapterImpl.setPrinterAdapter(new PrinterAdapter2_0(deviceManager, commonUtil, activityMonitor));
        apiAdapterImpl.setCfdAdapter(new CfdAdapterRefreshOr2_0(deviceManager));
        apiAdapterImpl.setBarCodeReaderAdapter(new BarCodeReaderAdapter2_0(deviceManager, commonUtil, activityMonitor));
        apiAdapterImpl.setFtdiAdapter(new FtdiAdapterCommon(deviceManager));
        apiAdapterImpl.setMSRAdapter(new MsrAdapterMagtek(context));
    }

    private void initApiAdapterRefresh(ApiAdapterImpl apiAdapterImpl, Inventory inventory) throws UnsupportedEloPlatform {
        DeviceManager deviceManager = DeviceManager.getInstance(EloPlatform.PAYPOINT_REFRESH, context);
        ActivityMonitor activityMonitor = new ActivityMonitor();

        apiAdapterImpl.setActivityMonitor(activityMonitor);
        apiAdapterImpl.setCashDrawerAdapter(new CashDrawerAdapterRefresh(deviceManager));
        apiAdapterImpl.setPrinterAdapter(new PrinterAdapterRefresh(deviceManager));
        apiAdapterImpl.setCfdAdapter(new CfdAdapterRefreshOr2_0(deviceManager));

        // Some Rev B units were made with Honeywell scanner, and behave like Plus.
        if (inventory.barCodeReaderSupportsVComMode()) {
            CommonUtil2_0 commonUtil = new CommonUtil2_0(context);
            apiAdapterImpl.setBarCodeReaderAdapter(new BarCodeReaderAdapter2_0(deviceManager, commonUtil, activityMonitor));
        } else {
            apiAdapterImpl.setBarCodeReaderAdapter(new BarCodeReaderAdapterRefresh(deviceManager));
        }

        apiAdapterImpl.setFtdiAdapter(new FtdiAdapterCommon(deviceManager));
        apiAdapterImpl.setMSRAdapter(new MsrAdapterMagtek(context));
    }

    private void initApiAdapter1_0(ApiAdapterImpl apiAdapterImpl){
        ActivityMonitor activityMonitor = new ActivityMonitor();

        apiAdapterImpl.setActivityMonitor(activityMonitor);
        apiAdapterImpl.setCashDrawerAdapter(new CashDrawerAdapter1_0());
        apiAdapterImpl.setCfdAdapter(new CfdAdapter1_0());
        apiAdapterImpl.setBarCodeReaderAdapter(new BarCodeReaderAdapter1_0());
        apiAdapterImpl.setPrinterAdapter(new PrinterAdapter1_0());
        apiAdapterImpl.setFtdiAdapter(new FtdiAdapter1_0());
        apiAdapterImpl.setMSRAdapter(new MsrAdapterMagtek(context));
    }

    // TODO: If there is any difference between I-Series 1.0 and 2.0, split this into two
    private void initApiAdapterISeries(ApiAdapterImpl apiAdapterImpl, Inventory inventory) throws UnsupportedEloPlatform {
        DeviceManager deviceManager = DeviceManager.getInstance(EloPlatform.I_SERIES_1, context);
        EloPlatform platform = inventory.getProductInfo().eloPlatform;
        CommonUtil2_0 commonUtil = new CommonUtil2_0(context);
        ActivityMonitor activityMonitor = new ActivityMonitor();

        apiAdapterImpl.setActivityMonitor(activityMonitor);
        if (inventory.hasBarCodeReader()) {
            switch (inventory.getBcrDeviceType()) {
            case NUMA:
                apiAdapterImpl.setBarCodeReaderAdapter(new BarCodeReaderAdapterISeriesNuma());
                break;
            case HONEYWELL_N3680:
                apiAdapterImpl.setBarCodeReaderAdapter(new BarCodeReaderAdapterISeriesHoneywell(deviceManager, commonUtil, activityMonitor));
                break;
            }
        }

        if (inventory.hasPrinter()) {
            switch (inventory.getPrinterDeviceType()) {
            case STAR:
                apiAdapterImpl.setPrinterAdapter(new PrinterAdapter2_0(deviceManager, commonUtil, activityMonitor));
                apiAdapterImpl.setCashDrawerAdapter(new CashDrawerAdapterStar(deviceManager, commonUtil));
                break;
            case EPSON:

                break;
            default:
                apiAdapterImpl.setPrinterAdapter(new NullApiAdapter());
                apiAdapterImpl.setCashDrawerAdapter(new NullApiAdapter());
                break;
            }
        }

        if (inventory.hasMsr()) {
            apiAdapterImpl.setMSRAdapter(new MsrAdapterMagtek(context));
        } else {
            apiAdapterImpl.setMSRAdapter(new NullApiAdapter());
        }

        if (platform.equals(EloPlatform.I_SERIES_1)) {
            apiAdapterImpl.setFtdiAdapter(new FtdiAdapter1_0());
        } else {
            apiAdapterImpl.setFtdiAdapter(new FtdiAdapterCommon(deviceManager));
        }

        // Unimplemented
        apiAdapterImpl.setCfdAdapter(new NullApiAdapter());
    }

}
