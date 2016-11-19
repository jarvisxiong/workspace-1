package ume.pareva.it;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSmsDao;

@Component("digitapiit")
public class DigitAPI {

    @Autowired
    private UmeSmsDao umesmsdao;
    
    private static final String client_id = "b5328f97-c154-463d-9840-9c0de3392ec5";
    private static final String secret_key = "b5843fb263b8c1d169b3ce3c16355a07121b929b";
    public String submission_id;
    private String submission_datetime;

    private String submission_signature = "";
    private String msg;
    private String network;
    private String msisdn;
    private String from;
    private String tariff = "BULK";
    private String split = "0";
    private String tag = "CampaignX";
    private String note = "";
    private String report = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String clubUnique = "";
    private String campaignUnique = "";

    public String getClubUnique() {
        return clubUnique;
    }

    public void setClubUnique(String clubUnique) {
        this.clubUnique = clubUnique;
    }

    public String getCampaignUnique() {
        return campaignUnique;
    }

    public void setCampaignUnique(String campaignUnique) {
        this.campaignUnique = campaignUnique;
    }
    


//    public UmeSmsDao getUmesmsdao() {
//        return umesmsdao;
//    }
//
//    public void setUmesmsdao(UmeSmsDao umesmsdao) {
//        this.umesmsdao = umesmsdao;
//    }

    public String getClient_id() {
        return client_id;
    }

    public String getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(String submission_id) {
        this.submission_id = submission_id;
    }

    public String getSubmission_datetime() {
        return submission_datetime;
    }

    public void setSubmission_signature(String submission_signature) {
        this.submission_signature = submission_signature;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "DigitAPI{" + "client_id=" + client_id + ", submission_id="
                + submission_id + ", submission_datetime="
                + submission_datetime + ", submission_signature="
                + submission_signature + ", msg=" + msg + ", network="
                + network + ", msisdn=" + msisdn + ", from=" + from
                + ", tariff=" + tariff + ", split=" + split + ", tag=" + tag
                + ", note=" + note + ", report=" + report + '}';
    }

    // This is the method which sends sms...
    public void sendSMS() throws Exception {
        UUID submission_id = UUID.randomUUID();
        String digitparam = "";
//		String response = "";
//		String res = "";
        // this is the digitapi url where you send request..
        String digiturl = "http://sms.digifi.it/v1/send" + "";
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.HOUR, -4);
        Date currentDate = cal.getTime();
        submission_datetime = sdf.format(currentDate);
        System.out.println(submission_datetime);

        // You are setting other parameters for http request
        submission_signature = generateSHA1(secret_key + "+" + client_id + "+"
                + submission_id.toString() + "+" + submission_datetime + "+"
                + secret_key);
        System.out.println("Signature generated " + submission_signature);
        try {
            digitparam = "?client_id=" + java.net.URLEncoder.encode(client_id, "UTF-8");
            digitparam += "&submission_id=" + java.net.URLEncoder.encode(submission_id.toString(), "UTF-8");
            digitparam += "&submission_datetime=" + java.net.URLEncoder.encode(submission_datetime, "UTF-8");
            digitparam += "&submission_signature=" + java.net.URLEncoder.encode(submission_signature, "UTF-8");
            digitparam += "&msg=" + java.net.URLEncoder.encode(msg, "UTF-8");
            digitparam += "&msisdn=" + java.net.URLEncoder.encode(msisdn, "UTF-8");
            digitparam += "&from=" + java.net.URLEncoder.encode(from, "UTF-8");
            digitparam += "&tariff=" + java.net.URLEncoder.encode(tariff, "UTF-8");
            digitparam += "&split=" + java.net.URLEncoder.encode(split.toString(), "UTF-8");
            digitparam += "&tag=" + java.net.URLEncoder.encode(tag.toString(), "UTF-8");
            digitparam += "&note=" + java.net.URLEncoder.encode(note.toString(), "UTF-8");
            digitparam += "&report=" + java.net.URLEncoder.encode(report.toString(), "UTF-8");

            final String finalrequesturl = digiturl + digitparam;
            System.out.println(finalrequesturl);

            System.out.println(submission_datetime);
			// ==========================
            // connection with the http url, so now you are requesting that
            // digitapi URL
            // with parameter

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            executorService.execute(new Runnable() {
                public void run() {
                    try {

                        try {
                            SdcSmsSubmit sms = new SdcSmsSubmit();
                            sms.setSmsAccount("DigitAPI");
                            sms.setUnique(SdcMisc.generateUniqueId());
                            sms.setLogUnique(SdcMisc.generateUniqueId("SDK"));
                            sms.setToNumber(msisdn);
//                            sms.setFromNumber(from);
                            sms.setMsgType("sms");
                            sms.setMsgBody(msg);
                            sms.setMsgCode1(sms.getLogUnique());
                            sms.setCampaignUnique(campaignUnique);
                            sms.setClubUnique(clubUnique);
                            sms.setCreated(new Date());
                            
                            String messagelogUnique = umesmsdao.log(sms);
//SdcSmsDao.log((SdcSmsSubmit)sms);
                            System.out.println("MessageLogUnique: " + messagelogUnique+ " sms creation date "+sms.getCreated());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        URL digitrequesturl = new URL(finalrequesturl);
                        HttpURLConnection digitsmsRequest = (HttpURLConnection) digitrequesturl
                                .openConnection();
                        // digitsmsRequest.setDoOutput(true);
                        digitsmsRequest.setRequestMethod("GET");
                        digitsmsRequest.setReadTimeout(15 * 1000);
                        digitsmsRequest.connect();

						// After successful connection it provides us responsecode and
                        // responsedesc
                        String code = digitsmsRequest.getResponseCode() + "";
                        String desc = digitsmsRequest.getResponseMessage();
                        //digitsmsRequest.get
                        InputStream is;
                        if (digitsmsRequest.getResponseCode() >= 400) {
                            is = digitsmsRequest.getErrorStream();
                        } else {
                            is = digitsmsRequest.getInputStream();
                        }

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(is));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        String resp = response.toString();
                        //System.out.println("SMS BULK sending: " + new Date() + " - RESPONSE: " + resp);
                        System.out.println("SMS BULK sending: " + msisdn + "---" + new Date() + " - RESPONSE: " + resp);

                        System.out.println("Response code: " + code + desc);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });
            executorService.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // SHA encryption or decryption algorithm is going to be used.
    public static String generateSHA1(String params)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        // encrypts using the SHA-1 algorithm
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        // Clears all the values that MessageDigest is occupied with
        crypt.reset();
        // used for encryption
        crypt.update(params.getBytes("UTF-8"));

        return new BigInteger(1, crypt.digest()).toString(16);
    }
}
