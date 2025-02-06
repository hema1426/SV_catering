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

    // Clients API Urls
    public static String SM_LIVE="http://122.11.163.201:86/es/data/api/";
    public static String  SM_DEMO="http://94.237.70.51:301/es/data/api/";
    public static String ANA_LIVE="http://ezysales.sg:452/es/data/api/";
    public static String ANA_DEMO="http://94.237.70.51:153/es/data/api/";
    public static String TEST_URL="http://116.202.130.235:588/es/data/api/";
    public static String TEST_UTL_2="http://122.11.163.201:85/winappapi/";
    public static String TEST_CLIENT="http://94.237.70.51:220/es/data/api/";
    public static String SHANSAN="http://94.237.70.51:340/es/data/api/";

    // Url Using for the SAP implementation
    public static String SAP_BASE_URL_DEMO ="http://223.25.81.205:8345/api/";

    public static String SAP_FOODGLE_HUB_NEW="https://c21202app01p02.cloudiax.com:10092/api/";

    public static String SAP_BASE_URL_LIVE_DEMO="http://223.25.81.205:8349/api/";

    // Client Live URL
    public static String SAP_LIVE_URL="http://223.25.81.205:8352/api/";


    public static String WINAPP_DEMO_URL="http://43.228.126.51:83/api/";

    //shaban LIVE URL    http://129.150.58.169:95
    public static String SHABAN_URL="http://129.150.58.169:91/api/";


    // Live URL
    public static String NEW_LIVE_URL_AADHI ="http://18.143.95.61:81/api/";

    // Live URL
   // public static String WINAPP_SOLUTION_PTE_LTD ="http://223.25.81.205:91/api/";


    // Deli X-Press-Live
    public static String DELI_XPRESS_Live ="http://101.100.181.5:91/api/";


    // Deli X-Press-Demo
    public static String DELI_XPRESS_Demo ="http://18.141.253.66:83/api/";

    // DT-FRESH
    public static String DT_FRESH ="http://172.16.10.98:88/api/";


    public static String DEMO_URL_SUPERSTAR ="http://43.228.126.65:80/api/";

    // Super Star- Demo
    public static String SUPER_STAR_DEMO ="http://13.215.239.251:81/api/";
    //UNICO
    public static String UNICO ="http://158.140.143.87:79/api/";

    // Super Star- Live
    public static String SUPER_STAR_LIVE_1 ="http://13.215.239.251:83/api/";

    // ALS Demo
   // public static String ALS_DEMO ="http://43.228.126.65:5455/api/";

    // ALS Demo
    public static String ALS_DEMO ="http://43.228.126.114:80/api/";


    // Dawood Demo
    public static String DAWOOD_DEMO ="http://129.150.58.169:95/api/";

    // Dawood Live
    public static String DAWOOD_LIVE ="http://129.150.58.169:97/api/";
    // ALS Live
    public static String ALS_LIVE ="http://43.228.126.114:85/api/";


    // OmniMetric Live
    public static String OMNI_METRIC ="http://43.228.126.51:83/api/";

    // ALS Live
    public static String DHANALAKSHMI_URL ="http://43.228.126.67:8082/api/";

    // Deli X-Press-3-Live
    public static String DELI_XPRESS_3_Live ="http://43.228.126.65:5554/api/";
    // Deli X-Press-live-13.3
    public static String DELI_XPRESS_live_new ="http://18.141.253.66:83/api/";
    // AADHI DEMO URL
    public static String AADHI_DEMO ="http://18.143.95.61:99/api/";

    // AADHI LIVE URL
    public static String AADHI_LIVE ="http://18.143.95.61:81/api/";

    //RAYMANG
    public static String cloudtak ="https://app0027.cloudtaktiks.com:11014/api/";

    // SLG LIVE URL
    public static String SLG_LIVE ="http://13.251.236.153:90/api/";

    public static String SLG_DEMO ="http://13.251.236.153:90/api/";
    public static String SUPER_STAR_DEMO_new10 ="http://13.215.239.251:81/api/";

    public static String TRANS_ORIENT_DEMO ="https://c21326-EasySales-Test.cloudiax.com/api/";
    public static String FUXIN ="https://app0030.cloudtaktiks.com:11018/api/";
    public static String test_URL_OCT24 ="https://c21199-er-test.cloudiax.com/api/";
   //RAYMANG
    public static String RAYMANG_URL_JULY="https://app0027.cloudtaktiks.com:11014/api/";
    public static String SV_Catering_URL=" https://c21199-ezy-cashcollection.cloudiax.com/api/";

   // Aadhi demo july
    public static String aadhi_URL_JULY="http://18.143.95.61:99/api/";
    // transOrient live

    //http://c21326-easysales-test.cloudiax.com:11014/
   //public static String TRANS_ORIENT_RAYMANG ="https://app0027.cloudtaktiks.com:11014/api/";

    public static String foodgle_LIVE ="https://c21202app01p01.cloudiax.com:10093/api/";

    public static String SHABAN ="http://129.150.58.169:95/api/";


    // TO Singapore Pte. Ltd.
    public static String TO_Singapore ="http://52.220.175.199:97/api/";

    // Cloudx-API
    public static String Cloud_X="https://c21202app01p01.cloudiax.com:10093/api/";



    // EXPO-Deix-Demo-->.
    public static String Expo_Delix_Demo ="http://101.100.181.5:91/api/";


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
