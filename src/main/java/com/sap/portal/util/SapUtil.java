package com.sap.portal.util;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hfzhang
 * @date 2024/4/30 15:37
 */
public class SapUtil {


    //mvn install:install-file -Dfile=sapjco3.jar -Dpackaging=jar -DgroupId=com.sap -DartifactId=sapjco -Dversion=3.0
    private static final String ABAP_AS_SAP = "ABAP_AS_SAP";
    private static JCoDestination destination = null;
    static{
        Properties connectProperties = new Properties();
//        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "10.3.129.1");//服务器
//        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");        //系统编号
//        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "120");       //SAP集团
//        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "JCWEI");  //SAP用户名
//        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "201909");     //密码
//        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "ZH");        //登录语言
//        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");  //最大连接数
//        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");     //最大连接线程


        connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, "ET0");
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "sapet0app01.gaojihealth.com");//服务器
        connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV, "3601");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");
        connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, "ET0_LG_01");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "ZH");        //登录语言
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "600");       //SAP集团
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "GJ_JM");  //SAP用户名
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "Gaoji_2024");     //密码
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");  //最大连接数
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");     //最大连接线程

        createDataFile(ABAP_AS_SAP, "jcoDestination", connectProperties);
    }

    /**
     * 创建SAP接口属性文件。
     * @param name  ABAP管道名称
     * @param suffix  属性文件后缀
     * @param properties  属性文件内容
     */
    private static void createDataFile(String name, String suffix, Properties properties){
        File cfg = new File(name+"."+suffix);
        if(cfg.exists()){
            cfg.deleteOnExit();
        }
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(cfg, false);
            properties.store(fos, "for connection !");
        }catch (Exception e){
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
        }finally {
            if(null != fos){
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /*
     * * 获取SAP连接
     *
     * @return SAP连接对象
     */
    public static JCoDestination connect() {
        try {
            if(null == destination)
                destination = JCoDestinationManager.getDestination(ABAP_AS_SAP);
        } catch (JCoException e) {
            System.out.println("Connect SAP fault, error msg: " + e.toString());
        }
        return destination;
    }

    public static void main(String[] args) {
        try {
//            select();
//            create();
            selectStoreReturn();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建订单
     * @throws JCoException
     */
    public static void create() throws JCoException {
        JCoDestination destination = SapUtil.connect();
        JCoFunction function = destination.getRepository().getFunctionTemplate("ZMMFM0802").getFunction();
        JCoStructure jCoStructure = function.getImportParameterList().getStructure("IS_HEAD");
        jCoStructure.setValue("ZDDLX", "01");
        jCoStructure.setValue("LIFNR", "30A0");
        jCoStructure.setValue("EKORG", "V003");
        jCoStructure.setValue("ERNAM", "80000525");
        jCoStructure.setValue("WERKS", "V003");
        jCoStructure.setValue("ZYWCJ", "1");
        jCoStructure.setValue("EEIND", "20240509");

        JCoTable jCoTableItems = function.getTableParameterList().getTable("IT_ITEM");
        jCoTableItems.appendRow();
        jCoTableItems.setValue("EBELP","1");
        jCoTableItems.setValue("LGORT","1000");
        jCoTableItems.setValue("MATNR","1005569");
        jCoTableItems.setValue("MENGE","1.000");
        jCoTableItems.setValue("ZZPRICE","18.80000");
        jCoTableItems.setValue("ZZMWSKZ","J0");
        jCoTableItems.setValue("ZSCPH","2405090001");
        jCoTableItems.setValue("ZZSCCJ","10111632");
        jCoTableItems.setValue("ZZSPCD","沧州");
        jCoTableItems.appendRow();
        jCoTableItems.setValue("EBELP","2");
        jCoTableItems.setValue("LGORT","1000");
        jCoTableItems.setValue("MATNR","1005569");
        jCoTableItems.setValue("MENGE","1.000");
        jCoTableItems.setValue("ZZPRICE","18.80000");
        jCoTableItems.setValue("ZZMWSKZ","J0");
        jCoTableItems.setValue("ZSCPH","2405090001");
        jCoTableItems.setValue("ZZSCCJ","10111632");
        jCoTableItems.setValue("ZZSPCD","沧州");

        function.execute(destination);
        JCoStructure jCoStructure1 = function.getExportParameterList().getStructure("OS_RETURN");
        Map<String, Object> resultMap = new ConcurrentHashMap<>();
        jCoStructure1.forEach(v->{
            resultMap.put(v.getName(), v.getValue());
        });
        System.out.println(resultMap);
        System.out.println("EBELN: "+jCoStructure1.getString("EBELN"));
        System.out.println("VBELN: "+jCoStructure1.getString("VBELN"));
        System.out.println("ZSTATUS: "+jCoStructure1.getString("ZSTATUS"));
        System.out.println("ZMESS: "+jCoStructure1.getString("ZMESS"));
    }

    /**
     * 查询订单
     * @throws JCoException
     */
    public static void select() throws JCoException {
        JCoDestination destination = SapUtil.connect();
        JCoFunction function = destination.getRepository().getFunctionTemplate("ZMMFM0804").getFunction();
        JCoStructure jCoStructure = function.getImportParameterList().getStructure("IS_INPUT");
//        jCoStructure.setValue("BUKRS", "V003");
        jCoStructure.setValue("LIFNR", "30A0");
        jCoStructure.setValue("ZKSDAT", "20240501");
        jCoStructure.setValue("ZJSDAT", "20240531");

//        JCoTable jCoTableItems = function.getTableParameterList().getTable("IT_MAT");
//        jCoTableItems.appendRow();
//        jCoTableItems.setValue("MATNR","1005569");

        function.execute(destination);
        JCoTable jCoTable = function.getTableParameterList().getTable("OT_DETAIL");
        System.out.println(function.getTableParameterList().getParameterFieldIterator().nextParameterField().getName());
        List<Map<String, Object>> mapList = new ArrayList<>();
        for(int i = 0; i<jCoTable.getNumRows(); i++){
            jCoTable.setRow(i);
            Map<String, Object> map = new ConcurrentHashMap<>();
            JCoRecordFieldIterator iterator = jCoTable.getRecordFieldIterator();
            while(iterator.hasNextField()){
                JCoRecordField jCoRecordField = iterator.nextRecordField();
                map.put(jCoRecordField.getName(), jCoTable.getValue(jCoRecordField.getName()));
            }
            mapList.add(map);
//            System.out.println(jCoTable.getString("ZYWCJ"));
//            System.out.println(jCoTable.getString("BUKRS"));
//            System.out.println(jCoTable.getString("LIFNR"));
//            System.out.println(jCoTable.getString("EKORG"));
//            System.out.println(jCoTable.getString("EBELN"));
//            System.out.println(jCoTable.getString("EBELP"));
//            System.out.println(jCoTable.getString("ERNAM"));
//            System.out.println(jCoTable.getString("WERKS"));
//            System.out.println(jCoTable.getString("LGORT"));
//            System.out.println(jCoTable.getString("MATNR"));
//            System.out.println(jCoTable.getString("MENGE"));
//            System.out.println(jCoTable.getString("EINDT"));
//            System.out.println(jCoTable.getString("MENGE1"));
//            System.out.println(jCoTable.getString("ZSFGG"));
//            System.out.println(jCoTable.getString("ZZPRICE"));
        }
        System.out.println("--------------------mapList-------------------------"+mapList);
    }


    /**
     * 查询门店退仓
     * @throws JCoException
     */
    public static void selectStoreReturn() throws JCoException {
        JCoDestination destination = SapUtil.connect();
        JCoFunction function = destination.getRepository().getFunctionTemplate("ZMMFM0809").getFunction();
        JCoStructure jCoStructure = function.getImportParameterList().getStructure("IS_INPUT");
        jCoStructure.setValue("BUKRS", "V003");
        jCoStructure.setValue("ZSTATUS", "0");
        jCoStructure.setValue("WERKS", "V003");
        jCoStructure.setValue("ZKSDAT", "20230130");
        jCoStructure.setValue("ZJSDAT", "20230130");

        JCoTable jCoTableItems = function.getTableParameterList().getTable("IT_MAT");
        jCoTableItems.appendRow();
        jCoTableItems.setValue("MATNR","1005217");
        JCoTable jCoTableItems2 = function.getTableParameterList().getTable("IT_MD");
        jCoTableItems2.appendRow();
        jCoTableItems2.setValue("ZMEND","B4X4q");

        function.execute(destination);
        JCoTable jCoTable = function.getTableParameterList().getTable("OT_DETAIL");
        System.out.println(function.getTableParameterList().getParameterFieldIterator().nextParameterField().getName());
        List<Map<String, Object>> mapList = new ArrayList<>();
        for(int i = 0; i<jCoTable.getNumRows(); i++){
            jCoTable.setRow(i);
            Map<String, Object> map = new ConcurrentHashMap<>();
            JCoRecordFieldIterator iterator = jCoTable.getRecordFieldIterator();
            while(iterator.hasNextField()){
                JCoRecordField jCoRecordField = iterator.nextRecordField();
                map.put(jCoRecordField.getName(), jCoTable.getValue(jCoRecordField.getName()));
            }
            mapList.add(map);
        }
    }

}
