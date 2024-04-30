package com.sap.portal.util;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "10.3.129.1");//服务器
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");        //系统编号
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "120");       //SAP集团
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   "JCWEI");  //SAP用户名
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "201909");     //密码
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "ZH");        //登录语言
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
        JCoFunction function = null;
        JCoDestination destination = SapUtil.connect();
        int result=0;//调用接口返回状态
        String message="";//调用接口返回信息
        try {
            function = destination.getRepository().getFunctionTemplate("ZRFC_DEMO").getFunction();
            JCoParameterList input = function.getImportParameterList();
            input.setValue("IV_BUKRS","32R0");//输入参数
            JCoParameterList inputTable = function.getTableParameterList();
            JCoTable jCoTable = inputTable.getTable("IT_MAT");
            for(int i = 0; i<jCoTable.getNumRows(); i++){
                jCoTable.setRow(i);
                jCoTable.getTable(i);
//               System.out.println(table.getString("IDAT2").equals("0000-00-00"));
            }
//            input.setValue("I_ADD","X");
            function.execute(destination);
//            result= function.getExportParameterList().getInt("RESULT");//调用接口返回结果
//            message= function.getExportParameterList().getString("MSG");//调用接口返回信息
            JCoTable table =  function.getTableParameterList().getTable("OT_ITEM");
            JCoMetaData jCoMetaData = table.getMetaData();
            System.out.println(jCoMetaData);
            for(int i = 0; i<table.getNumRows(); i++){
                table.setRow(i);
                System.out.println(table.getString("ZZZMDSQ"));
//               System.out.println(table.getString("IDAT2").equals("0000-00-00"));
            }
            System.out.println("调用返回结果--->"+result+";调用返回状态--->"+message);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
