package com.nse;

import com.nse.model.AnnonuncementPage;
import com.nse.model.response.Annonuncements;
import com.nse.model.response.AnnonuncementsResult;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


/**
 * Created by srikanth on 16/10/17.
 */
public class NSEService {
    private static final Logger LOG = LoggerFactory.getLogger(NSEService.class);

    private HttpClient  client;
    private ObjectMapper mapper = new ObjectMapper();

    public NSEService(){
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).setSocketTimeout(30 * 1000).build();
        this.client  = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    public AnnonuncementsResult getLatestAnnouncements(){
        AnnonuncementsResult resultListt = null;
        String url = "https://www.nseindia.com/corporates/directLink/latestAnnouncementsCorpHome.jsp?start=0&limit=20";
        HttpGet get = new HttpGet(url);
        try {
            Header header = new BasicHeader("content-type","application/json");
            HttpResponse res = client.execute(get);

            InputStream inputStream = res.getEntity().getContent();
            BufferedReader br =new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line =null;
            while ((line=br.readLine())!=null){
                buffer.append(line);
            }
            String s = buffer.toString();
            s = s.replace("company:", "\"company\":");
            s = s.replace("symbol:", "\"symbol\":");
            s = s.replace("desc:", "\"desc\":");
            s = s.replace("link:", "\"link\":");
            s = s.replace("date:", "\"date\":");


            if(res.getStatusLine().getStatusCode()==200) {
                resultListt = mapper.readValue(s, AnnonuncementsResult.class);
            }
            LOG.info("getting results: {}",res.getStatusLine());
            inputStream.close();
            br.close();
        } catch (IOException e) {
            LOG.info("{}",e);
        }

        return resultListt;

    }

    public AnnonuncementPage getAttachementUri(String link) throws IOException {
        AnnonuncementPage annonuncementPage = new AnnonuncementPage();
        String zipUrl ="";
        String url = "https://www.nseindia.com"+link;
        LOG.info("url:{}",url);
        HttpGet get = new HttpGet(url);
        HttpResponse response =client.execute(get);
        LOG.info(response.getStatusLine().toString());

        InputStream content = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(content));
        StringBuffer data= new StringBuffer();
        String line=null;
        while ((line = br.readLine()) != null) {
            data.append(line);
        }
        br.close();
        content.close();

        annonuncementPage.setPage(data.toString());
        org.jsoup.nodes.Document document = Jsoup.parse(data.toString());
         zipUrl = document.select("a").get(1).attr("href");

         try {
             String annoncementDescription = document.select("td").get(14).childNodes().get(0).toString();
             annonuncementPage.setPage(annoncementDescription);
         }catch (Exception e){

         }
         annonuncementPage.setZipUrl(zipUrl);
        return annonuncementPage;
    }


    public void extractZipAndSendEmail(AnnonuncementPage page, Annonuncements annonuncements) {
        URL url = null;
        try {
            url = new URL("https://www.nseindia.com"+page.getZipUrl());
        } catch (MalformedURLException e) {
            LOG.error("unable to open Zip location");
        }

        try {
            InputStream inputStream = url.openConnection().getInputStream();
            ZipInputStream zip = new ZipInputStream(inputStream);
            ZipEntry entry = null;

            while((entry=zip.getNextEntry())!=null){
                extractEntry(entry,zip);
                sendWithFileAttachment(entry.getName(),annonuncements,page);
            }
            zip.close();
            inputStream.close();

        } catch (IOException e) {
            LOG.error("Unable to Open Connection");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private  void extractEntry(final ZipEntry entry, InputStream is) throws IOException {
        String exractedFile = entry.getName();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(exractedFile);
            final byte[] buf = new byte[1024];
            int read = 0;
            int length;
            while ((length = is.read(buf, 0, buf.length)) >= 0) {
                fos.write(buf, 0, length);
            }
        } catch (IOException ioex) {
            fos.close();
        }
    }



    public void sendWithFileAttachment(String fileName, Annonuncements annonuncements, AnnonuncementPage page) throws MessagingException {
        String to="srikanthkann525@yahoo.com";//change accordingly
        final String user="srikanthkanna525@yahoo.com";//change accordingly
        final String password="Snapdeal@123";//change accordingly

        //1) get the session object
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.mail.yahoo.com");
        properties.put("mail.smtp.user", user);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        //2) compose message
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            StringBuffer subject = new StringBuffer().append(annonuncements.getCompany()).append("-").append(annonuncements.getDesc());

            message.setSubject(subject.toString());

            //3) create MimeBodyPart object and set your message content
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(page.getPage());

            //4) create new MimeBodyPart object and set DataHandler object to this object
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();

            DataSource source = new FileDataSource(fileName);
            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(fileName);


            //5) create Multipart object and add MimeBodyPart objects to this object
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            //6) set the multiplart object to the message object
            message.setContent(multipart );

            //7) send message
            Transport transport = session.getTransport("smtp");
            transport.send(message,user,password);
            transport.close();
            System.out.println("message sent....");
        }catch (MessagingException ex) {
            LOG.info("{}",ex);
            ex.printStackTrace();
        }

        }
}



