package com.nse;

import com.nse.model.AnnonuncementPage;
import com.nse.model.response.Annonuncements;
import com.nse.model.response.AnnonuncementsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by srikanth on 16/10/17.
 */
public class AppStart {
    private static final Logger LOG = LoggerFactory.getLogger(AppStart.class);

    public static void main(String[] args) {


        NSEService service =new NSEService();
        AnnonuncementsResult latestAnnouncements = service.getLatestAnnouncements();
        List<Annonuncements> rows = latestAnnouncements.getRows();
        for (Annonuncements annonuncements :rows){
            try {
                LOG.info(annonuncements.toString());
                AnnonuncementPage annonuncementPage = service.getAttachementUri(annonuncements.getLink());
                LOG.info("Extracting zip url:{}",annonuncementPage.getZipUrl());
                service.extractZipAndSendEmail(annonuncementPage,annonuncements);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
}
