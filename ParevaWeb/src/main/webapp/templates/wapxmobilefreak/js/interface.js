$(document).ready(function(){

    $('#left-panel-trigger').click(function(){      		$('#left-slide').show();    });
    $('#left-panel-trigger-default').click(function(){      $('#left-slide-default').show();    });
    $('#left-slide').click(function(){              		$('#left-slide').hide();    });
    $('#left-slide-default').click(function(){              $('#left-slide-default').hide();    });
    $('#right-panel-trigger').click(function(){     		$('#right-slide').show();   });
    $('#right-panel-trigger-default').click(function(){     $('#right-slide-default').show();   });
    $('#right-slide').click(function(){             		$('#right-slide').hide();   });
    $('#right-slide-default').click(function(){             $('#right-slide-default').hide();   });

    /* GA event for slick-carousel click */
    $('#slick-wrapper .slick-prev').live('click', function() { gaqEvent('NAV', 'Scroller_click_left', ''); });
    $('#slick-wrapper .slick-next').live('click', function() { gaqEvent('NAV', 'Scroller_click_right', ''); });
    $('#slick-wrapper .slick-carousel-image').live('click', function() { gaqEvent('CONV', 'Scroller_click', ''); });
    // GA events
    $('.categoryholder a.category').click(function(){ gaqEvent('NAV', 'Category_click', ''); });
    $('.logo-holder').click(function()  { gaqEvent('NAV', 'Top_logo_click', ''); });
    $('.link-homepage').click(function(){ gaqEvent('NAV', 'Menu_Home', '') });
    $('.link-categories').click(function(){ gaqEvent('NAV', 'Menu_Category', '') });
    $('.icon-date').click(function()    { gaqEvent('NAV', 'Filter_Button', 'date') });
    $('.icon-length').click(function()  { gaqEvent('NAV', 'Filter_Button', 'length') });
    $('.icon-rating').click(function()  { gaqEvent('NAV', 'Filter_Button', 'rating') });
    $('.button1 a').click(function()    { gaqEvent('NAV', 'Browse_Previous', '') });
    $('.button2 a').click(function()    { gaqEvent('NAV', 'Browse_Next', '') });
    $('#videoContent .video a.billable').click(function()
                                        { gaqEvent('CONV', 'VideoList_Thumb_Click', '');  }) ;
    $('#scroller a').bind('click', function() { gaqEvent('CONV', 'Carousel_Item_Click', ''); });

    $('#scroller.middle').bind('click', function() { gaqEvent('CONV', 'Middle_Carousel_Click', ''); }); // Carousel

    /** GA events for paid category thumbnails on homepage */
    $('#categoryThumbsWrapper #seeAll').bind('click', function() { gaqEvent('CONV', 'HP_Cat_SeeAll', ''); });
    $('#categoryThumbsWrapper .HP_Cat_Amateur').bind('click', function() { gaqEvent('CONV', 'HP_Cat_Amateur', ''); });
    $('#categoryThumbsWrapper .HP_Cat_AssLicking').bind('click', function() { gaqEvent('CONV', 'HP_Cat_AssLicking', ''); });
    $('#categoryThumbsWrapper .HP_Cat_BigButt').bind('click', function() { gaqEvent('CONV', 'HP_Cat_BigButt', ''); });
    $('#categoryThumbsWrapper .HP_Cat_BigCock').bind('click', function() { gaqEvent('CONV', 'HP_Cat_BigCock', ''); });
    $('#categoryThumbsWrapper .HP_Cat_Blowjob').bind('click', function() { gaqEvent('CONV', 'HP_Cat_Blowjob', ''); });
    $('#categoryThumbsWrapper .HP_Cat_Party').bind('click', function() { gaqEvent('CONV', 'HP_Cat_Party', ''); });
    $('#categoryThumbsWrapper .HP_Cat_Tattoo').bind('click', function() { gaqEvent('CONV', 'HP_Cat_Tattoo', ''); });
    $('#categoryThumbsWrapper .HP_Cat_Threesome').bind('click', function() { gaqEvent('CONV', 'HP_Cat_Threesome', ''); });

    $('#categoryThumbsWrapper .HP_Cat_anal').bind('click', function() { gaqEvent('CONV', 'HP_Cat_anal', ''); });
    $('#categoryThumbsWrapper .HP_Cat_teen').bind('click', function() { gaqEvent('CONV', 'HP_Cat_teen', ''); });
    $('#categoryThumbsWrapper .HP_Cat_milf').bind('click', function() { gaqEvent('CONV', 'HP_Cat_milf', ''); });
    $('#categoryThumbsWrapper .HP_Cat_mature').bind('click', function() { gaqEvent('CONV', 'HP_Cat_mature', ''); });
    $('#categoryThumbsWrapper .HP_Cat_lesbian').bind('click', function() { gaqEvent('CONV', 'HP_Cat_lesbian', ''); });
    $('#categoryThumbsWrapper .HP_Cat_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_bigtits').bind('click', function() { gaqEvent('CONV', 'HP_Cat_bigtits', ''); });
    $('#categoryThumbsWrapper .HP_Cat_squirt').bind('click', function() { gaqEvent('CONV', 'HP_Cat_squirt', ''); });


    /** GA events for soft paid category thumbnails on homepage */
    $('#categoryThumbsWrapper .HP_Cat_big_ass_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_big_ass_soft', ''); });
    $('#categoryThumbsWrapper .HP_Cat_big_tits_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_big_tits_soft', ''); });
    $('#categoryThumbsWrapper .HP_Cat_blonde_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_blonde_soft', ''); });
    $('#categoryThumbsWrapper .HP_Cat_brunette_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_brunette_soft', ''); });
    $('#categoryThumbsWrapper .HP_Cat_black_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_black_soft', ''); });
    $('#categoryThumbsWrapper .HP_Cat_asian_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_asian_soft', ''); });
    $('#categoryThumbsWrapper .HP_Cat_milf_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_milf_soft', ''); });
    $('#categoryThumbsWrapper .HP_Cat_teen_soft').bind('click', function() { gaqEvent('CONV', 'HP_Cat_teen_soft', ''); });

    /** GA events for gay paid category thumbnails on homepage */
    $('#categoryThumbsWrapper .HP_Cat_bareback_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_bareback_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_bigDick_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_bigDick_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_group_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_group_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_latino_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_latino_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_tattoo_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_tattoo_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_brunette_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_brunette_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_Straight_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_Straight_gay', ''); });
    $('#categoryThumbsWrapper .HP_Cat_Twink_gay').bind('click', function() { gaqEvent('CONV', 'HP_Cat_Twink_gay', ''); });

    /** GVI shield tracking */
    if ($('.non-age-verified-shield').length){
        gaqEvent('GVI_SHIELD', 'Impresssion');
    }

    $('.non-age-verified-shield #non-av-image').bind('click', function() { gaqEvent('GVI_SHIELD', 'Click', ''); });


});