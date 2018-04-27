package com.oldratlee.runner;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Task {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final int TOTAL_MS = 86400000;
    private final int MIN_STEP = 60000;
    private final ZoneId ZONE = ZoneId.of("UTC");
    
    private final String HEART_BEAT = "heartbeat";

    //    private final String PATH_PRE = "/data/";
    private final String PATH_PRE = "/data/";

    public static void main(String[] args) throws IOException {

        Set<String> interfaces = new HashSet<>();
        interfaces.add("device_management_service");
        interfaces.add("query_device_management_service");
        new Task().run("device_management", interfaces, "2016-09-01", "2018-04-23");
    }


    public void run(String serviceName, Set<String> interfaces, String startDate, String endDate) throws IOException {


        String[] interfaceNameList = new String[interfaces.size() + 1];
        //间隔
        int[] steps = new int[interfaces.size() + 1];
        //最小间隔
        int step = MIN_STEP;

        Random random = new Random();
        int n = random.nextInt(100000) + 200001;
        int m = random.nextInt(500) + 1001;
        int x = random.nextInt(50000) + 100001;


        int i = 0;
        for (String interfaceName : interfaces) {
            if (interfaceName.contains("query")) {
                steps[i] = TOTAL_MS / n;
            } else if (interfaceName.contains("delete")) {
                steps[i] = TOTAL_MS / m;
            } else {
                steps[i] = TOTAL_MS / x;
            }
            if (step > steps[i]) {
                step = steps[i];
            }
            interfaceNameList[i] = interfaceName;
            i++;
        }
        //加一个 heartbeat
        interfaceNameList[interfaceNameList.length - 1] = HEART_BEAT;
        //加一个 heartbeat step
        steps[steps.length - 1] = 60000;
        outFile(serviceName, interfaceNameList, startDate, endDate, steps, step);
    }

    /**
     * 输出到文件
     */
    public void outFile(String serverName, String[] interfaceNameList, String startDateStr, String endDateStr, int[] steps, int step) throws IOException {
        if (interfaceNameList == null || interfaceNameList.length == 0) {
            return;
        }


        File folder = new File(PATH_PRE + serverName);
        if (!folder.exists()){
            folder.mkdirs();
        }

        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        //任务开始日期跟结束日期的天数差
        int days = Period.between(startDate, endDate).getDays();
        boolean startlog = true;
        for (int i = 0; i < days; i++) {
            //当天开始的时间
            Instant instant = startDate.plusDays(i).atStartOfDay(ZONE).toInstant();
            //初始化
            Instant[] interfaceCurrent = new Instant[interfaceNameList.length];
            for (int j = 0; j < interfaceNameList.length; j++) {
                interfaceCurrent[j] = instant;
            }
            File file = new File(PATH_PRE + serverName + "/" + serverName + "." + startDate.plusDays(i).format(formatter) + ".log");//新建一个文件对象，如果不存在则创建一个该文件
            if (file.exists()){
                file.delete();
            }

            FileWriter fw;

            Period period = Period.between(startDate.plusDays(i), endDate);
            int month = period.getYears() * 12 + period.getMonths();
            long[] currentSteps = new long[steps.length];
            Random random = new Random();
            double n = ((double) random.nextInt(100) / 33 + 88) / 100;
            for (int z = 0; z < steps.length; z++) {
                //心跳日志不改变频率
                if (z != steps.length - 1) {
                    currentSteps[z] = (int) Math.ceil(steps[z] / Math.pow(n, (double) month));
                } else {
                    currentSteps[z] = steps[z];
                }
                System.out.println(startDate.plusDays(i) + " " + interfaceNameList[z] + "调用频次 = " + TOTAL_MS / currentSteps[z]);
            }
            int currentStep = (int) Math.ceil(step / Math.pow(n, (double) month));
            fw = new FileWriter(file, true);
            StringBuffer outputStr = new StringBuffer();
            if (startlog == true) {
                outputStr.append(outStartServerlog(serverName, instant));
                startlog = false;
            }
            while (instant.isBefore(startDate.plusDays(i + 1).atStartOfDay().atZone(ZONE).toInstant())) {
                for (int k = 0; k < steps.length; k++) {
                    if (interfaceCurrent[k].isBefore(instant)) {
                        outputStr.append(writeLog(serverName, interfaceNameList[k], instant));
                        instant = instant.plusMillis(67);
                        interfaceCurrent[k] = instant.plusMillis(currentSteps[k]);
                    }
                    fw.append(outputStr);
                    outputStr.setLength(0);
                }
                instant = instant.plusMillis(currentStep);
            }
            if (fw != null) {
                fw.close();
            }

        }
    }


    public String writeLog(String serverName, String interfaceName, Instant instant) {

        StringBuffer sb = new StringBuffer();

        if (interfaceName.equals(HEART_BEAT)) {
            sb.append(HEART_BEAT_LOG_MODEL.replace("xxxxxxxxxxxxx", UUID.randomUUID().toString().replace("-", "")).replace("YYYYYYY", formatTime(instant, "yyyy-MM-dd HH:mm:ss.SSS")));
            return sb.toString();
        }

//        //服务关闭日志(替换地址)
//        sb.append(END_SERVER_LOG_MODEL.replace("XXXXXX", "127.0.0.1"));


        words.replaceAll("\r|\n*", "");
        List<String> gameids = java.util.Arrays.asList(words.split(","));
        Collections.shuffle(gameids);
        Random random = new Random();
        int number1 = random.nextInt(21) + 5;
        //请求参数
        StringBuffer urlrequest = new StringBuffer();
        StringBuffer sqlrequest = new StringBuffer();

        List<String> params = new ArrayList<>();
        params = gameids.subList(0, number1);
        StringBuffer sqlinsert = new StringBuffer();
        //获取请求随机参数
        for (int i = 0; i < params.size(); i++) {
            if (i < params.size() - 1) {
                urlrequest.append(params.get(i)).append("=").append(params.get(i)).append(",");
                sqlrequest.append(params.get(i)).append("as").append(params.get(i)).append("_id").append(i).append("_5_,");
            } else {
                urlrequest.append(params.get(i)).append("=").append(params.get(i));
                sqlrequest.append(params.get(i)).append("as").append(params.get(i)).append("_id").append(i).append("_5_");
            }
            sqlinsert.append("?");
        }
        List<String> urlrequestparams = new ArrayList<>();
        urlrequestparams.add(urlrequest.toString().replaceAll("\r|\n*", ""));

        List<String> sqlrequestparams = new ArrayList<>();
        sqlrequestparams.add(sqlrequest.toString().replaceAll("\r|\n*", ""));

        //获取响应随机参数
        Collections.shuffle(gameids);
        StringBuffer urlresponse = new StringBuffer();
        StringBuffer sqlresponse = new StringBuffer();

        int number3 = random.nextInt(6) + 1;
        for (int i = 0; i < number3; i++) {
            if (i < number3 - 1) {
                urlresponse.append(gameids.get(i)).append("=").append(gameids.get(i)).append(",");
                sqlresponse.append(gameids.get(i)).append("= ? and ");
            } else {
                urlresponse.append(gameids.get(i)).append("=").append(gameids.get(i));
                sqlresponse.append(gameids.get(i)).append("= ?");
            }
        }
        //接口响应参数
        List<String> urlresponseparams = new ArrayList<>();
        urlresponseparams.add(urlresponse.toString().replaceAll("\r|\n*", ""));
        //sql相应参数
        List<String> sqlresponseparams = new ArrayList<>();
        sqlresponseparams.add(sqlresponse.toString().replaceAll("\r|\n*", ""));
        List<String> sqlinseret2 = new ArrayList<>();
        sqlinseret2.add(sqlinsert.toString().replaceAll("\r|\n*", ""));

        String abc = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuffer dbName = new StringBuffer();
        for (int i = 0; i < 3; ++i) {
            int number = random.nextInt(36);
            dbName.append(abc.charAt(number));
        }
        dbName.append("_DB");
        //查询SQL模板
        String sql_select_model = "Hibernate: select " + sqlrequestparams.toString().replace("[", "").replace("]", "").toLowerCase() + " from " + dbName.toString() + " where " + sqlresponseparams.toString().replace("[", "").replace("]", "").toLowerCase() + " limit ?\n";
        //插入SQL模板
        String sql_insert_model = "Hibernate: inseret into " + dbName.toString().toLowerCase() + "(" + sqlrequestparams.toString().replace("[", "").replace("]", "").toLowerCase() + ")values (" + sqlinseret2.toString().replace("[", "").replace("]", "").toLowerCase() + ")\n";
        //更新SQL模板
        String sql_update_model = "Hibernate: update " + sqlrequestparams.toString().replace("[", "").replace("]", "").toLowerCase() + " from " + dbName.toString() + " where " + sqlresponseparams.toString().replace("[", "").replace("]", "").toLowerCase() + "\n";

        //接口调用模板
        String request_log_model = "YYYYYYY DEBUG XNIO-2 task-2 --- [XNIO-2 task-2] com.irootech.aop.logging.LoggingAspect - Enter: XXXXXXXX with argument[s] = [ModuleBoxChangesQueryVM{" + urlrequestparams.toString().replace("[", "").replace("]", "") + "}, Page request [number: 1, size 20, sort: null]]\n" +
                "YYYYYYY DEBUG XNIO-2 task-2 --- [XNIO-2 task-2] XXXXXXXX : {" + urlrequestparams + "}Page request [number: 1, size 20, sort: null]\n";

        //接口调用响应模板
        String response_log_model =  "YYYYYYY DEBUG XNIO-2 task-2 --- [XNIO-2 task-2] com.irootech.aop.logging.LoggingAspect - Exit: XXXXXXXX with result = <200 OK,{total='0', pageSize='20', data = " + urlresponseparams.toString().replace("[", "").replace("]", "") + "},{X-Total-Count=[0], Link=[</api/V1/modulebox/changes?page=0&size=20>; rel=\"last\",</api/V1/modulebox/changes?page=0&size=20>; rel=\"first\"], status_code=[200], status_msg=[]}>+\n";

        //接口调用日志
        String request_log = request_log_model.replace("XXXXXXXX", serverName + "." + interfaceName).replace("YYYYYYY", formatTime(instant.plusMillis(17), "yyyy-MM-dd HH:mm:ss.SSS"));
        //接口调用响应日志
        String response_log = response_log_model.replace("XXXXXXXX", serverName + "." + interfaceName).replace("YYYYYYY", formatTime(instant.plusMillis(33), "yyyy-MM-dd HH:mm:ss.SSS"));

        if (interfaceName.startsWith("query")) {
            sb.append(request_log + sql_select_model + response_log);
        } else if (interfaceName.startsWith("delete")) {
            sb.append(request_log + sql_select_model + sql_update_model + response_log);

        } else {
            sb.append(request_log + sql_insert_model + response_log);
        }

        return sb.toString();
    }

    //服务关闭日志模板
    String END_SERVER_LOG_MODEL = "Disconnected from the target VM, address: '127.0.0.1', transport: 'socket'\n" +
            "\n" +
            "Process finished with exit code -1";

    //心跳日志模板
    private String HEART_BEAT_LOG_MODEL = "YYYYYYY INFO  DiscoveryClient-HeartbeatExecutor-0 --- [DiscoveryClient-HeartbeatExecutor-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-DEVICE/boss-device:xxxxxxxxxxxxx - Re-registering apps/BOSS-DEVICE\n" +
            "YYYYYYY INFO  DiscoveryClient-HeartbeatExecutor-0 --- [DiscoveryClient-HeartbeatExecutor-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-DEVICE/boss-device:xxxxxxxxxxxxx: registering service...\n" +
            "YYYYYYY INFO  DiscoveryClient-HeartbeatExecutor-0 --- [DiscoveryClient-HeartbeatExecutor-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-DEVICE/boss-device:xxxxxxxxxxxxx - registration status: 204\n";


    private String outStartServerlog(String serverName, Instant dateTime) {
        //服务启动日志模板
        String start_server_log_model = "        ██╗ ██╗   ██╗ ████████╗ ███████╗   ██████╗ ████████╗ ████████╗ ███████╗\n" +
                "        ██║ ██║   ██║ ╚══██╔══╝ ██╔═══██╗ ██╔════╝ ╚══██╔══╝ ██╔═════╝ ██╔═══██╗\n" +
                "        ██║ ████████║    ██║    ███████╔╝ ╚█████╗     ██║    ██████╗   ███████╔╝\n" +
                "  ██╗   ██║ ██╔═══██║    ██║    ██╔════╝   ╚═══██╗    ██║    ██╔═══╝   ██╔══██║\n" +
                "  ╚██████╔╝ ██║   ██║ ████████╗ ██║       ██████╔╝    ██║    ████████╗ ██║  ╚██╗\n" +
                "   ╚═════╝  ╚═╝   ╚═╝ ╚═══════╝ ╚═╝       ╚═════╝     ╚═╝    ╚═══════╝ ╚═╝   ╚═╝\n" +
                "\n" +
                ":: JHipster \uD83E\uDD13  :: Running Spring Boot 1.5.9.RELEASE ::\n" +
                ":: http://www.jhipster.tech ::\n" +
                "\n" +
                "2016-09-01 01:20:51.440 INFO  restartedMain --- [restartedMain] com.irootech.BillApp - The following profiles are active: swagger,local\n" +
                "2016-09-01 01:20:56.818 DEBUG restartedMain --- [restartedMain] com.irootech.config.AsyncConfiguration - Creating Async Task Executor\n" +
                "2016-09-01 01:20:58.396 DEBUG restartedMain --- [restartedMain] com.irootech.config.MetricsConfiguration - Registering JVM gauges\n" +
                "2016-09-01 01:20:58.553 DEBUG restartedMain --- [restartedMain] com.irootech.config.MetricsConfiguration - Monitoring the datasource\n" +
                "2016-09-01 01:20:58.560 DEBUG restartedMain --- [restartedMain] com.irootech.config.MetricsConfiguration - Initializing Metrics JMX reporting\n" +
                "2016-09-01 01:20:00.714 INFO  restartedMain --- [restartedMain] com.irootech.config.WebConfigurer - Web application configuration, using profiles: swagger\n" +
                "2016-09-01 01:20:00.736 DEBUG restartedMain --- [restartedMain] com.irootech.config.WebConfigurer - Initializing Metrics registries\n" +
                "2016-09-01 01:20:00.743 DEBUG restartedMain --- [restartedMain] com.irootech.config.WebConfigurer - Registering Metrics Filter\n" +
                "2016-09-01 01:20:00.752 DEBUG restartedMain --- [restartedMain] com.irootech.config.WebConfigurer - Registering Metrics Servlet\n" +
                "2016-09-01 01:20:00.757 INFO  restartedMain --- [restartedMain] com.irootech.config.WebConfigurer - Web application fully configured\n" +
                "2016-09-01 01:20:01.814 DEBUG restartedMain --- [restartedMain] com.irootech.config.DatabaseConfiguration - Configuring Liquibase\n" +
                "2016-09-01 01:20:01.826 DEBUG restartedMain --- [restartedMain] i.g.jhipster.config.liquibase.AsyncSpringLiquibase - Starting Liquibase synchronously\n" +
                "2016-09-01 01:20:03.746 DEBUG restartedMain --- [restartedMain] i.g.jhipster.config.liquibase.AsyncSpringLiquibase - Liquibase has updated your database in 1916 ms\n" +
                "2016-09-01 01:20:11.061 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Initializing Eureka in region us-east-1\n" +
                "2016-09-01 01:20:11.318 INFO  restartedMain --- [restartedMain] c.n.discovery.provider.DiscoveryJerseyProvider - Using JSON encoding codec LegacyJacksonJson\n" +
                "2016-09-01 01:20:11.323 INFO  restartedMain --- [restartedMain] c.n.discovery.provider.DiscoveryJerseyProvider - Using JSON decoding codec LegacyJacksonJson\n" +
                "2016-09-01 01:20:11.599 INFO  restartedMain --- [restartedMain] c.n.discovery.provider.DiscoveryJerseyProvider - Using XML encoding codec XStreamXml\n" +
                "2016-09-01 01:20:11.599 INFO  restartedMain --- [restartedMain] c.n.discovery.provider.DiscoveryJerseyProvider - Using XML decoding codec XStreamXml\n" +
                "2016-09-01 01:20:12.305 INFO  restartedMain --- [restartedMain] c.n.d.shared.resolver.aws.ConfigClusterResolver - Resolving eureka endpoints via configuration\n" +
                "2016-09-01 01:20:12.549 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Disable delta property : false\n" +
                "2016-09-01 01:20:12.554 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Single vip registry refresh property : null\n" +
                "2016-09-01 01:20:12.555 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Force full registry fetch : false\n" +
                "2016-09-01 01:20:12.560 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Application is null : false\n" +
                "2016-09-01 01:20:12.560 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Registered Applications size is zero : true\n" +
                "2016-09-01 01:20:12.566 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Application version is -1: true\n" +
                "2016-09-01 01:20:12.567 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Getting all instance registry info from the eureka server\n" +
                "2016-09-01 01:20:12.940 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - The response status is 200\n" +
                "2016-09-01 01:20:12.943 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Starting heartbeat executor: renew interval is: 5\n" +
                "2016-09-01 01:20:12.951 INFO  restartedMain --- [restartedMain] com.netflix.discovery.InstanceInfoReplicator - InstanceInfoReplicator onDemand update allowed rate per min is 12\n" +
                "2016-09-01 01:20:12.956 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Discovery Client initialized at timestamp 1524627672956 with initial instances count: 3\n" +
                "2016-09-01 01:20:13.106 WARN  restartedMain --- [restartedMain] o.s.b.f.a.AutowiredAnnotationBeanPostProcessor - Autowired annotation should only be used on methods with parameters: public void com.irootech.service.BillSchedulingTask.run()\n" +
                "2016-09-01 01:20:16.789 DEBUG restartedMain --- [restartedMain] com.irootech.config.CacheConfiguration - Starting redisCacheManager\n" +
                "2016-09-01 01:20:17.967 INFO  DiscoveryClient-HeartbeatExecutor-0 --- [DiscoveryClient-HeartbeatExecutor-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-BILL/boss-bill:7ecbc334256bdcb0848cbb8e9989f251 - Re-registering apps/BOSS-BILL\n" +
                "2016-09-01 01:20:17.972 INFO  DiscoveryClient-HeartbeatExecutor-0 --- [DiscoveryClient-HeartbeatExecutor-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-BILL/boss-bill:7ecbc334256bdcb0848cbb8e9989f251: registering service...\n" +
                "2016-09-01 01:20:18.020 INFO  DiscoveryClient-HeartbeatExecutor-0 --- [DiscoveryClient-HeartbeatExecutor-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-BILL/boss-bill:7ecbc334256bdcb0848cbb8e9989f251 - registration status: 204\n" +
                "2016-09-01 01:20:18.357 DEBUG restartedMain --- [restartedMain] c.i.config.oauth2.OAuth2JwtAccessTokenConverter - Public key retrieved from OAuth2 server to create SignatureVerifier\n" +
                "2016-09-01 01:20:19.097 WARN  restartedMain --- [restartedMain] com.netflix.config.sources.URLConfigurationSource - No URLs will be polled as dynamic configuration sources.\n" +
                "2016-09-01 01:20:19.109 WARN  restartedMain --- [restartedMain] com.netflix.config.sources.URLConfigurationSource - No URLs will be polled as dynamic configuration sources.\n" +
                "2016-09-01 01:20:20.423 DEBUG restartedMain --- [restartedMain] i.g.jhipster.config.apidoc.SwaggerConfiguration - Starting Swagger\n" +
                "2016-09-01 01:20:20.441 DEBUG restartedMain --- [restartedMain] i.g.jhipster.config.apidoc.SwaggerConfiguration - Started Swagger in 14 ms\n" +
                "2016-09-01 01:20:23.429 INFO  restartedMain --- [restartedMain] com.netflix.discovery.DiscoveryClient - Saw local status change event StatusChangeEvent [timestamp=1524627683428, current=UP, previous=STARTING]\n" +
                "2016-09-01 01:20:24.154 INFO  DiscoveryClient-InstanceInfoReplicator-0 --- [DiscoveryClient-InstanceInfoReplicator-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-BILL/boss-bill:7ecbc334256bdcb0848cbb8e9989f251: registering service...\n" +
                "2016-09-01 01:20:24.165 INFO  DiscoveryClient-InstanceInfoReplicator-0 --- [DiscoveryClient-InstanceInfoReplicator-0] com.netflix.discovery.DiscoveryClient - DiscoveryClient_BOSS-BILL/boss-bill:7ecbc334256bdcb0848cbb8e9989f251 - registration status: 204\n" +
                "2016-09-01 01:20:24.390 INFO  restartedMain --- [restartedMain] com.irootech.BillApp - Started BillApp in 40.911 seconds (JVM running for 43.061)\n" +
                "2016-09-01 01:20:24.396 INFO  restartedMain --- [restartedMain] com.irootech.BillApp - \n" +
                "----------------------------------------------------------\n" +
                "\tApplication 'boss-bill' is running! Access URLs:\n" +
                "\tLocal: \t\thttp://localhost:8081\n" +
                "\tExternal: \thttp://10.66.25.120:8081\n" +
                "\tProfile(s): \t[swagger, local]\n" +
                "----------------------------------------------------------\n" +
                "2016-09-01 01:20:24.397 INFO  restartedMain --- [restartedMain] com.irootech.BillApp - \n" +
                "----------------------------------------------------------\n" +
                "\tConfig Server: \tConnected to the JHipster Registry\n" +
                "----------------------------------------------------------\n";
        //服务启动日志（替换服务名,地址）
        String start_server_log = start_server_log_model.replace("XXXXXX", serverName);
        return start_server_log;
    }


    //获取指定时间的指定格式
    private String formatTime(Instant instant, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZONE).format(instant);
    }

    //参数
    private final String words = "code,\n" +
            "method,\n" +
            "sort,\n" +
            "response,\n" +
            "category_id,\n" +
            "http_url,\n" +
            "environment,\n" +
            "remarks,\n" +
            "api_id,\n" +
            "instance_value,\n" +
            "code_id,\n" +
            "biz_date,\n" +
            "irootech_id,\n" +
            "cyc_time,\n" +
            "aa,\n" +
            "deviceid,\n" +
            "CHARACTER_SET_NAME,\n" +
            "DEFAULT_COLLATE_NAME,\n" +
            "DESCRIPTION,\n" +
            "MAXLEN,\n" +
            "COLLATION_NAME,\n" +
            "SORTLEN,\n" +
            "ID,\n" +
            "IS_DEFAULT,\n" +
            "IS_COMPILED,\n" +
            "PRIVILEGES,\n" +
            "CHARACTER_OCTET_LENGTH,\n" +
            "TABLE_NAME,\n" +
            "COLUMN_TYPE,\n" +
            "IS_NULLABLE,\n" +
            "COLUMN_COMMENT,\n" +
            "NUMERIC_PRECISION,\n" +
            "COLUMN_NAME,\n" +
            "COLUMN_KEY,\n" +
            "DATA_TYPE,\n" +
            "TABLE_CATALOG,\n" +
            "NUMERIC_SCALE,\n" +
            "ORDINAL_POSITION,\n" +
            "EXTRA,\n" +
            "CHARACTER_MAXIMUM_LENGTH,\n" +
            "TABLE_SCHEMA,\n" +
            "DATETIME_PRECISION,\n" +
            "COLUMN_DEFAULT,\n" +
            "IS_GRANTABLE,\n" +
            "GRANTEE,\n" +
            "PRIVILEGE_TYPE,\n" +
            "COMMENT,\n" +
            "TRANSACTIONS,\n" +
            "ENGINE,\n" +
            "XA,\n" +
            "SUPPORT,\n" +
            "SAVEPOINTS,\n" +
            "CHARACTER_SET_CLIENT,\n" +
            "STATUS,\n" +
            "EVENT_TYPE,\n" +
            "EVENT_CATALOG,\n" +
            "LAST_EXECUTED,\n" +
            "SQL_MODE,\n" +
            "TIME_ZONE,\n" +
            "COLLATION_CONNECTION,\n" +
            "ON_COMPLETION,\n" +
            "EXECUTE_AT,\n" +
            "EVENT_SCHEMA,\n" +
            "EVENT_COMMENT,\n" +
            "STARTS,\n" +
            "EVENT_BODY,\n" +
            "DATABASE_COLLATION,\n" +
            "CREATED,\n" +
            "INTERVAL_VALUE,\n" +
            "EVENT_NAME,\n" +
            "ORIGINATOR,\n" +
            "ENDS,\n" +
            "EVENT_DEFINITION,\n" +
            "LAST_ALTERED,\n" +
            "INTERVAL_FIELD,\n" +
            "DEFINER,\n" +
            "LOGFILE_GROUP_NAME,\n" +
            "UPDATE_TIME,\n" +
            "TABLE_ROWS,\n" +
            "CREATION_TIME,\n" +
            "UPDATE_COUNT,\n" +
            "FILE_NAME,\n" +
            "INDEX_LENGTH,\n" +
            "TRANSACTION_COUNTER,\n" +
            "INITIAL_SIZE,\n" +
            "LOGFILE_GROUP_NUMBER,\n" +
            "CHECK_TIME,\n" +
            "AVG_ROW_LENGTH,\n" +
            "LAST_UPDATE_TIME,\n" +
            "FREE_EXTENTS,\n" +
            "FILE_TYPE,\n" +
            "DATA_FREE,\n" +
            "VERSION,\n" +
            "MAXIMUM_SIZE,\n" +
            "FULLTEXT_KEYS,\n" +
            "CHECKSUM,\n" +
            "DATA_LENGTH,\n" +
            "LAST_ACCESS_TIME,\n" +
            "TOTAL_EXTENTS,\n" +
            "TABLESPACE_NAME,\n" +
            "CREATE_TIME,\n" +
            "ROW_FORMAT,\n" +
            "AUTOEXTEND_SIZE,\n" +
            "DELETED_ROWS,\n" +
            "FILE_ID,\n" +
            "MAX_DATA_LENGTH,\n" +
            "RECOVER_TIME,\n" +
            "EXTENT_SIZE,\n" +
            "VARIABLE_VALUE,\n" +
            "VARIABLE_NAME,\n" +
            "BLOCK_ID,\n" +
            "PAGE_STATE,\n" +
            "NUMBER_RECORDS,\n" +
            "PAGE_TYPE,\n" +
            "IO_FIX,\n" +
            "NEWEST_MODIFICATION,\n" +
            "POOL_ID,\n" +
            "DATA_SIZE,\n" +
            "FLUSH_TYPE,\n" +
            "IS_OLD,\n" +
            "OLDEST_MODIFICATION,\n" +
            "LRU_POSITION,\n" +
            "COMPRESSED_SIZE,\n" +
            "FIX_COUNT,\n" +
            "FREE_PAGE_CLOCK,\n" +
            "ACCESS_TIME,\n" +
            "PAGE_NUMBER,\n" +
            "COMPRESSED,\n" +
            "IS_HASHED,\n" +
            "PAGES_READ_RATE,\n" +
            "PAGES_MADE_YOUNG,\n" +
            "DATABASE_PAGES,\n" +
            "LRU_IO_TOTAL,\n" +
            "HIT_RATE,\n" +
            "NUMBER_PAGES_READ,\n" +
            "PENDING_READS,\n" +
            "NUMBER_READ_AHEAD_EVICTED,\n" +
            "PAGES_CREATE_RATE,\n" +
            "PAGES_NOT_MADE_YOUNG,\n" +
            "OLD_DATABASE_PAGES,\n" +
            "LRU_IO_CURRENT,\n" +
            "YOUNG_MAKE_PER_THOUSAND_GETS,\n" +
            "NUMBER_PAGES_CREATED,\n" +
            "PENDING_FLUSH_LRU,\n" +
            "POOL_SIZE,\n" +
            "READ_AHEAD_RATE,\n" +
            "PAGES_WRITTEN_RATE,\n" +
            "PAGES_MADE_YOUNG_RATE,\n" +
            "MODIFIED_DATABASE_PAGES,\n" +
            "UNCOMPRESS_TOTAL,\n" +
            "NOT_YOUNG_MAKE_PER_THOUSAND_GETS,\n" +
            "NUMBER_PAGES_WRITTEN,\n" +
            "PENDING_FLUSH_LIST,\n" +
            "FREE_BUFFERS,\n" +
            "READ_AHEAD_EVICTED_RATE,\n" +
            "NUMBER_PAGES_GET,\n" +
            "PAGES_MADE_NOT_YOUNG_RATE,\n" +
            "PENDING_DECOMPRESS,\n" +
            "UNCOMPRESS_CURRENT,\n" +
            "NUMBER_PAGES_READ_AHEAD,\n" +
            "uncompress_time,\n" +
            "compress_ops_ok,\n" +
            "compress_time,\n" +
            "page_size,\n" +
            "uncompress_ops,\n" +
            "compress_ops,\n" +
            "pages_free,\n" +
            "relocation_ops,\n" +
            "relocation_time,\n" +
            "pages_used,\n" +
            "database_name,\n" +
            "KEY,\n" +
            "value,\n" +
            "DOC_ID,\n" +
            "buffer_pool_instance,\n" +
            "LAST_DOC_ID,\n" +
            "DOC_COUNT,\n" +
            "WORD,\n" +
            "POSITION,\n" +
            "FIRST_DOC_ID,\n" +
            "lock_table,\n" +
            "lock_rec,\n" +
            "lock_trx_id,\n" +
            "lock_index,\n" +
            "lock_data,\n" +
            "lock_mode,\n" +
            "lock_space,\n" +
            "lock_type,\n" +
            "lock_page,\n" +
            "lock_id,\n" +
            "requesting_trx_id,\n" +
            "requested_lock_id,\n" +
            "blocking_trx_id,\n" +
            "blocking_lock_id,\n" +
            "TYPE,\n" +
            "MAX_COUNT_RESET,\n" +
            "TIME_DISABLED,\n" +
            "MIN_COUNT,\n" +
            "MIN_COUNT_RESET,\n" +
            "SUBSYSTEM,\n" +
            "TIME_ELAPSED,\n" +
            "AVG_COUNT,\n" +
            "AVG_COUNT_RESET,\n" +
            "COUNT,\n" +
            "TIME_RESET,\n" +
            "COUNT_RESET,\n" +
            "TIME_ENABLED,\n" +
            "MAX_COUNT,\n" +
            "MTYPE,\n" +
            "PRTYPE,\n" +
            "LEN,\n" +
            "POS,\n" +
            "SPACE,\n" +
            "PATH,\n" +
            "FOR_NAME,\n" +
            "REF_NAME,\n" +
            "N_COLS,\n" +
            "REF_COL_NAME,\n" +
            "FOR_COL_NAME,\n" +
            "INDEX_ID,\n" +
            "N_FIELDS,\n" +
            "PAGE_NO,\n" +
            "FLAG,\n" +
            "FILE_FORMAT,\n" +
            "ZIP_PAGE_SIZE,\n" +
            "AUTOINC,\n" +
            "TABLE_ID,\n" +
            "CLUST_INDEX_SIZE,\n" +
            "REF_COUNT,\n" +
            "NAME,\n" +
            "OTHER_INDEX_SIZE,\n" +
            "STATS_INITIALIZED,\n" +
            "MODIFIED_COUNTER,\n" +
            "NUM_ROWS,\n" +
            "trx_last_foreign_key_error,\n" +
            "trx_lock_memory_bytes,\n" +
            "trx_weight,\n" +
            "trx_autocommit_non_locking,\n" +
            "trx_isolation_level,\n" +
            "trx_tables_in_use,\n" +
            "trx_started,\n" +
            "trx_adaptive_hash_latched,\n" +
            "trx_rows_locked,\n" +
            "trx_mysql_thread_id,\n" +
            "trx_unique_checks,\n" +
            "trx_tables_locked,\n" +
            "trx_requested_lock_id,\n" +
            "trx_adaptive_hash_timeout,\n" +
            "trx_rows_modified,\n" +
            "trx_query,\n" +
            "trx_id,\n" +
            "trx_foreign_key_checks,\n" +
            "trx_lock_structs,\n" +
            "trx_wait_started,\n" +
            "trx_is_read_only,\n" +
            "trx_concurrency_tickets,\n" +
            "trx_operation_state,\n" +
            "trx_state,\n" +
            "CONSTRAINT_NAME,\n" +
            "REFERENCED_COLUMN_NAME,\n" +
            "POSITION_IN_UNIQUE_CONSTRAINT,\n" +
            "CONSTRAINT_CATALOG,\n" +
            "REFERENCED_TABLE_SCHEMA,\n" +
            "CONSTRAINT_SCHEMA,\n" +
            "REFERENCED_TABLE_NAME,\n" +
            "INSUFFICIENT_PRIVILEGES,\n" +
            "QUERY,\n" +
            "TRACE,\n" +
            "MISSING_BYTES_BEYOND_MAX_MEM_SIZE,\n" +
            "SPECIFIC_NAME,\n" +
            "ROUTINE_TYPE,\n" +
            "PARAMETER_MODE,\n" +
            "SPECIFIC_CATALOG,\n" +
            "PARAMETER_NAME,\n" +
            "SPECIFIC_SCHEMA,\n" +
            "DTD_IDENTIFIER,\n" +
            "PARTITION_COMMENT,\n" +
            "PARTITION_ORDINAL_POSITION,\n" +
            "PARTITION_EXPRESSION,\n" +
            "NODEGROUP,\n" +
            "SUBPARTITION_ORDINAL_POSITION,\n" +
            "SUBPARTITION_EXPRESSION,\n" +
            "PARTITION_NAME,\n" +
            "PARTITION_METHOD,\n" +
            "PARTITION_DESCRIPTION,\n" +
            "SUBPARTITION_NAME,\n" +
            "SUBPARTITION_METHOD,\n" +
            "PLUGIN_LIBRARY,\n" +
            "PLUGIN_LICENSE,\n" +
            "PLUGIN_STATUS,\n" +
            "PLUGIN_LIBRARY_VERSION,\n" +
            "LOAD_OPTION,\n" +
            "PLUGIN_TYPE,\n" +
            "PLUGIN_AUTHOR,\n" +
            "PLUGIN_NAME,\n" +
            "PLUGIN_TYPE_VERSION,\n" +
            "PLUGIN_DESCRIPTION,\n" +
            "PLUGIN_VERSION,\n" +
            "STATE,\n" +
            "DB,\n" +
            "INFO,\n" +
            "COMMAND,\n" +
            "USER,\n" +
            "TIME,\n" +
            "HOST,\n" +
            "PAGE_FAULTS_MINOR,\n" +
            "CONTEXT_VOLUNTARY,\n" +
            "SOURCE_LINE,\n" +
            "MESSAGES_SENT,\n" +
            "DURATION,\n" +
            "SWAPS,\n" +
            "CONTEXT_INVOLUNTARY,\n" +
            "MESSAGES_RECEIVED,\n" +
            "CPU_USER,\n" +
            "SOURCE_FUNCTION,\n" +
            "BLOCK_OPS_IN,\n" +
            "QUERY_ID,\n" +
            "PAGE_FAULTS_MAJOR,\n" +
            "CPU_SYSTEM,\n" +
            "SOURCE_FILE,\n" +
            "BLOCK_OPS_OUT,\n" +
            "SEQ,\n" +
            "UNIQUE_CONSTRAINT_NAME,\n" +
            "MATCH_OPTION,\n" +
            "UNIQUE_CONSTRAINT_CATALOG,\n" +
            "UPDATE_RULE,\n" +
            "UNIQUE_CONSTRAINT_SCHEMA,\n" +
            "DELETE_RULE,\n" +
            "PARAMETER_STYLE,\n" +
            "ROUTINE_CATALOG,\n" +
            "SECURITY_TYPE,\n" +
            "ROUTINE_DEFINITION,\n" +
            "IS_DETERMINISTIC,\n" +
            "ROUTINE_SCHEMA,\n" +
            "ROUTINE_COMMENT,\n" +
            "EXTERNAL_NAME,\n" +
            "SQL_DATA_ACCESS,\n" +
            "ROUTINE_NAME,\n" +
            "EXTERNAL_LANGUAGE,\n" +
            "SQL_PATH,\n" +
            "ROUTINE_BODY,\n" +
            "SCHEMA_NAME,\n" +
            "DEFAULT_CHARACTER_SET_NAME,\n" +
            "DEFAULT_COLLATION_NAME,\n" +
            "CATALOG_NAME,\n" +
            "COLLATION,\n" +
            "NULLABLE,\n" +
            "INDEX_SCHEMA,\n" +
            "CARDINALITY,\n" +
            "INDEX_TYPE,\n" +
            "INDEX_NAME,\n" +
            "SUB_PART,\n" +
            "INDEX_COMMENT,\n" +
            "SEQ_IN_INDEX,\n" +
            "PACKED,\n" +
            "NON_UNIQUE,\n" +
            "TABLE_TYPE,\n" +
            "TABLE_COMMENT,\n" +
            "AUTO_INCREMENT,\n" +
            "TABLE_COLLATION,\n" +
            "CREATE_OPTIONS,\n" +
            "TABLESPACE_TYPE,\n" +
            "NODEGROUP_ID,\n" +
            "TABLESPACE_COMMENT,\n" +
            "CONSTRAINT_TYPE,\n" +
            "ACTION_STATEMENT,\n" +
            "TRIGGER_NAME,\n" +
            "ACTION_REFERENCE_NEW_TABLE,\n" +
            "EVENT_OBJECT_TABLE,\n" +
            "ACTION_ORIENTATION,\n" +
            "EVENT_MANIPULATION,\n" +
            "ACTION_REFERENCE_OLD_ROW,\n" +
            "ACTION_ORDER,\n" +
            "TRIGGER_CATALOG,\n" +
            "ACTION_TIMING,\n" +
            "EVENT_OBJECT_CATALOG,\n" +
            "ACTION_REFERENCE_NEW_ROW,\n" +
            "ACTION_CONDITION,\n" +
            "TRIGGER_SCHEMA,\n" +
            "ACTION_REFERENCE_OLD_TABLE,\n" +
            "EVENT_OBJECT_SCHEMA,\n" +
            "VIEW_DEFINITION,\n" +
            "CHECK_OPTION,\n" +
            "IS_UPDATABLE,\n" +
            "oil_pressure,\n" +
            "yearday,\n" +
            "Timestamp,\n" +
            "Column_priv,\n" +
            "Create_routine_priv,\n" +
            "References_priv,\n" +
            "Select_priv,\n" +
            "Trigger_priv,\n" +
            "Lock_tables_priv,\n" +
            "Create_priv,\n" +
            "Alter_routine_priv,\n" +
            "Index_priv,\n" +
            "Insert_priv,\n" +
            "Create_view_priv,\n" +
            "Drop_priv,\n" +
            "Execute_priv,\n" +
            "Alter_priv,\n" +
            "Update_priv,\n" +
            "Show_view_priv,\n" +
            "Grant_priv,\n" +
            "Event_priv,\n" +
            "Create_tmp_table_priv,\n" +
            "Delete_priv,\n" +
            "db_collation,\n" +
            "body_utf8,\n" +
            "body,\n" +
            "modified,\n" +
            "ret,\n" +
            "dl,\n" +
            "server_id,\n" +
            "event_time,\n" +
            "command_type,\n" +
            "user_host,\n" +
            "argument,\n" +
            "thread_id,\n" +
            "parent_category_id,\n" +
            "url,\n" +
            "help_category_id,\n" +
            "help_keyword_id,\n" +
            "help_topic_id,\n" +
            "example,\n" +
            "stat_value,\n" +
            "sample_size,\n" +
            "last_update,\n" +
            "stat_description,\n" +
            "stat_name,\n" +
            "clustered_index_size,\n" +
            "sum_of_other_index_sizes,\n" +
            "n_rows,\n" +
            "gci,\n" +
            "epoch,\n" +
            "schemaops,\n" +
            "inserts,\n" +
            "orig_server_id,\n" +
            "updates,\n" +
            "orig_epoch,\n" +
            "File,\n" +
            "deletes,\n" +
            "language,\n" +
            "param_list,\n" +
            "returns,\n" +
            "Proc_priv,\n" +
            "Grantor,\n" +
            "Proxied_host,\n" +
            "Proxied_user,\n" +
            "With_grant,\n" +
            "Port,\n" +
            "Server_name,\n" +
            "Socket,\n" +
            "Username,\n" +
            "Wrapper,\n" +
            "Password,\n" +
            "Owner,\n" +
            "Retry_count,\n" +
            "Ssl_cipher,\n" +
            "User_name,\n" +
            "Bind,\n" +
            "Ssl_ca,\n" +
            "Number_of_lines,\n" +
            "Ssl_crl,\n" +
            "Ssl_key,\n" +
            "User_password,\n" +
            "Ignored_server_ids,\n" +
            "Ssl_capath,\n" +
            "Master_log_name,\n" +
            "Ssl_crlpath,\n" +
            "Ssl_verify_server_cert,\n" +
            "Connect_retry,\n" +
            "Uuid,\n" +
            "Ssl_cert,\n" +
            "Master_log_pos,\n" +
            "Enabled_auto_position,\n" +
            "Heartbeat,\n" +
            "Enabled_ssl,\n" +
            "Relay_log_name,\n" +
            "Relay_log_pos,\n" +
            "Sql_delay,\n" +
            "Checkpoint_master_log_pos,\n" +
            "Number_of_workers,\n" +
            "Checkpoint_relay_log_name,\n" +
            "Checkpoint_seqno,\n" +
            "Checkpoint_relay_log_pos,\n" +
            "Checkpoint_group_size,\n" +
            "Checkpoint_master_log_name,\n" +
            "Checkpoint_group_bitmap,\n" +
            "rows_sent,\n" +
            "sql_text,\n" +
            "start_time,\n" +
            "rows_examined,\n" +
            "query_time,\n" +
            "last_insert_id,\n" +
            "lock_time,\n" +
            "insert_id,\n" +
            "Table_priv,\n" +
            "Use_leap_seconds,\n" +
            "Time_ZONE_id\n";
}


