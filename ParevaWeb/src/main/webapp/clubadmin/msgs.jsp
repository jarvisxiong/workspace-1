<%@include file="/WEB-INF/jspf/clubadmin/msgs.jspf"%>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html">
    <style>
      .unimportantHidden {
        display: none;
      } 
      .submitBtn {
        margin-bottom: 30px;
      }
    </style>
</head>
<body>

  <div class="alertHolder"></div> 

  <h3 class="page-title">
      Mobile Club Details - <%=item.getClubName()%> (<%=unq%>)  
  </h3>

  <button data-url='clubDetails.jsp?unq=<%=unq%>' type="button" class="btn btn-outline sbold uppercase jsUrlLoad">
    Properties
  </button>

  <button data-urlNo='msgs.jsp?unq=<%=unq%>' type="button" class="btn dark">
    Messages
  </button>
  
  <button data-url='index.jsp' type="button" class="btn btn-outline sbold uppercase jsUrlLoad">
    Back to all clubs
  </button>

  <div class="statusMsg"><%=statusMsg%></div>

  <div class="row">
              <div class="col-md-12">
                  <div class="tabbable-line boxless tabbable-reversed">
                      <ul class="nav nav-tabs">
                          <li class="active">
                              <a href="#tab_0" data-toggle="tab"> Billable </a>
                          </li>
                          <li>
                              <a href="#tab_1" data-toggle="tab"> Welcome </a>
                          </li>
                          <li>
                              <a href="#tab_2" data-toggle="tab"> Stop </a>
                          </li>
                          <li>
                              <a href="#tab_3" data-toggle="tab"> Reminder </a>
                          </li>
                          <li>
                              <a href="#tab_4" data-toggle="tab"> Follow Up </a>
                          </li>
                          <li>
                              <a href="#tab_5" data-toggle="tab"> Teaser </a>
                          </li>
                          
                      </ul>
                      <div class="tab-content">
                          <div class="tab-pane active" id="tab_0">
                            <div class="portlet box green bordered" data-type="Billable">
                              <div class="portlet-title ">
                                  <div class="caption">
                                      <i class="fa fa-gbp"></i>
                                      <span class="caption-subject bold uppercase">Billable Messages</span>
                                      <span name="numberOfMessages" class="msgsNum" readonly value="<%=billableList.size()%>" ><%=billableList.size()%></span>
                                  </div>
                                  <div class="actions">
                                      <div class="btn-group">
                                          <a class="btn btn-sm bg-white js_AddButton" > Add New
                                              <i class="fa fa-plus"></i>
                                          </a>
                                      </div>
                                  </div>
                              </div>
                              <div class="portlet-body form">
                                  <form role="form" action="../clubadmin/msgs.jsp?unq=<%=unq%>">
                                      <div class="form-body">
                                        <div class="msgsHolder">
                                        <%for(int i=0;i<billableList.size();i++) {
                                        	Map<String,Object> billableMap=billableList.get(i);
                                          UmeClubMessages umeClubMessages=(UmeClubMessages)billableMap.get("message");
                                          Integer messageLength=(Integer)billableMap.get("messageLength");
                                        		  
                                          int j=i+1;
                                        %>

                                          <div class="form-group form-md-line-input form-md-floating-label js_msgHolder">
                                            <div class="input-group">
                                              <div class="input-group-control">
                                                <input  type="hidden" 
                                                        name="Billable_<%=i%>_Unique" 
                                                        value="<%=umeClubMessages.getaUnique()%>" 
                                                        />
                                                <textarea class="form-control edited" rows="1" name="msg_<%=i%>" ><%=umeClubMessages.getaMessage()%></textarea>
                                                <span class="help-block"><%=messageLength%></span>
                                                <label for="form_control_1">Billable Message <%=j%></label>
                                              </div>
                                              <span class="input-group-btn btn-right open">
                                                  <button type="button"
                                                          data-delete="billableMessage_<%=i%>_Delete" 
                                                          data-uniquename="Billable_<%=i%>_Unique"
                                                          data-uniquevalue="<%=umeClubMessages.getaUnique()%>"
                                                          class="btn red js_DeleteMsg"> 
                                                    <i class="fa fa-remove"></i>
                                                    Delete
                                                  </button>
                                              </span>
                                            </div>
                                          </div>
                                        <%} %>
                                        </div>
                                      </div>
                                      <div class="row">
                                          <div class="col-md-offset-3 col-md-6">
                                            <input type="hidden" name="unq" value="<%=unq%>">
                                            <input type="hidden" name="msgType" value="Billable">
                                            <input type="hidden" name="numberOfMessages" class="msgsNum" value="<%=billableList.size()%>">
                                            <input type="hidden" name="saveClubMsg" value="saveClubMsg"  />
                                            <input type="submit" name="saveClubMsg" value="Save" class="btn blue submitBtn col-md-12" />
                                          </div>
                                      </div>
                                  </form>
                              </div>
                            </div>
                          </div>
                          <div class="tab-pane" id="tab_1">
                            <div class="portlet box purple-studio" data-type="Welcome">
                              <div class="portlet-title ">
                                  <div class="caption">
                                      <i class="fa fa-child"></i>
                                      <span class="caption-subject bold uppercase">Welcome Messages </span>
                                      <span name="numberOfMessages" class="msgsNum" readonly value="<%=welcomeList.size()%>" ><%=welcomeList.size()%></span>
                                  </div>
                                  <div class="actions">
                                      <div class="btn-group">
                                          <a class="btn btn-sm bg-white js_AddButton" > Add New
                                              <i class="fa fa-plus"></i>
                                          </a>
                                      </div>
                                  </div>
                              </div>
                              <div class="portlet-body form">

                                  <form role="form" action="..clubadmin/msgs.jsp?unq=<%=unq%>">
                                      <div class="form-body">
                                        <div class="msgsHolder">
                                          <%for(int i=0;i<welcomeList.size();i++) {
                                            Map<String,Object> welcomeMap=welcomeList.get(i);
                                            UmeClubMessages umeClubMessages=(UmeClubMessages)welcomeMap.get("message");
                                            Integer messageLength=(Integer)welcomeMap.get("messageLength");
                                            int j=i+1;
                                          %>
                                          <div class="form-group form-md-line-input form-md-floating-label js_msgHolder">
                                            <div class="input-group">
                                              <div class="input-group-control">
                                                <input  type="hidden" 
                                                        name="Welcome_<%=i%>_Unique" 
                                                        value="<%=umeClubMessages.getaUnique()%>" 
                                                />
                                                <textarea name="msg_<%=i%>"
                                                        class="form-control edited" 
                                                        rows="1" ><%=umeClubMessages.getaMessage()%></textarea>
                                                <span class="help-block"><%=messageLength%> </span>
                                                <label for="form_control_1">
                                                  Welcome Message <%=j%>
                                                </label>
                                              </div>
                                              <span class="input-group-btn btn-right open">
                                                  <button type="button"
                                                          data-delete="welcomeMessage_<%=i%>_Delete" 
                                                          data-uniquename="Welcome_<%=i%>_Unique"
                                                          data-uniquevalue="<%=umeClubMessages.getaUnique()%>"
                                                          class="btn red js_DeleteMsg"> 
                                                    <i class="fa fa-remove"></i>
                                                    Delete
                                                  </button>
                                              </span>
                                            </div>
                                              
                                          </div>
                                        <%} %>
                                        </div>
                                      </div>
                                      <div class="row">
                                          <div class="col-md-offset-3 col-md-6">
                                            <input type="hidden" name="unq" value="<%=unq%>">
                                            <input type="hidden" name="msgType" value="Welcome">
                                            <input type="hidden" name="numberOfMessages" class="msgsNum" value="<%=welcomeList.size()%>">
                                            <input type="hidden" name="saveClubMsg" value="saveClubMsg"  />
                                            <input type="submit" name="saveClubMsg" value="Save" class="btn blue submitBtn col-md-12" />
                                          </div>
                                      </div>
                                  </form>
                              </div>
                            </div> 
                          </div>
                          <div class="tab-pane" id="tab_2">
                            <div class="portlet box red-sunglo" data-type="Stop">
                              <div class="portlet-title">
                                  <div class="caption">
                                      <i class="fa fa-times-circle"></i>
                                      <span class="caption-subject bold uppercase">Stop Messages</span>
                                      <span name="numberOfMessages" class="msgsNum" readonly value="<%=welcomeList.size()%>" ><%=stopList.size()%></span>
                                  </div>
                                  <div class="actions">
                                      <div class="btn-group">
                                          <a class="btn btn-sm bg-white js_AddButton" > Add New
                                              <i class="fa fa-plus"></i>
                                          </a>
                                      </div>
                                  </div>
                              </div>
                              <div class="portlet-body form">
                                  <form role="form" action="../clubadmin/msgs.jsp?unq=<%=unq%>">

                                      <div class="form-body">
                                        <div class="msgsHolder">
                                          <%for(int i=0;i<stopList.size();i++) {
                                             Map<String,Object> stopMap=stopList.get(i);
                                             UmeClubMessages umeClubMessages=(UmeClubMessages)stopMap.get("message");
                                             Integer messageLength=(Integer)stopMap.get("messageLength");
                                           
                                             int j=i+1;
                                           %>
                                          <div class="form-group form-md-line-input form-md-floating-label js_msgHolder">
                                            <div class="input-group">
                                              <div class="input-group-control">
                                                <input  type="hidden" 
                                                        name="Stop_<%=i%>_Unique" 
                                                        value="<%=umeClubMessages.getaUnique()%>" />
                                                <textarea 
                                                        class="form-control edited" 
                                                        rows="1" 
                                                        name="msg_<%=i%>"><%=umeClubMessages.getaMessage()%></textarea>
                                                <span class="help-block"><%=messageLength %> </span>
                                                <label for="form_control_1">
                                                    Stop Message <%=j%> 
                                                </label>
                                              </div>
                                              <span class="input-group-btn btn-right open">
                                                  <button type="button" 
                                                          data-delete="stopMessage_<%=i%>_Delete"
                                                          data-uniqueName="Stop_<%=i%>_Unique"
                                                          data-uniqueValue="<%=umeClubMessages.getaUnique()%>"
                                                          class="btn red js_DeleteMsg"> 
                                                    <i class="fa fa-remove"></i>
                                                    Delete
                                                  </button>
                                              </span>
                                            </div>
                                          </div>

                                          <%} %>
                                        </div>
                                          </div>
                                          <div class="row">
                                            <div class="col-md-offset-3 col-md-6">
                                              <input type="hidden" name="unq" value="<%=unq%>">
                                              <input type="hidden" name="msgType" value="Stop">
                                              <input type="hidden" name="numberOfMessages" class="msgsNum" value="<%=stopList.size()%>">
                                              <input type="hidden" name="saveClubMsg" value="saveClubMsg"  />
                                              <input type="submit" name="saveClubMsg" value="Save" class="btn blue submitBtn col-md-12" />
                                            </div>
                                          </div>
                                  </form>
                              </div>
                            </div>
                          </div>
                          <div class="tab-pane" id="tab_3">
                            <div class="portlet box yellow-lemon" data-type="Reminder">
                              <div class="portlet-title">
                                  <div class="caption">
                                      <i class="fa fa-exclamation"></i>
                                      <span class="caption-subject bold uppercase">Reminder Messages</span>
                                      <span name="numberOfMessages" class="msgsNum" readonly value="<%=welcomeList.size()%>" ><%=reminderList.size()%></span>

                                  </div>
                                  <div class="actions">
                                      <div class="btn-group">
                                          <a class="btn btn-sm bg-white js_AddButton" > Add New
                                              <i class="fa fa-plus"></i>
                                          </a>
                                      </div>
                                  </div>
                              </div>
                              <div class="portlet-body form">
                                  <form role="form" action="../clubadmin/msgs.jsp?unq=<%=unq%>">
                                      <div class="form-body">
                                        <div class="msgsHolder">
                                        <%for(int i=0;i<reminderList.size();i++) {
                                          Map<String,Object> reminderMap=reminderList.get(i);
                                          UmeClubMessages umeClubMessages=(UmeClubMessages)reminderMap.get("message");
                                          Integer messageLength=(Integer)reminderMap.get("messageLength");
                                        
                                          int j=i+1;
                                        %>
                                          <div class="form-group form-md-line-input form-md-floating-label js_msgHolder">
                                            <div class="input-group">
                                              <div class="input-group-control">
                                                <input  type="hidden" 
                                                      name="Reminder_<%=i%>_Unique" 
                                                      value="<%=umeClubMessages.getaUnique()%>" 
                                                />
                                                <textarea rows="1" 
                                                        class="form-control edited" 
                                                        name="msg_<%=i%>"><%=umeClubMessages.getaMessage()%></textarea>
                                                <span class="help-block"><%=messageLength %> </span>
                                                <label for="form_control_1">
                                                  Reminder Message <%=j%>
                                                </label>
                                              </div>
                                              <span class="input-group-btn btn-right open">
                                                  <button type="button"
                                                          data-delete="reminderMessage_<%=i%>_Delete" 
                                                          data-uniquename="Reminder_<%=i%>_Unique"
                                                          data-uniquevalue="<%=umeClubMessages.getaUnique()%>"
                                                          class="btn red js_DeleteMsg"> 
                                                    <i class="fa fa-remove"></i>
                                                    Delete
                                                  </button>
                                              </span>
                                            </div>
                                          </div>
                                        <%} %>
                                      </div>
                                      </div>
                                      <div class="row">
                                        <div class="col-md-offset-3 col-md-6">
                                          <input type="hidden" name="unq" value="<%=unq%>">
                                          <input type="hidden" name="msgType" value="Reminder">
                                          <input type="hidden" name="numberOfMessages" class="msgsNum" value="<%=reminderList.size()%>">
                                          <input type="hidden" name="saveClubMsg" value="saveClubMsg"  />
                                          <input type="submit" name="saveClubMsg" value="Save" class="btn blue submitBtn col-md-12" />
                                        </div>
                                      </div>
                                  </form>
                              </div>
                          </div>
                          </div>
                          <div class="tab-pane" id="tab_4">
                            <div class="portlet box dark" data-type="FollowUp">
                                <div class="portlet-title">
                                    <div class="caption">
                                        <i class="fa fa-exclamation"></i>
                                        <span class="caption-subject bold uppercase">FollowUp Messages</span>
                                        <span name="numberOfMessages" class="msgsNum" readonly value="<%=followUpList.size()%>" ><%=reminderList.size()%></span>
                                    </div>
                                    <div class="actions">
                                        <div class="btn-group">
                                            <a class="btn btn-sm bg-white js_AddButton" > Add New
                                                <i class="fa fa-plus"></i>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                <div class="portlet-body form">
                                    <form role="form" action="../clubadmin/msgs.jsp?unq=<%=unq%>">

                                        <div class="form-body">
                                          <div class="msgsHolder">
                                          <%for(int i=0;i<followUpList.size();i++) {
                                            Map<String,Object> followupMap=followUpList.get(i);
                                            UmeClubMessages umeClubMessages=(UmeClubMessages)followupMap.get("message");
                                            Integer messageLength=(Integer)followupMap.get("messageLength");
                                          
                                            int j=i+1;
                                          %>
                                            <div class="form-group form-md-line-input form-md-floating-label js_msgHolder">
                                              <div class="input-group">
                                                <div class="input-group-control">
                                                  <input  type="hidden" 
                                                          name="FollowUp_<%=i%>_Unique" 
                                                          value="<%=umeClubMessages.getaUnique()%>" 
                                                  />
                                                  <textarea rows="1" 
                                                            class="form-control edited" 
                                                            name="msg_<%=i%>"><%=umeClubMessages.getaMessage()%></textarea>
                                                  <span class="help-block"><%=messageLength %> </span>
                                                    <label for="form_control_1">
                                                      FollowUp Message <%=j%>
                                                    </label>
                                                </div>
                                                <span class="input-group-btn btn-right open">
                                                  <button type="button"
                                                          data-delete="followUpMessage_<%=i%>_Delete" 
                                                          data-uniquename="FollowUp_<%=i%>_Unique"
                                                          data-uniquevalue="<%=umeClubMessages.getaUnique()%>"
                                                          class="btn red js_DeleteMsg"> 
                                                    <i class="fa fa-remove"></i>
                                                    Delete
                                                  </button>
                                                </span>
                                              </div>
                                            </div>
                                          <%} %>
                                        </div>
                                        </div>
                                      <div class="row">
                                        <div class="col-md-offset-3 col-md-6">
                                          <input type="hidden" name="unq" value="<%=unq%>">
                                          <input type="hidden" name="msgType" value="FollowUp">
                                          <input type="hidden" name="numberOfMessages" class="msgsNum" value="<%=followUpList.size()%>">
                                          <input type="hidden" name="saveClubMsg" value="saveClubMsg"  />
                                          <input type="submit" name="saveClubMsg" value="Save" class="btn blue submitBtn col-md-12" />
                                        </div>
                                      </div>
                                    </form>
                                </div>
                            </div>                             
                          </div>
                          <div class="tab-pane" id="tab_5">
                            <div class="portlet box yellow-gold" data-type="Teaser">
                              <div class="portlet-title">
                                  <div class="caption">
                                      <i class="fa fa-exclamation "></i>
                                      <span class="caption-subject bold uppercase">Teaser Messages</span>
                                      <span name="numberOfMessages" class="msgsNum" readonly value="<%=teaserList.size()%>" ><%=reminderList.size()%></span>
                                  </div>
                                  <div class="actions">
                                      <div class="btn-group">
                                          <a class="btn btn-sm bg-white js_AddButton" > Add New
                                              <i class="fa fa-plus"></i>
                                          </a>
                                      </div>
                                  </div>
                              </div>
                              <div class="portlet-body form">
                                  <form role="form" action="../clubadmin/msgs.jsp?unq=<%=unq%>">

                                      <div class="form-body">
                                        <div class="msgsHolder">
                                        <%for(int i=0;i<teaserList.size();i++) {
                                          Map<String,Object> teaserMap=teaserList.get(i);
                                          UmeClubMessages umeClubMessages=(UmeClubMessages)teaserMap.get("message");
                                          Integer messageLength=(Integer)teaserMap.get("messageLength");
                                        
                                          int j=i+1;
                                        %>
                                          <div class="form-group form-md-line-input form-md-floating-label js_msgHolder">
                                            <div class="input-group">
                                              <div class="input-group-control">
                                                <input  type="hidden" 
                                                      name="Teaser_<%=i%>_Unique" 
                                                      value="<%=umeClubMessages.getaUnique()%>" 
                                                />
                                                <textarea rows="1" 
                                                        class="form-control edited" 
                                                        name="msg_<%=i%>"><%=umeClubMessages.getaMessage()%></textarea>
                                                <span class="help-block"><%=messageLength %> </span>
                                                <label for="form_control_1">
                                                  Teaser Message <%=j%>
                                                </label>
                                              </div>
                                              <span class="input-group-btn btn-right open">
                                                  <button type="button"
                                                          data-delete="teaserMessage_<%=i%>_Delete" 
                                                          data-uniquename="Teaser_<%=i%>_Unique"
                                                          data-uniquevalue="<%=umeClubMessages.getaUnique()%>"
                                                          class="btn red js_DeleteMsg"> 
                                                    <i class="fa fa-remove"></i>
                                                    Delete
                                                  </button>
                                              </span>
                                            </div>
                                          </div>
                                        <%} %>
                                      </div>
                                      </div>
                                      <div class="row">
                                        <div class="col-md-offset-3 col-md-6">
                                          <input type="hidden" name="unq" value="<%=unq%>">
                                          <input type="hidden" name="msgType" value="Teaser">
                                          <input type="hidden" name="numberOfMessages" class="msgsNum" value="<%=teaserList.size()%>">
                                          <input type="hidden" name="saveClubMsg" value="saveClubMsg"  />
                                          <input type="submit" name="saveClubMsg" value="Save" class="btn blue submitBtn col-md-12" />
                                        </div>
                                      </div>
                                  </form>
                              </div>
                          </div>
                          </div>
                         
                      </div>
                  </div>
              </div>
          </div>

<script>
// Angel JS (Angel 
$(document).ready(function() {

  function charCountStatic() {
    $( "body textarea" ).each(function ( index ) {
      var cs = $(this).val().length;
      $(this).closest('.help-block').text(' (' + cs + ' characters)');
      $(this).siblings('.help-block').html(cs + ' characters');
    });
  }
 
  function charCount() {
    $('body').on("keyup, keydown", "textarea", function(){
        cs = $(this).val().length + 1;
        $('body').find($(this)).siblings('.help-block').html(cs + ' characters');
      });
   }

  charCount();
  charCountStatic();

  function messageCode(type, num) {                                                                          
    var code  = '<div id="msg' + type + num + '" class="form-group form-md-line-input form-md-floating-label js_msgHolder unimportantHidden">'
              +   '<div class="input-group">'
              +     '<div class="input-group-control">'
              +       '<textarea class="form-control" rows="1" name="msg_' + num + '"></textarea>'
              +       '<span class="help-block">0 characters</span>'
              +       '<label for="form_control_1">'+ type +' Message ' + (num+1) + '</label>'
              +     '</div>'
              +     '<span class="input-group-btn btn-right open">'
              +       '<button type="button" class="btn red js_DeleteMsg"> '
              +         '<i class="fa fa-remove"></i>Delete'
              +       '</button>'                             
              +     '</span>'                     
              +   '</div>'
              + '</div>'; 
    return code; 
  }

  $('.tab-content').on('click', '.js_AddButton', function() {
    var holder = $(this).closest(".portlet");
    var msgType = holder.data("type");
    var formBody = holder.find(".form-body .msgsHolder");
    var num = formBody.children().size();
    formBody.append(messageCode(msgType, num));
    var msgToHide = '#msg'+msgType+num;
    $(msgToHide).slideDown("fast"); 
    holder.find(".msgsNum").val(num + 1);
    holder.find(".msgsNum").text(' ( '+(num + 1)+ ' ) ');
    charCount();
  });

  $(".tab-content .form").on("click", ".js_DeleteMsg", function() {
    var el = $(this).closest(".tab-pane.active");
    var msgDeleted = $(this).data("delete");
    var msgUniqueName = $(this).data("uniquename");
    var msgUniqueValue = $(this).data("uniquevalue");
    var currentTab = '#' + el.prop("id");
    var dataObject = {};
    dataObject[msgDeleted] = "1";
    dataObject["delete"] = "delete";
    dataObject["msgunique"] = msgUniqueValue;
    dataObject["unq"] = "<%=unq%>";
    App.blockUI({
            target: el,
            animate: true,
            overlayColor: 'black',
            message: 'Please Wait, we are either pricessing or the server is out of memory. Again. James?'
            }); 
    $.ajax({ 
        url : 'clubadmin/msgs.jsp?unq=<%=unq%>',
        type: "post",
        data: dataObject
    }).done(function(data) { 
        //submitBtn.attr("value", "Save");
        var parsed  = $.parseHTML(data);
        var success = $(parsed).filter(".statusMsg").text();
        alertInfo(success);
        var portlet = $(parsed).find(currentTab).find('.msgsHolder');
        $(currentTab).find('.form-body').html(portlet);
        App.unblockUI(el);
        charCountStatic();
       // $(this).closest(".js_msgHolder").slideUp("fast");
    });
    
  });
                        

  $(".contentHolder").on("click", ".jsLoad", function(e) {
    var url = $(this).data("url");
    $('.contentHolder').html("");
    $('.contentHolder').load("../clubadmin/" + url);
  });

  $(".jsUrlLoad").on("click", function(e) {
    var url = $(this).data("url");
    $('.contentHolder').html("");
    $('.contentHolder').load("../clubadmin/" + url);
  });

  $('body form').submit(function(event) {
    var $form = $(this);
    var submitBtn = $form.find(".submitBtn");
    var el = $(this).closest(".tab-pane.active");
    var currentTab = '#' + el.prop("id");
    console.log(currentTab);
    //var msgNum = $form.children("textarea").size();
    submitBtn.attr("value", "Sending data, please wait...");
    //$form.find(".msgsNum").val(msgNum);
    App.blockUI({
            target: el,
            animate: true,
            overlayColor: 'black',
            message: 'Please Wait, we are either pricessing or the server is out of memory. Again. James?'
            }); 
    $.ajax({ 
        url : 'clubadmin/msgs.jsp?unq=<%=unq%>',
        type: "post",
        data: $form.serialize()
    }).done(function(data) { 
        submitBtn.attr("value", "Save");
        var parsed  = $.parseHTML(data);
        var success = $(parsed).filter(".statusMsg").text();
        alertInfo(success);
        var portlet = $(parsed).find(currentTab).find('.msgsHolder');
        $(currentTab).find('.form-body').html(portlet);
        App.unblockUI(el);
        charCountStatic();
    });
    event.preventDefault();
  }); 

function alertInfo(alertText) {
    App.alert({ 
        container   : ".alertHolder",   // alerts parent container
        place       : 'append',         // append or prepent in container
        type        : 'info',           // alert's type
        message     : alertText,        // alert's message
        close       : true,             // make alert closable
        reset       : true,             // close all previouse alerts first
        icon        : 'fa fa-info',     // icon 
        closeInSeconds: 120,            // auto close after defined seconds 
        focus       : true              // auto scroll to the alert after shown 

        // TODO eventually - if Madan sends me the type of status Message can make it success, fail, info etc.
    });
} 

});
</script>




<%--
<form name="addMsg" method="post" action="<%=fileName%>.jsp">
<input type="hidden" name="unq" value="<%=unq%>">
<input type="hidden" name="numberOfMessages" class="msgsNum" id="numberOfMessages" >

Select Message Type: <select name="msgType">
  <option value="" selected>Select Message Type</option>
  <%for(int i=0;i<messageTypes.size();i++) {%>
  <option value="<%= messageTypes.get(i)%>" <% if(msgType.equals(messageTypes.get(i))){%> selected <%} %>><%= messageTypes.get(i)%></option>
  <%} %>

</select>

<%
if(!msgType.equals("")){
%>
<div id="addinput">
<p>
<textarea class="countlength" id="msg" name="msg_0" style="width:300px; height:60px;"></textarea><a href="#" id="addNew">Add</a>
</p>
</div> 
<input type="submit" name="saveClubMsg" value="save"/>
<%} %>
</form>








    <form method="post" action="<%=fileName%>.jsp">
  <input type="hidden" name="unq" value="<%=unq%>">
  <input type="hidden" name="msgType" value="<%=msgType%>">


    <table cellspacing="0" cellpadding="5" border="0" width="100%">
      
      <%for(int i=0;i<billableList.size();i++) {
        UmeClubMessages umeClubMessages=(UmeClubMessages)billableList.get(i);
        int j=i+1;
      %>
        <tr>
            <td align="left" class="grey_11" valign="top">Billable Message <%=j%>:</td>
            <td align="left" class="grey_11">
            <input type="hidden" name="billableMessage_<%=i%>_Unique" value="<%=umeClubMessages.getaUnique()%>" />
            <textarea class="countlength" name="billableMessage_<%=i%>" style="width:300px; height:60px;"><%=umeClubMessages.getaMessage()%></textarea>
            <span class="count_display" id="billableMessage_<%=i%>"></span></td>
            <td><input type="checkbox" name="billableMessage_<%=i%>_Delete"/></td>
            
        </tr>
      <%} %>
      
      <%for(int i=0;i<welcomeList.size();i++) {
        UmeClubMessages umeClubMessages=(UmeClubMessages)welcomeList.get(i);
        int j=i+1;
      %>
        <tr>
            <td align="left" class="grey_11" valign="top">Welcome Message <%=j%>:</td>
            <td align="left" class="grey_11">
            <input type="hidden" name="welcomeMessage_<%=i%>_Unique" value="<%=umeClubMessages.getaUnique()%>" />
            <textarea class="countlength" name="welcomeMessage_<%=i%>" style="width:300px; height:60px;"><%=umeClubMessages.getaMessage()%></textarea>
            <span class="count_display" id="welcomeMessage_<%=i%>"></span></td>
            <td><input type="checkbox" name="welcomeMessage_<%=i%>_Delete"/></td>
            
        </tr>
      <%} %>
      
      <%for(int i=0;i<reminderList.size();i++) {
        UmeClubMessages umeClubMessages=(UmeClubMessages)reminderList.get(i);
        int j=i+1;
      %>
        <tr>
            <td align="left" class="grey_11" valign="top">Reminder Message <%=j%>:</td>
            <td align="left" class="grey_11">
            <input type="hidden" name="reminderMessage_<%=i%>_Unique" value="<%=umeClubMessages.getaUnique()%>" />
            
            <textarea class="countlength" name="reminderMessage_<%=i%>" style="width:300px; height:60px;"><%=umeClubMessages.getaMessage()%></textarea>
            <span class="count_display" id="reminderMessage_<%=i%>"></span></td>
          <td><input type="checkbox" name="reminderMessage_<%=i%>_Delete"/></td>
        </tr>
      <%} %>
      
       <%for(int i=0;i<stopList.size();i++) {
         UmeClubMessages umeClubMessages=(UmeClubMessages)stopList.get(i);
         int j=i+1;
       %>
        <tr>
            <td align="left" class="grey_11" valign="top">Stop Message <%=j%>:</td>
            <td align="left" class="grey_11">
            <input type="hidden" name="stopMessage_<%=i%>_Unique" value="<%=umeClubMessages.getaUnique()%>" />
            
            <textarea class="countlength" name="stopMessage_<%=i%>" style="width:300px; height:60px;"><%=umeClubMessages.getaMessage()%></textarea>
            <span class="count_display" id="stopMessage_<%=i%>"></span></td>
            <td><input type="checkbox" name="stopMessage_<%=i%>_Delete"/></td>
        </tr>
      <%} %>
      
      <%for(int i=0;i<followUpList.size();i++) {
        UmeClubMessages umeClubMessages=(UmeClubMessages)followUpList.get(i);
        int j=i+1;
      %>
        <tr>
            <td align="left" class="grey_11" valign="top">FollowUp Message <%=j%>:</td>
            <td align="left" class="grey_11">
            <input type="hidden" name="followUpMessage_<%=i%>_Unique" value="<%=umeClubMessages.getaUnique()%>" />
            
            <textarea class="countlength" name="followUpMessage_<%=i%>" style="width:300px; height:60px;"><%=umeClubMessages.getaMessage()%></textarea>
            <span class="count_display" id="followUpMessage_<%=i%>"></span></td>
            <td><input type="checkbox" name="followUpMessage_<%=i%>_Delete"/></td>
        </tr>
      <%} %>
      
      <%for(int i=0;i<teaserList.size();i++) {
        UmeClubMessages umeClubMessages=(UmeClubMessages)teaserList.get(i);
        int j=i+1;
      %>
        <tr>
            <td align="left" class="grey_11" valign="top">Stop Message <%=j%>:</td>
            <td align="left" class="grey_11">
            <input type="hidden" name="teaserMessage_<%=i%>_Unique" value="<%=umeClubMessages.getaUnique()%>" />
            
            <textarea class="countlength" name="teaserMessage_<%=i%>" style="width:300px; height:60px;"><%=umeClubMessages.getaMessage()%></textarea>
            <span class="count_display" id="teaserMessage_<%=i%>"></span></td>
            <td><input type="checkbox" name="teaserMessage__<%=i%>_Delete"/></td>
        </tr>
      <%} %>
      
      <%--   <tr>
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
            <td align="left" class="grey_11"><textarea class="countlength" name="billingmessage3" style="width:300px; height:60px;"><%=billingmessage3%></textarea>
            <span class="count_display" id="count_billingmessage3"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Welcome SMS:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aWelcomeSms" style="width:300px; height:60px;"><%=item.getWelcomeSms()%></textarea>
            <span class="count_display" id="count_aWelcomeSms"></td>
        </tr>
         <tr>
            <td align="left" class="grey_11" valign="top">Welcome SMS 2:</td>
            <td align="left" class="grey_11"><textarea class="countlength" name="aWelcomeSms2" style="width:300px; height:60px;"><%=welcomesms2%></textarea>
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
            
            
            <td align="left" class="grey_11"><textarea class="countlength" name="freeday" style="width:30px; height:20px;"><%=freedays%></textarea>
                <span class="count_display" id="count_freeday"></td>
            <td align="left" class="grey_11" valign="top">(Free Days)</td> 
            
            
        </tr>
         </table>
            </td></tr>
            
            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
            <tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
            <tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
            <tr>
            <td align="right"><input type="submit" name="save" value="Save" style="width:150px;"></td>
            <td align="right"><input type="submit" name="delete" value="Delete Selected" style="width:150px;"></td>
            </tr>
            
        </table>

    </form>
    
    <script src="/static/AdminPanel/assets/global/plugins/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript">
    /*$(function() {
      var addDiv = $('#addinput');
      var i = $('#addinput p').size();
      document.getElementById("numberOfMessages").value=i;
      
      $('body').on('click', '#addNew', function() {
        
        $('<p><textarea id="msg" style="width:300px; height:60px;" name="msg_' + i +'" value=""></textarea><a href="#" id="remNew">Remove</a> </p>').appendTo(addDiv);
        i++;
        $('#numberOfMessages').value=i;
        document.getElementById("numberOfMessages").value=i;
        
        return false;
      });
      $('body').on('click', '#remNew', function() {
        if( i > 1 ) {
          $(this).parents('p').remove();
          i--;
        }
        $('#numberOfMessages').value=i;
        document.getElementById("numberOfMessages").value=i;
        
      return false;
      });
    });
*/
    function form_submit(form){
      form.submit();
    }

    </script>
--%>
</body>
</html>
