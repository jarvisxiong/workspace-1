
<%@ include file="/WEB-INF/jspf/umeadmin/index.jspf" %>
<html>
<head>
				
</head>
<body>

    <div id="content-wrapper">

<table cellspacing="0" cellpadding="0" border="0" width="100%" height="100%">
<tr>
<td valign="top" align="left">

    <!-- Left Column Start-->

    <table cellspacing="0" cellpadding="0" border="0">
    <tr><td align="left" valign="top">

            <table cellspacing="0" cellpadding="2" border="0">
            <tr>
                
               
                        
                
                
                    <td colspan="2" align="left"><div class="normal_blue"><%=lp.get(2)%> <%=Misc.utfToUnicode(uProps[2],"utf-8").toUpperCase()%></div></td>
	<div id="chart1"></div>
   		
<td>
            </tr>
            <!--
            <tr>
                    <td align="left"><div class="small_blue"><%=lp.get(3)%></div></td>
                    <td align="left"><div class="small_blue"><b><%=MiscDate.formatSqlDate(uProps[12])%></b></div></td>
            </tr>
            -->
           
            </table>

    </td></tr>
<!--<tr>
   <div id="chart"></div>
   <div id="chart1"></div>
<div id="chart2"></div>
    <script>

      var pubnub = PUBNUB.init({
        publish_key: 'pub-c-6dbe7bfd-6408-430a-add4-85cdfe856b47',
        subscribe_key: 'sub-c-2a73818c-d2d3-11e3-9244-02ee2ddab7fe'
      });

      var channel = "c3-spline" + Math.random();
	var channel1 = 'c3-gauge'  + Math.random();
	var channel2 = "c3-spline" + Math.random();
      
      eon.chart({
        pubnub: pubnub,
        channel: channel,
        history: true,
        debug: true,
        generate: {
          bindto: '#chart',
          data: {
            labels: true,
            type: 'bar'
          },
          bar: {
            width: {
              ratio: 0.5
            }
          }
        }
		
      });
	  eon.chart({
        pubnub: pubnub,
        channel: channel1,
        generate: {
          bindto: '#chart1',
          data: {
		  labels:true,
            type: 'gauge'
          }
        }
      });
	  
	  eon.chart({
        pubnub: pubnub,
        channel: channel,
        history: true,
        debug: true,
        generate: {
          bindto: '#chart2',
          data: {
            labels: true,
            type: 'pie'
          },
          bar: {
            width: {
              ratio: 0.5
            }
          }
        }
      });
    </script>
    <script>
      setInterval(function(){

        pubnub.publish({
          channel: channel,
          message: {
            eon: {
              'ZA': Math.floor(Math.random() * 99),
              'UK': Math.floor(Math.random() * 99),
              'IE': Math.floor(Math.random() * 99),
              'IT': Math.floor(Math.random() * 99)
            }
          }
        });

		
      }, 1000);
	  
	  setInterval(function(){

        pubnub.publish({
          channel: channel1,
          message: {
            eon: {
              'ZA': Math.floor(Math.random() * 99)
                          }
          }
        });
		      }, 1000);
			  
			  setInterval(function(){

        pubnub.publish({
          channel: channel2,
          message: {
            eon: {
              'ZA': Math.floor(Math.random() * 99),
              'UK': Math.floor(Math.random() * 99),
              'IE': Math.floor(Math.random() * 99),
              'IT': Math.floor(Math.random() * 99)
            }
          }
        });

      }, 700);

    </script> -->
    
<iframe src="" style="width: 1000px; height: 500px; border: none;">


</td>
</tr>
    </table>

    <!-- Left Column End -->

</td>
<td><img src="/images/glass_dot.gif" height="1" width="15"></td>
<td valign="top" align="right">

<!-- Right Column -->

		



</td>
</tr>


</table>
            </div>

			
			</body>
</html>


