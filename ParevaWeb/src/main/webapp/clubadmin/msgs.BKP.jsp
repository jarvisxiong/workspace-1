
<%@include file="/WEB-INF/jspf/clubadmin/msgs.jspf"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
    <link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
</head>
<body>

    <form method="post" action="clubadmin/msgs.jsp">
    <input type="hidden" name="unq" value="<%=unq%>">

<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">
            <table cellspacing="0" cellpadding="5" border="0" width="100%">
                <tr>
                    <td align="left" valign="bottom" class="blue_14"><b>Mobile Club Details</b></td>
                    <td align="right" valign="bottom" class="red_11"><nobr><%=statusMsg%></nobr></td>
                </tr>
            </table>
</td></tr>

<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "0")); %>
    <%@ include file="/clubadmin/tabs.jsp" %>
    <br>
</td></tr>
<tr><td><img src="/images/glass_dot.gif" height="20" width="1"></td></tr>
<tr><td>


    <table cellspacing="0" cellpadding="5" border="0" width="100%">
      
        <tr>
            <td align="left" class="grey_11" valign="top">Billable Message 1:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aWebConfirmation" style="width:300px; height:60px;"><%=item.getWebConfirmation()%></textarea>
            <span class="count_display" id="count_aWebConfirmation"></span></td>
        </tr>
        <tr>
            <td align="left" class="grey_11" valign="top">Billable Message 2:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aSmsConfirmation" style="width:300px; height:60px;"><%=item.getSmsConfirmation()%></textarea>
            <span class="count_display" id="count_aSmsConfirmation"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Billable Message 3:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="billingmessage3" style="width:300px; height:60px;"><%=billablemessage3%></textarea>
            <span class="count_display" id="count_billingmessage3"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Welcome SMS:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aWelcomeSms" style="width:300px; height:60px;"><%=item.getWelcomeSms()%></textarea>
            <span class="count_display" id="count_aWelcomeSms"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Welcome SMS 2:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aWelcomeSms2" style="width:300px; height:60px;"><%=timewelcomesms%></textarea>
            <span class="count_display" id="count_aWelcomeSms2"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Reminder SMS:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aReminderSms" style="width:300px; height:60px;"><%=item.getReminderSms()%></textarea>
            <span class="count_display" id="count_aReminderSms"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Stop SMS:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aStopSms" style="width:300px; height:60px;"><%=item.getStopSms()%></textarea>
            <span class="count_display" id="count_aStopSms"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Follow Up SMS:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aFollowUpSms" style="width:300px; height:60px;"><%=followupMsg%></textarea>
            <span class="count_display" id="count_aFollowUpSms"></td>
        </tr>
        <tr>
            <td align="left" class="grey_11" valign="top">Follow up sms URL:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aFollowUpSmsURL" style="width:300px; height:60px;"><%=followupMsgURL%></textarea>
            <span class="count_display" id="count_aFollowUpSmsURL"></td></td>
        </tr>
        
         <tr>
            <td align="left" class="grey_11" valign="top">Teaser:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="teaser" style="width:300px; height:60px;"><%=teaser%></textarea>
            <span class="count_display" id="count_teaser"></td>
            
            
<!--            <td align="left" class="grey_11"><textarea class="countlength" name="freeday" style="width:30px; height:20px;"><%=freedays%></textarea>
                <span class="count_display" id="count_freeday"></td>
            <td align="left" class="grey_11" valign="top">(Free Days)</td>-->
            
            
        </tr>
         </table>
            </td></tr>
            
            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
            <tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
            <tr><td align="right"><input type="submit" name="save" value="Save" style="width:150px;"></td></tr>
            
        </table>

    </form>
    <script>
        $(document).ready(function() {
            $(".countlength").each( function() {
                $("#count_" + $(this).attr("name")).text($(this).val().length);
            });
        });

        $(".countlength").keyup(function() {
            $("#count_" + $(this).attr("name")).text($(this).val().length);
        });
            

    </script>
</body>
</html>
