<%@include file="/WEB-INF/jspf/itemadmin/java/linkgames.jspf"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
        <link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
        <script src="/lib/scriptaculous/prototype.js" language="javascript"></script>
        <script src="/lib/scriptaculous/scriptaculous.js" language="javascript"></script>
        <style type="text/css">
            div.page_name_auto_complete {
                    width: 100px;
                    background: #fff;
                    display: inline;
            }

            div.page_name_auto_complete ul {
                    border: 1px solid #888;
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    list-style-type: none;
            }

            div.page_name_auto_complete ul li {
                    margin: 0;
                    padding: 3px;
            }

            div.page_name_auto_complete ul li.selected { 
                    background-color: lightblue; 
            }

            div.page_name_auto_complete ul strong.highlight { 
                    color: #800; 
                    margin: 0;
                    padding: 0;
            }

        </style>
        <script language="javascript">
function submitForm (thisForm) {
	thisForm.del.value="1";
	thisForm.submit();
}
defColor = '';
selectedColor='#111111';
function seldesel( el , sel ){
   
    if( el.bgColor != selectedColor ){
        el.bgColor = selectedColor; 
        if( typeof( sel ) == 'undefined')
            document.getElementById( 'chk_' + el.id ).checked = true;
        else
            document.getElementById( 'unchk_' + el.id ).checked = true;
        }
    else {
        el.bgColor = ''; 
        if( typeof( sel ) == 'undefined')
            document.getElementById( 'chk_' + el.id ).checked = false;
        else
            document.getElementById( 'unchk_' + el.id ).checked = false;
        }
}


        </script>
    </head>
    <body>
        
        
        <form action="<%=fileName%>.jsp?unq=<%=unique%>" method="post" name="idform">
            
            <table cellspacing="0" cellpadding="0" border="0" width="98%">
                <tr><td valign="top" align="left">
                        <table cellspacing="0" cellpadding="3" border="0" width="100%">
                            <tr>
                                <td align="left" valign="bottom" class="blue_14"><b>Java Game Details: </b> <span class="grey_12"><b><%=orgTitle%></b></span></td>
                                <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
                            </tr>
                        </table>    
                </td></tr>
                <tr><td valign="top" align="left">
                        <% int _curitem = Integer.parseInt(aReq.get("_curitem", "1")); %>
                        <%@ include file="/itemadmin/java/tabs.jsp" %>
                        <br>
                </td></tr>
                <tr><td><img src="/images/glass_dot.gif" height="10" width="100%"></td></tr>
                <tr><td><img src="/images/grey_dot.gif"  height="1" width="100%"></td></tr>
                <tr><td><img src="/images/glass_dot.gif" height="10" width="100%"></td></tr>
            </table>
            <table width="98%" cellspacing="0">
                <tr>
                    <td align="left">
                        <input type="text" name="searchbox" autocomplete="off" id="searchbox" value="<%=search_query%>">
                        <input type="submit" class="button" value="Search" name="search">
                        <input type="submit" class="button" value="Whole Library" name="listall">
                        <span id="status" >&nbsp;</span>
                    </td>
                </tr>
            </table>
            <table width="98%" class="tableview" cellspacing="0">

                <tr>
                    <th align="left"> 
                        <span style="font-size:15;">Library Games</span>
                    </th>
                    <th align="center">
                        <input type="submit" value="&lt;&lt;&nbsp;SWITCH&nbsp;&amp;&nbsp;SAVE&nbsp;&gt;&gt" class="button" name="switch" align="middle">
                    </th>
                    <th align="right">
                        <span style="font-size:15;">Linked Games</span>
                    </th>
                </tr>
            </table>
            <table width="98%" class="tableview" cellspacing="0">
                <% int dh = 450; %>
                <tr>
                    <td valign="top" width="50%">
                        <div style="overflow-x: hidden; overflow-y: auto;height:<%=dh%>px" >
                            
                            <ul style="padding:0;margin:0; list-style-type: none; " id="all"><%
                                for( int i = 0; i < fullList.size(); i ++ ){
                                values = (String[]) fullList.get( i );
                                %>
                                <li style="margin: 0; padding: 0;" id="item_<%=values[0]%>">
                                    <table cellspacing="0"  cellpadding="8" border="0" class="grey_11" width="100%"> 
                                        <tr onclick="return seldesel( this );" id="<%=values[0]%>" >
                                            <td width="10%">
                                                <input type="checkbox" name="chk_<%=values[0]%>" id="chk_<%=values[0]%>" >
                                            </td>
                                            <td width="10%">
                                                <!-- <a href="gamedetails.jsp?unq=<%=values[0]%>&dm=&sort=cr"> --><img class="linkGamesHandle" src="<%=values[2]%>" width="36" alt="<%=values[1]%>" border="0"><!-- </a> -->
                                            </td>
                                            <td>
                                                <!-- <a href="gamedetails.jsp?unq=<%=values[0]%>&dm=&sort=cr"> --><%=values[1]%><!-- </a> -->
                                            </td>
                                            <td width="30%">
                                                <a href="itemdetails.jsp?unq=<%=values[0]%>&dm=&sort=cr">Details</a>
                                            </td>
                                        </tr>
                                    </table>
                                </li><%
                                }%>
                            </ul>
                        </div>
                    </td>
                    <td valign="top" width="50%">
                        <div style="overflow-x: hidden; overflow-y: auto;height:<%=dh%>px" >
                            
                            
                            <ul style="padding:0;margin:0; list-style-type: none;" id="sel">
                                <%for( int i = 0; i < selectedList.size(); i ++ ){
                                values = (String[]) selectedList.get( i );
                                %>
                                <li style="margin: 0; padding: 0;" id="item_<%=values[0]%>">
                                    <table cellspacing="0" cellpadding="8" border="0" class="grey_11" width="100%">
                                        <tr onclick="return seldesel( this, 'des' );" id="<%=values[0]%>" >
                                            <td width="10%">
                                                <input type="checkbox" name="unchk_<%=values[0]%>" id="unchk_<%=values[0]%>" >
                                            </td>
                                            <td width="10%">
                                                <!-- <a href="gamedetails.jsp?unq=<%=values[0]%>&dm=&sort=cr"> --><img class="linkGamesHandle" src="<%=values[2]%>" width="36" alt="<%=values[1]%>" border="0"><!-- </a> -->
                                            </td>
                                            <td>
                                                <!-- <a href="gamedetails.jsp?unq=<%=values[0]%>&dm=&sort=cr"> --><%=values[1]%> <!-- </a> -->
                                            </td>
                                            <td width="30%">
                                                <a href="gamedetails.jsp?unq=<%=values[0]%>&dm=&sort=cr">Details</a>
                                            </td>
                                        </tr>
                                    </table>
                                </li>
                                <%
                                }if( selectedList.size() == 0 ){%><li id="<%=noneselectedid %>" class="grey_11" style="height:<%=noneselectedheight%>" ><%=noneselectedtext%></li><%}%>
                            </ul>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div id="search_choices" class="page_name_auto_complete" style="display:none;" ></div>
                    </td>
                </tr>
            </table>
            
            
        </form>
        <script type="text/javascript">
            el  = '';
            bo = '';
            ajel =''; ajel2=''; 
            Sortable.create( "sel", 
                { dropOnEmpty:true, handle:'linkGamesHandle', containment:["sel","all"], constraint:false,
                     onUpdate: function( box ){
                        if( bo != 'sel' ){
                            str = el.id;
                            new Ajax.Updater( 'status', '/javagamesadmin/linkgames_be.jsp', 
                                {
                                    asynchronous:true, 
                                    evalScripts:true, 
                                    onComplete:function(request)
                                    {
                                        var ajel = document.getElementById ( 'unchk_' + str.substring( 5 ) ) ;
                                        ajel.id = ajel.id.substring( 2 );
                                        ajel.name = ajel.name.substring( 2 );
                                        
                                        var ajel2 = document.getElementById( ajel.id.substring(4) );
                                        ajel2.bgColor = selectedColor; 
                                        seldesel( ajel2 );
                                        ajel2.onclick = function(){
                                            return seldesel( ajel2 );
                                        }
                                        var selectedlist = document.getElementById('sel');
                                        if( selectedlist.childNodes.length == 0){
                                            var noneselected = document.createElement( 'li' );
                                            noneselected.setAttribute( 'id', '<%=noneselectedid %>' );
                                            noneselected.setAttribute( 'class', 'grey_11' );
                                            noneselected.style.height = '<%=noneselectedheight%>';
                                            noneselected.innerHTML = '<%=noneselectedtext%>';
                                            
                                            selectedlist.appendChild( noneselected );
                                        }
                                    }, 
                                    parameters:{action:'unlink', game1: el.id, game2: '<%=unique%>' }
                                }
                            );    
                        }
                    },
                    onChange: function( elem ){
                        el = elem;
                        bo = 'sel';
                    }
                }
            );
            Sortable.create( "all", 
                { dropOnEmpty:true, handle:'linkGamesHandle', containment:["sel","all"], constraint:false,
                    onUpdate: function( box ){
                        if( bo != 'all' ){
                            str = el.id;
                            new Ajax.Updater( 'status', 'linkgames_be.jsp',
                                {
                                    asynchronous:true, 
                                    evalScripts:true, 
                                    onComplete:function(request)
                                    {
                                        var ajel = document.getElementById ( 'chk_' + str.substring( 5 ) ) ;
                                        ajel.id = 'un' + ajel.id;
                                        ajel.name = 'un' + ajel.name;
                                        
                                        var ajel2 = document.getElementById( ajel.id.substring(6) );
                                        ajel2.bgColor = selectedColor; 
                                        seldesel( ajel2, 'des' );
                                        ajel2.onclick = function(){
                                            return seldesel( ajel2,'des');
                                        }
                                        var noneselected = document.getElementById('<%=noneselectedid%>');
                                        if( noneselected != null ){
                                            document.getElementById('sel').removeChild( noneselected );
                                        }
                                        
                                    }, 
                                    parameters:{action:'link', game1: el.id, game2: '<%=unique%>' }
                                }
                            );    
                        }
                    },
                    onChange: function( elem ){
                        el = elem;
                        bo = 'all';
                    }
                }
            );
            var search_choices = new Array(<% for (int i = 0; i < allGames.size(); i ++ ){%>"<%=(String)allGames.get(i)%>",<%}%>"");
            new Autocompleter.Local( 'searchbox', 'search_choices', search_choices, {} );
          
        </script>
        
    </body>
</html>

    


