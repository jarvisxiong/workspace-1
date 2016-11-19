$(document).ready(function() {

    var i = 0;
    var defaultHeight = 480;
    var divVideo = $('#vid-0');
    var divCarousel = $('#carousel-div');
    var height = window.screen.height;

    if (/iP(hone|od|ad)/.test(navigator.platform)) {

        if(window.orientation == 0){
            height = window.screen.height;
        }else{
            height = window.screen.width;
        }
    }


    if(height <= defaultHeight){

        var divVideo = $('#vid-0');
        var divCarousel = $('#carousel-div');
        var tempVideo = divVideo.clone();
        var tempCarousel = divCarousel.clone();
        divVideo.replaceWith(tempCarousel);
        divCarousel.replaceWith(tempVideo);
        i = 1;

    }


    $(window).on("orientationchange",function(){

        if(window.orientation == 0){

            if (/iP(hone|od|ad)/.test(navigator.platform)) {

                var height = window.screen.height;
            } else {
                var height = window.screen.height;
            }

        } else {

            if (/iP(hone|od|ad)/.test(navigator.platform)) {
                var height = window.screen.width;
            } else {
                var height = window.screen.height;
            }

        }

        if(height <= defaultHeight){

            if(i != 1){

                var divVideo = $('#vid-0');
                var divCarousel = $('#carousel-div');
                var tempVideo = divVideo.clone();
                var tempCarousel = divCarousel.clone();
                divVideo.replaceWith(tempCarousel);
                divCarousel.replaceWith(tempVideo);

                i = 1;

            }

        }

        if(height > defaultHeight){

            if(i == 1){

                var divVideo = $('#vid-0');
                var divCarousel = $('#carousel-div');
                var tempVideo = divVideo.clone();
                var tempCarousel = divCarousel.clone();
                divVideo.replaceWith(tempCarousel);
                divCarousel.replaceWith(tempVideo);

                i = 0;

            }

        }

    });

});