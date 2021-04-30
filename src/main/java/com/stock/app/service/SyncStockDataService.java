package com.stock.app.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.opencsv.CSVReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stock.app.domain.DailyPrice;
import com.stock.app.domain.MainStock;
import com.stock.app.domain.TWT38U;
import com.stock.app.domain.WeeklyHolder;
import com.stock.app.repository.DailyPriceRepository;
import com.stock.app.repository.MainStockRepository;
import com.stock.app.repository.TWT38URepository;
import com.stock.app.repository.WeeklyHolderRepository;
import com.stock.app.service.dto.StockDay;
import com.stock.app.service.dto.StockMainInfo;
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

import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import org.apache.http.impl.client.CloseableHttpClient;
import org.w3c.dom.NodeList;

import java.io.*;
import java.lang.reflect.Type;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    /*
    "0 0 12 * * ?"    每天中午十二點觸發
"0 15 10 ? * *"    每天早上10：15觸發
"0 15 10 * * ?"    每天早上10：15觸發
"0 15 10 * * ? *"    每天早上10：15觸發
"0 15 10 * * ? 2005"    2005年的每天早上10：15觸發
"0 * 14 * * ?"    每天從下午2點開始到2點59分每分鐘一次觸發
"0 0/5 14 * * ?"    每天從下午2點開始到2：55分结束每5分鐘一次觸發
"0 0/5 14,18 * * ?"    每天的下午2點至2：55和6點至6點55分

        兩個時間内每5分鐘一次觸發
"0 0-5 14 * * ?"    每天14:00至14:05每分鐘一次觸發
"0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44觸發
"0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15觸發
*/
//    @Scheduled((cron="0 0 */6 * * *"))

    @Scheduled(fixedDelay = 1000000000, initialDelay = 1000)
    public void SyncStockDay() throws NoSuchAlgorithmException, KeyManagementException, InterruptedException {
        long now = System.currentTimeMillis() / 1000;
        System.out.println(
            "Fixed rate task with one second initial delay - " + now);
        doGetMainInfo();
        List<MainStock> mainStockList = mainStockRepository.findAll();
        log.debug("query stock size: {}", mainStockList.size());

        LocalDate startDate = LocalDate.parse("2020-01-01"),
            endDate   = LocalDate.now();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            String queryStartDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            //每天股價
            mainStockList.forEach(x -> doGetDailyStock(x.getNo(), queryStartDate));

            //mainStockList.forEach(x -> doGetDailyStock2(x.getNo(), queryStartDate));
            //大戶持股比
            mainStockList.forEach(x -> doPostQueryStockLevelNumber(x.getNo(), queryStartDate));
            //外資持股比
            doGetTWT38U(queryStartDate);




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
        url = "https://quality.data.gov.tw/dq_download_xml.php?nid=18419&md5_url=9791ec942cbcb925635aa5612ae95588";
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

            String line = "";//br.readLine();
            //System.out.println(line);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse("https://quality.data.gov.tw/dq_download_xml.php?nid=18419&md5_url=9791ec942cbcb925635aa5612ae95588");
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("row");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                org.w3c.dom.Node nNode = nList.item(temp);
                MainStock stockMainInfo = new MainStock();
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;

                    //出表日期
                    eElement.getElementsByTagName("Col1").item(0).getTextContent();

                    //公司代號
                    String no = eElement.getElementsByTagName("Col2").item(0).getTextContent();
                    stockMainInfo.setNo(no);

                    //公司名稱
                    String fullName = eElement.getElementsByTagName("Col3").item(0).getTextContent();
                    stockMainInfo.setFullName(fullName);
                    //、公司簡稱、
                    String name = eElement.getElementsByTagName("Col4").item(0).getTextContent();
                    stockMainInfo.setName(name);

                    //外國企業註冊地國、
                    eElement.getElementsByTagName("Col5").item(0).getTextContent();
                    //產業別、
                    eElement.getElementsByTagName("Col6").item(0).getTextContent();
                    //住址、
                    String address = eElement.getElementsByTagName("Col7").item(0).getTextContent();
                    stockMainInfo.setCompanyAddress(address);
                    //營利事業統一編號、
                    String taxId = eElement.getElementsByTagName("Col8").item(0).getTextContent();
                    stockMainInfo.setTaxid(taxId);
                    //董事長、
                    String ceo = eElement.getElementsByTagName("Col9").item(0).getTextContent();
                    stockMainInfo.setCeo(ceo);
                    //總經理、
                    eElement.getElementsByTagName("Col10").item(0).getTextContent();
                    //發言人、
                    eElement.getElementsByTagName("Col11").item(0).getTextContent();
                    //發言人職稱、
                    eElement.getElementsByTagName("Col12").item(0).getTextContent();
                    //代理發言人、
                    eElement.getElementsByTagName("Col13").item(0).getTextContent();
                    //總機電話、
                    eElement.getElementsByTagName("Col14").item(0).getTextContent();

                    //成立日期、
                    String buildDate = eElement.getElementsByTagName("Col15").item(0).getTextContent();
                    LocalDate lBuildDate = LocalDate.parse(buildDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                    stockMainInfo.setBuildDate(java.util.Date.from(lBuildDate.atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()));
                    //上市日期、
                    String ongoingDate = eElement.getElementsByTagName("Col16").item(0).getTextContent();
                    LocalDate lOngoingDate = LocalDate.parse(ongoingDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                    stockMainInfo.setOngoingDate(java.util.Date.from(lOngoingDate.atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()));
                    //普通股每股面額、
                    eElement.getElementsByTagName("Col17").item(0).getTextContent();

                    //實收資本額、
                    String capital = eElement.getElementsByTagName("Col18").item(0).getTextContent();
                    stockMainInfo.setCapital(new BigDecimal(capital));

                    //私募股數、
                    eElement.getElementsByTagName("Col19").item(0).getTextContent();
                    //特別股、
                    eElement.getElementsByTagName("Col20").item(0).getTextContent();
                    //編制財務報表類型、
                    eElement.getElementsByTagName("Col21").item(0).getTextContent();
                    //股票過戶機構、
                    eElement.getElementsByTagName("Col22").item(0).getTextContent();
                    //過戶電話、
                    eElement.getElementsByTagName("Col23").item(0).getTextContent();
                    //過戶地址、
                    eElement.getElementsByTagName("Col24").item(0).getTextContent();
                    //簽證會計師事務所、
                    eElement.getElementsByTagName("Col25").item(0).getTextContent();
                    //簽證會計師1、
                    eElement.getElementsByTagName("Col26").item(0).getTextContent();
                    //簽證會計師2、
                    eElement.getElementsByTagName("Col27").item(0).getTextContent();
                    //英文簡稱、
                    eElement.getElementsByTagName("Col28").item(0).getTextContent();
                    //英文通訊地址、
                    eElement.getElementsByTagName("Col29").item(0).getTextContent();
                    //傳真機號碼、
                    eElement.getElementsByTagName("Col30").item(0).getTextContent();
                    //電子郵件信箱、
                    eElement.getElementsByTagName("Col31").item(0).getTextContent();
                    //網址
                    eElement.getElementsByTagName("Col32").item(0).getTextContent();
                    log.info("{}",stockMainInfo);
                    if (mainStockRepository.findByNo(stockMainInfo.getNo()) == null) {
                        mainStockRepository.save(stockMainInfo);
                    }
                }
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
    public static final String removeBOM(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;

        }


        if (data.startsWith("\ufeff")) {
            return data.substring(1);

        } else {
            return data;

        }
    }
    public void doGetDailyStock(String stockNo, String queryDate) {

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
            Thread.sleep(3000);
            HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(getHostnameVerifier());
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            con.setRequestProperty("Cache-Control", "max-age=0");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Cookie", "_ga=GA1.1.427047596.1619076056; _ga_F4L5BYPQDJ=GS1.1.1619764416.8.0.1619764416.0; JSESSIONID=82FE4C17F4F61EBE5CFB9544EFFF8EF9");
            con.setRequestProperty("Host", "www.twse.com.tw");
            con.setRequestProperty("Sec-Fetch-Dest", "document");
            con.setRequestProperty("Sec-Fetch-Mode", "navigate");
            con.setRequestProperty("Sec-Fetch-Site", "none");
            con.setRequestProperty("Sec-Fetch-User", "?1");
            con.setRequestProperty("Upgrade-Insecure-Requests", "1");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36");
            //con.setRequestProperty("", "");


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
    public void doGetDailyStock2(String stockNo, String queryDate) {

        HttpURLConnection con = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        DataOutputStream dataOutputStream = null;
        File srcFile = null;

        if (dailyPriceRepository.findByNoAndDate(stockNo, queryDate) != null) {
            return ;
        }
        String url = "https://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=tse_"+stockNo+".tw_"+queryDate+ "&json=1&delay=0";
        log.info("fetch {}", url);
        URL obj = null;
        try {
            Thread.sleep(3000);
            //HttpsURLConnection.setDefaultSSLSocketFactory(getSSLContext().getSocketFactory());
            //HttpsURLConnection.setDefaultHostnameVerifier(getHostnameVerifier());
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
                System.out.println(line);
                /*
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

                 */
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
    public void doPostQueryStockLevelNumber(String stockNo, String queryDate) {
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
