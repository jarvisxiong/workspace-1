$(document).ready(function() {

    $('.responsive').slick({
        dots: false,
        infinite: false,
        speed: 300,
        slidesToShow: 2,
        slidesToScroll: 2,
        draggable: false,
        touchMove: false,
        responsive: [{
            breakpoint: 1024,
            settings: {
                slidesToShow: 2,
                slidesToScroll: 2,
                infinite: false,
                dots: false
            }
        }, {
            breakpoint: 600,
            settings: {
                slidesToShow: 2,
                slidesToScroll: 2
            }
        }, {
            breakpoint: 480,
            settings: {
                slidesToShow: 2,
                slidesToScroll: 2
            }
        }]
    });

    

});