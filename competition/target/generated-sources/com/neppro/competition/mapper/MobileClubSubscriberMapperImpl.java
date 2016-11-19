package com.neppro.competition.mapper;

import com.neppro.competition.dto.MobileClubSubscriptionRequest;
import com.neppro.competition.dto.MobileClubSubscriptionResponse;
import com.neppro.competition.model.MobileClub;
import com.neppro.competition.model.MobileClubSubscriber;
import com.neppro.competition.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2016-11-19T13:01:34+0545",
    comments = "version: 1.0.0.Final, compiler: javac, environment: Java 1.7.0_79 (Oracle Corporation)"
)
@Component
public class MobileClubSubscriberMapperImpl extends MobileClubSubscriberMapper {

    @Autowired
    private MsisdnToUserMapper msisdnToUserMapper;
    @Autowired
    private MobileClubIdToMobileClubMapper mobileClubIdToMobileClubMapper;

    @Override
    public MobileClubSubscriptionResponse mobileClubSubscriberToMobileClubSubscriptionResponse(MobileClubSubscriber mobileClubSubscriber) {
        if ( mobileClubSubscriber == null ) {
            return null;
        }

        MobileClubSubscriptionResponse mobileClubSubscriptionResponse = new MobileClubSubscriptionResponse();

        mobileClubSubscriptionResponse.setMobileClub( mobileClubSubscriberMobileClubId( mobileClubSubscriber ) );
        mobileClubSubscriptionResponse.setMsisdn( mobileClubSubscriberUserMsisdn( mobileClubSubscriber ) );
        if ( mobileClubSubscriber.getActive() != null ) {
            mobileClubSubscriptionResponse.setActive( mobileClubSubscriber.getActive() );
        }
        mobileClubSubscriptionResponse.setCreatedDate( xmlGregorianCalendarToString( dateToXmlGregorianCalendar( mobileClubSubscriber.getCreatedDate() ) , "yyyy-MM-dd hh:mm:ss" ) );
        mobileClubSubscriptionResponse.setSubscriptionId( mobileClubSubscriber.getId() );

        return mobileClubSubscriptionResponse;
    }

    @Override
    public MobileClubSubscriber mobileClubSubscriptionRequestToMobileClubSubscriber(MobileClubSubscriptionRequest mobileClubSubscriptionRequest) {
        if ( mobileClubSubscriptionRequest == null ) {
            return null;
        }

        MobileClubSubscriber mobileClubSubscriber = new MobileClubSubscriber();

        mobileClubSubscriber.setMobileClub( mobileClubIdToMobileClubMapper.MobileClubIdToMobileClub( mobileClubSubscriptionRequest.getMobileClub() ) );
        mobileClubSubscriber.setUser( msisdnToUserMapper.msisdnToUser( mobileClubSubscriptionRequest.getMsisdn() ) );

        mobileClubSubscriber.setId( String.valueOf(new java.util.Date().getTime()) );
        mobileClubSubscriber.setActive( new java.lang.Boolean(false) );
        mobileClubSubscriber.setCreatedDate( new java.sql.Timestamp(new java.util.Date().getTime()) );

        return mobileClubSubscriber;
    }

    private String xmlGregorianCalendarToString( XMLGregorianCalendar xcal, String dateFormat ) {
        if ( xcal == null ) {
            return null;
        }

        if (dateFormat == null ) {
            return xcal.toString();
        }
        else {
            Date d = xcal.toGregorianCalendar().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
            return sdf.format( d );
        }
    }
    private XMLGregorianCalendar dateToXmlGregorianCalendar( Date date ) {
        if ( date == null ) {
            return null;
        }

        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime( date );
            return DatatypeFactory.newInstance().newXMLGregorianCalendar( c );
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }
    private String mobileClubSubscriberMobileClubId(MobileClubSubscriber mobileClubSubscriber) {

        if ( mobileClubSubscriber == null ) {
            return null;
        }
        MobileClub mobileClub = mobileClubSubscriber.getMobileClub();
        if ( mobileClub == null ) {
            return null;
        }
        String id = mobileClub.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String mobileClubSubscriberUserMsisdn(MobileClubSubscriber mobileClubSubscriber) {

        if ( mobileClubSubscriber == null ) {
            return null;
        }
        User user = mobileClubSubscriber.getUser();
        if ( user == null ) {
            return null;
        }
        String msisdn = user.getMsisdn();
        if ( msisdn == null ) {
            return null;
        }
        return msisdn;
    }
}
