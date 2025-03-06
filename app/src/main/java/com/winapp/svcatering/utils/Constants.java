package com.winapp.svcatering.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

public class Constants {

    // Urls of the App for All

    public static String LICENSE_VALIDATE_SECRET_CODE="4D0FW";
    public static String LICENSE_VALIDATE_SECRET_PASSWORD="98A5W";

    public static String API_SECRET_CODE="winapp";
    public static String API_SECRET_PASSWORD="admin";

    public static String VALIDATE_URL_REMAINING="/es/data/api/MasterApi/GetApiUrlValidation?Requestdata=null";

    public static String SPLASH_SCREEN_VALIDATION="http://ezysales.sg:500/edata/wims/data/api/MobileLicenseAPI/ValidateLicense_FlashScreen?RequestData=";
    public static String FIRST_TIME_ACTIVATION_URL="http://ezysales.sg:500/edata/wims/data/api/MobileLicenseAPI/ValidateLicenseFirstTimeActivation?RequestData=";
    public static String VALIDATE_LICENCE_VIA_OTP="http://ezysales.sg:500/edata/wims/data/api/MobileLicenseAPI/ValidateLicenseViaOTP?RequestData=";
    public static String PURCHASE_LICENCE_VIA_OTP="http://ezysales.sg:500/edata/wims/data/api/MobileLicenseAPI/PushLicenseViaOTP?RequestData=";
    public static String folderPath = Environment.getExternalStorageDirectory() + "/CatalogErp/Products";

    public static String APP_CODE="17";

    public static String NEW_LICENCE_URL="http://3.85.9.22/Licence/api/LicenceApi/RegisterLicence";

    public static String NEW_LICENCE_CHECK_URL="http://3.85.9.22/Licence/api/LicenceApi/CheckDevice";

    public static String PART_URL="/api/";

    public static String KEY_ADDRESS_ZONE_CODE = "addressZoneCode" ;
    public static String KEY_ADDRESS_ZONE_NAME = "addressZoneName";
    public static String DEFAULT_STRING = "" ;


    // todo app name - SV Catering cash collection
    public static String SV_Catering_DEMO_URL =" https://c21199-ezy-cashcollection.cloudiax.com/api/";
    public static String SV_Catering_DEMO_URL_1 =" https://c21521app01p01-cashcollection.cloudiax.com/api/";

    public static String getFolderPath(Context mContext){
        return new ContextWrapper(mContext).getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()+ "/CatalogSAPErp/Products";
    }

    public static String getPdfFolderPath(Context mContext){
        return new ContextWrapper(mContext).getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()+ "/CatalogSAPErp/InvoicePdfs";
    }

    public static String getSignatureFolderPath(Context mContext){
        return new ContextWrapper(mContext).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+ "/CatalogErp/Signature";
    }
}
