package com.stock.app.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.opencsv.CSVReader;
import com.stock.app.domain.DailyPrice;
import com.stock.app.domain.MainStock;
import com.stock.app.domain.TWT38U;
import com.stock.app.domain.WeeklyHolder;
import com.stock.app.repository.DailyPriceRepository;
import com.stock.app.repository.MainStockRepository;
import com.stock.app.repository.TWT38URepository;
import com.stock.app.repository.WeeklyHolderRepository;
import com.stock.app.service.dto.StockDay;
import com.stock.app.web.rest.MainStockResource;
import liquibase.util.csv.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.apache.http.impl.client.CloseableHttpClient;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.net.ssl.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class SyncStockDataService {

    private final Logger log = LoggerFactory.getLogger(SyncStockDataService.class);

    @Autowired
    MainStockRepository mainStockRepository;

    @Autowired
    DailyPriceRepository dailyPriceRepository;

    @Autowired
    WeeklyHolderRepository weeklyHolderRepository;

    @Autowired
    TWT38URepository twT38URepository;

    @Scheduled(fixedDelay = 10000000, initialDelay = 1000)
    public void SyncStockDay() throws NoSuchAlgorithmException, KeyManagementException, InterruptedException {
        long now = System.currentTimeMillis() / 1000;
        System.out.println(
            "Fixed rate task with one second initial delay - " + now);

        List<MainStock> mainStockList = mainStockRepository.findAll();
        log.debug("query stock size: {}", mainStockList.size());

        LocalDate startDate = LocalDate.parse("2020-01-01"),
            endDate   = LocalDate.now();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            String queryStartDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            //mainStockList.forEach(x -> doPost(x.getNo(), queryStartDate));
            //mainStockList.forEach(x -> doGet(x.getNo(), queryStartDate));

            //doGetTWT38U(queryStartDate);
            doGetMainInfo();
            Thread.sleep(3000);
        }



    }
    public void doGetMainInfo() {
        HttpURLConnection con = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        DataOutputStream dataOutputStream = null;
        File srcFile = null;

        String url = "http://mopsfin.twse.com.tw/opendata/t187ap03_L.csv";
        url = "https://quality.data.gov.tw/dq_download_json.php?nid=18419&md5_url=9791ec942cbcb925635aa5612ae95588";
        log.info("fetch {}", url);
        URL obj = null;
        try {

            HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(getHostnameVerifier());
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.connect();
            is = con.getInputStream();
            log.info("int {}",is.read());
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
            log.info("int {}",br.read());
            String line = "";
            while((line = br.readLine()) != null) {
                //System.out.println(line);
                //log.info("{}", line.length());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                List<Map<String,Object>> result =
                objectMapper.readValue(line, List.class);
                ;
                log.info("{}", result.get(0));

            }

            isr.close();
            is.close();
            br.close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br !=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }

    }

    public void doGet(String stockNo, String queryDate) {

        HttpURLConnection con = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        DataOutputStream dataOutputStream = null;
        File srcFile = null;

        if (dailyPriceRepository.findByNoAndDate(stockNo, queryDate) != null) {
            return ;
        }
        String url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date="+queryDate+"&stockNo=" + stockNo;
        log.info("fetch {}", url);
        URL obj = null;
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(getHostnameVerifier());
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.connect();
            is = con.getInputStream();
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);

            String line = "";
            while((line = br.readLine()) != null) {
                //System.out.println(line);
                JSONObject jsonObject = new JSONObject(line);
                StockDay stockDay = new ObjectMapper().readValue(line, StockDay.class);

                DailyPrice dp = new DailyPrice();
                dp.setNo(stockNo);

                if (stockDay != null && StringUtils.equals(stockDay.getStat(), "OK")) {
                    for(String[] a : stockDay.getData()) {

                        log.info("a[7] = {}", a[7]);
                        dp.setDate(a[0].replaceAll("/","").replaceAll(",", ""));
                        dp.setTxnNumber(new BigDecimal(a[1].trim().replaceAll("/","").replaceAll(",", "")));
                        dp.setTxnAmount(new BigDecimal(a[2].trim().replaceAll("/","").replaceAll(",", "")));
                        dp.setStartPrice(new BigDecimal(a[3].trim().trim().replaceAll("/","").replaceAll(",", "")));
                        dp.setHighPrice(new BigDecimal(a[4].trim().replaceAll("/","").replaceAll(",", "")));
                        dp.setLowPrice(new BigDecimal(a[5].trim().replaceAll("/","").replaceAll(",", "")));
                        dp.setEndPrice(new BigDecimal(a[6].trim().replaceAll("/","").replaceAll(",", "")));

                        dp.setHighLowPrice(new BigDecimal(a[7].trim().replaceAll("/","").replaceAll(",", "").replaceAll("X","")));
                        dp.setTxnCount(new BigDecimal(a[8].trim().replaceAll("/","").replaceAll(",", "")));

                        //log.info("-->{}",dp);
                        DailyPrice dprice = dailyPriceRepository.findByNoAndDate(dp.getNo(), dp.getDate());
                        if (dprice == null) {
                            dprice = dailyPriceRepository.save(dp);
                            //log.info("{}", dprice.toString());
                        }
                    }
                    //log.debug("{}", stockDay);
                }
            }
            isr.close();
            is.close();
            br.close();
            con.disconnect();
        } catch (Exception e) {
            log.info("Error info {}, {}", stockNo, queryDate);
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br !=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }

    }
    public void doPost(String stockNo, String queryDate) {
        HttpURLConnection con = null;
        String url = "https://www.tdcc.com.tw/smWeb/QryStockAjax.do";
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        DataOutputStream dataOutputStream = null;
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(getHostnameVerifier());
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con = this.commonConnectionBuilder(con);
            String postDataStr = "scaDates="+queryDate+"&scaDate="+queryDate+"&SqlMethod=StockNo&StockNo="+stockNo+
                "&StockName=&REQ_OPR=SELECT&clkStockNo="+stockNo+"&clkStockName=";//this.getQryParamData(viewState, eventValidation).toString();

            log.info("fetch {}", postDataStr);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", String.valueOf(postDataStr.length()));
            con.setUseCaches(true);
            con.setDoOutput(true);

            dataOutputStream = new DataOutputStream(con.getOutputStream());
            dataOutputStream.writeBytes(postDataStr);
            dataOutputStream.flush();
            is = con.getInputStream();
            //System.out.println(con.getResponseCode());
            //System.out.println("available-->" + is.available());
            if (con.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String text = new BufferedReader(
                    new InputStreamReader(is, "BIG5"))
                    .lines()
                    .collect(Collectors.joining("\n"));
                //System.out.println("text-->" + text);

                Document doc = Jsoup.parse(text);
                //log.info("-->{}", doc.select("font").get(0).text());
                if ("無此資料".equals(doc.select("font").get(0).text())) {
                    log.info("無此資料 {}, {}", stockNo, queryDate);
                } else {
                    Elements desktopOnly = doc.getElementsByClass("bw09");
                    //System.out.println("text-->" + desktopOnly.text());
                    for (Element table : doc.getElementsByClass("mt")) {
                        for (Element row : table.select("tr")) {
                            Elements tds = row.select("td");
                            if (tds.size() > 3) {
                                if (!StringUtils.equals("序", tds.get(0).text()) && !StringUtils.equals("合　計", tds.get(1).text()) && !StringUtils.equals("", tds.get(2).text())) {
                                    log.info("{}",tds.get(0).text() + ":" + tds.get(1).text()+ ":" + tds.get(2).text()+ ":" + tds.get(3).text()+ ":" + tds.get(4).text());
                                    WeeklyHolder wh = new WeeklyHolder();
                                    wh.setNo(stockNo);
                                    wh.setDate(queryDate);
                                    wh.setLevel(new BigDecimal(tds.get(0).text()));
                                    wh.setLevelName(tds.get(1).text());
                                    wh.setSum_people(new BigDecimal(tds.get(2).text().replaceAll(",", "")));
                                    wh.setSum_stock(new BigDecimal(tds.get(3).text().replaceAll(",", "")));
                                    wh.setPercentage(new BigDecimal(tds.get(4).text().replaceAll(",", "")));
                                    if (!StringUtils.equals("合　計", tds.get(2).text())) {
                                        if (weeklyHolderRepository.findByNoAndDateAndLevel(stockNo, queryDate, wh.getLevel() ) == null) {
                                            weeklyHolderRepository.save(wh);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                log.error("connect url {}, ResponseCode({}) ", url, con.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void doGetTWT38U(String queryDate) {

        HttpURLConnection con = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        DataOutputStream dataOutputStream = null;
        File srcFile = null;


        String url = "https://www.twse.com.tw/fund/TWT38U?response=json&date="+ queryDate +"&_=1619495026702";
        log.info("fetch {}", url);
        URL obj = null;
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(getHostnameVerifier());
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.connect();
            is = con.getInputStream();
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);

            String line = "";
            while((line = br.readLine()) != null) {
                //System.out.println(line);
                JSONObject jsonObject = new JSONObject(line);
                StockDay stockDay = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(line, StockDay.class);
                if (stockDay != null && StringUtils.equals(stockDay.getStat(), "OK")) {

                    for(String[] a : stockDay.getData()) {
                        TWT38U twt38u = new TWT38U();
                        twt38u.setDate(queryDate);
                        twt38u.setNo(a[1]);
                        twt38u.setName(a[2]);
                        twt38u.setForeignChinaBuyNumberNoself(new BigDecimal(a[3].replaceAll(",","")));
                        twt38u.setForeignChinaSellNumberNoself(new BigDecimal(a[4].replaceAll(",","")));
                        twt38u.setForeignChinaBuyOrSellNumberNoself(new BigDecimal(a[5].replaceAll(",","")));

                        twt38u.setForeignBuyNumber(new BigDecimal(a[6].replaceAll(",","")));
                        twt38u.setForeignSellNumber(new BigDecimal(a[7].replaceAll(",","")));
                        twt38u.setForeignBuyOrSellNumber(new BigDecimal(a[8].replaceAll(",","")));

                        twt38u.setForeignChinaBuyNumber(new BigDecimal(a[9].replaceAll(",","")));
                        twt38u.setForeignChinaSellNumber(new BigDecimal(a[10].replaceAll(",","")));
                        twt38u.setForeignChinaBuyOrSellNumber(new BigDecimal(a[11].replaceAll(",","")));
                        //log.info("-->{}",dp);
                        TWT38U twt38uNew = twT38URepository.findByNoAndDate(twt38u.getNo(), twt38u.getDate());
                        if (twt38uNew == null) {
                            twt38u = twT38URepository.save(twt38u);

                        }
                    }

                }

            }
            isr.close();
            is.close();
            br.close();
            con.disconnect();
        } catch (Exception e) {
            log.info("Error info {}", queryDate);
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(br !=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }

    }
    public static SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        return sc;
    }
    public static HostnameVerifier getHostnameVerifier() {
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        return allHostsValid;
    }
    private HttpURLConnection commonConnectionBuilder(HttpURLConnection con) {
//        con.setRequestProperty("Origin", "https://dmz26.moea.gov.tw");
//        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
//        con.setRequestProperty("DNT", "1");

        con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        con.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36");
        con.setRequestProperty("Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//        con.setRequestProperty("Referer", "https://dmz26.moea.gov.tw/GMWeb/investigate/InvestigateEA.aspx");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        return con;
    }
}
