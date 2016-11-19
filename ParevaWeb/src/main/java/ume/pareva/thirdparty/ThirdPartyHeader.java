/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.thirdparty;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.zadoi.service.ZaDoi;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.dao.DoiResponseLogDao;
import ume.pareva.dao.DoiResult;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SmsDoiLogDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.dao.WAPDoiLogDao;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.SdcLanguageProperty;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.SmsDoiRequest;
import ume.pareva.pojo.SmsDoiResponse;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.pojo.WapDoiRequest;
import ume.pareva.pojo.WapDoiResponse;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.servlet.Check24Hour;
import ume.pareva.smsservices.SmsService;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.CheckStop;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.UserAuthentication;
import ume.pareva.userservice.VideoList;
import ume.pareva.util.ZACPA;

/**
 *
 * @author madan
 */
public class ThirdPartyHeader extends HttpServlet {
}
