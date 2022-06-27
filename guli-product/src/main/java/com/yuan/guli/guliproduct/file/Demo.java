package com.yuan.guli.guliproduct.file;



public class Demo {
//    //测试：测试文件上传阿里云
//    @Autowired
//    OSSClient ossClient;
//
//    public static void main(String[] args) throws Exception {
//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
//
//        // 账号密码
//        String accessKeyId ="LTAI5t8ZCvG99m7K9pi9ieFr";
//        String accessKeySecret = "hAFh8zP4PpxfEZiQ1gt555kZnlePU8";
//
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "gulilzy";
//
//        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
//        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        String filePath= "D:\\test.jpg"; //要上传的图片路径
//
//        // 创建OSSClient实例:ossClient 对象存储操作的客户端
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        try {
//            //上传文件流：
//            InputStream inputStream = new FileInputStream(filePath);
//            // 创建PutObject请求。存储空间名字-
//            ossClient.putObject(bucketName, "test.jpg", inputStream);
//        }finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//                System.out.println("上传完成...");
//            }
//        }
//    }
}