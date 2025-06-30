document.addEventListener('DOMContentLoaded', function () {
    var collectionsMap = /*[[${produtosPorColecao}]]*/ {}; 

    for (var collectionName in collectionsMap) {
        if (collectionsMap.hasOwnProperty(collectionName)) {
            var swiperId = 'swiper-collection-' + collectionName.replace(/ /g, '-').replace(/\//g, '-');
            var swiperElement = document.getElementById(swiperId); // Obter a referência direta ao elemento Swiper

            if (swiperElement) {
                new Swiper(swiperElement, { // Passe o elemento DOM diretamente, não a string do seletor
                    slidesPerView: 4,
                    spaceBetween: 20,
                    loop: false,
                    pagination: {
                        el: swiperElement.querySelector('.swiper-pagination'), // Encontre a paginação DENTRO deste elemento Swiper
                        clickable: true,
                    },
                    navigation: {
                        nextEl: swiperElement.querySelector('.swiper-button-next'), // Encontre o next DENTRO deste elemento Swiper
                        prevEl: swiperElement.querySelector('.swiper-button-prev'), // Encontre o prev DENTRO deste elemento Swiper
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