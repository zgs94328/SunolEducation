package example;


public class Config {




 // 为了apiKey,secretKey为您调用百度人脸在线接口的，如注册，识别等。
 // 为了的安全，建议放在您的服务端，端把人脸传给服务器，在服务端端进行人脸注册、识别放在示例里面是为了您快速看到效果
 public static String apiKey = "19454353";
 public static String secretKey = "sExdr95cjwBg5wYvpGqePGizG7CycwAT";
 public static String licenseID = "sunoleducation-face-android";
 public static String licenseFileName = "idl-license.face-android";

    /*
     * <p>
     * 每个开发者账号只能创建一个人脸库；groupID用于标识人脸库
     * <p>
     * 人脸识别 接口 https://aip.baidubce.com/rest/2.0/face/v3/search
     * 人脸注册 接口 https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add
     */
    public static String groupID = "123";

}