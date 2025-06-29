/*<![CDATA[*/
document.addEventListener('DOMContentLoaded', function () {
    var collectionsMap = /*[[${produtosPorColecao}]]*/ {}; 
    
    for (var collectionName in collectionsMap) {
        if (collectionsMap.hasOwnProperty(collectionName)) {
            var swiperId = 'swiper-collection-' + collectionName.replace(/ /g, '-').replace(/\//g, '-');
            
            // Verifica se o elemento do Swiper existe antes de inicializar
            if (document.getElementById(swiperId)) {
                new Swiper('#' + swiperId, {
                    slidesPerView: 4,
                    spaceBetween: 20,
                    loop: false,
                    pagination: {
                        el: '#' + swiperId + ' .swiper-pagination',
                        clickable: true,
                    },
                    navigation: {
                        nextEl: '#' + swiperId + ' .swiper-button-next',
                        prevEl: '#' + swiperId + ' .swiper-button-prev',
                    },
                    observer: true,
                    observeParents: true,
                    breakpoints: {
                        0: { slidesPerView: 1, spaceBetween: 10 },
                        600: { slidesPerView: 2, spaceBetween: 20 },
                        1024: { slidesPerView: 3, spaceBetween: 20 },
                        1200: { slidesPerView: 4, spaceBetween: 30 }
                    },
                });
            } else {
                console.warn("Elemento Swiper com ID '" + swiperId + "' não encontrado. Verifique seu HTML e dados.");
            }
        }
    }
});
/*]]>*/                                             