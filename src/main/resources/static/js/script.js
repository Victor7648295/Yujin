let selectedCondition = "";

/* =========================
   СОСТОЯНИЕ (Новое / Б/у)
========================= */
document.querySelectorAll(".condition-btn").forEach(btn => {
    btn.addEventListener("click", function () {

        // если нажали на уже активную кнопку — снимаем выбор
        if (this.classList.contains("active")) {
            this.classList.remove("active");
            selectedCondition = "";
            return;
        }

        // иначе переключаем
        document.querySelectorAll(".condition-btn")
            .forEach(b => b.classList.remove("active"));

        this.classList.add("active");
        selectedCondition = this.dataset.value;
    });
});


/* =========================
   ЗАПРЕТ ОТРИЦАТЕЛЬНЫХ ЦЕН
========================= */
const priceFromInput = document.getElementById("priceFrom");
const priceToInput = document.getElementById("priceTo");

function preventNegative(input) {
    input.addEventListener("input", function () {
        if (this.value < 0) {
            this.value = 0;
        }
    });
}

preventNegative(priceFromInput);
preventNegative(priceToInput);


/* =========================
   ФИЛЬТР
========================= */
function applyFilters() {

    const region = document.getElementById("regionFilter").value;
    const category = document.getElementById("categoryFilter").value;
    const priceFrom = priceFromInput.value ? parseInt(priceFromInput.value) : null;
    const priceTo = priceToInput.value ? parseInt(priceToInput.value) : null;
    const searchText = document.querySelector(".search input").value.toLowerCase();

    // Проверка: От > До
    if (priceFrom !== null && priceTo !== null && priceFrom > priceTo) {
        alert("Минимальная цена не может быть больше максимальной");
        return;
    }

    const ads = document.querySelectorAll(".ad-card");

    ads.forEach(ad => {

        const adPrice = parseInt(ad.dataset.price);
        const adRegion = ad.dataset.region;
        const adCategory = ad.dataset.category;
        const adCondition = ad.dataset.condition;
        const adTitle = ad.querySelector("h3").textContent.toLowerCase();

        let match = true;

        if (region && adRegion !== region) match = false;
        if (category && adCategory !== category) match = false;
        if (selectedCondition && adCondition !== selectedCondition) match = false;

        if (priceFrom !== null && adPrice < priceFrom) match = false;
        if (priceTo !== null && adPrice > priceTo) match = false;

        if (searchText && !adTitle.includes(searchText)) match = false;

        ad.style.display = match ? "flex" : "none";
    });
}

document.getElementById("applyFilter")
    .addEventListener("click", applyFilters);


/* =========================
   ПОИСК (по кнопке Найти)
========================= */
document.querySelector(".btn-dark")
    .addEventListener("click", applyFilters);


/* =========================
   ПОИСК при Enter
========================= */
document.querySelector(".search input")
    .addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            applyFilters();
        }
    });

// Переключение состояния
const conditionBtns = document.querySelectorAll('.condition-btn');
const conditionValue = document.getElementById('conditionValue');

conditionBtns.forEach(btn => {
    btn.addEventListener('click', () => {

        conditionBtns.forEach(b => b.classList.remove('active'));

        btn.classList.add('active');
        conditionValue.value = btn.dataset.value;
    });
});

// Ограничение цены (не меньше 0)
const priceInput = document.getElementById('price');

priceInput.addEventListener('input', () => {
    if (priceInput.value < 0) {
        priceInput.value = 0;
    }
});

// Превью фото
const photoInput = document.getElementById('photoInput');
const preview = document.getElementById('preview');

photoInput.addEventListener('change', () => {
    const file = photoInput.files[0];
    if (file) {
        preview.src = URL.createObjectURL(file);
        preview.style.display = 'block';
    }
});

// Проверка выбора состояния
document.getElementById('adForm').addEventListener('submit', function (e) {
    if (!conditionValue.value) {
        e.preventDefault();
        alert('Выберите состояние товара');
    }
});

// Обработка кнопки "Применить фильтр"
document.getElementById('applyFilter').addEventListener('click', function() {
    const region = document.getElementById('regionFilter').value;
    const category = document.getElementById('categoryFilter').value;
    const priceFrom = document.getElementById('priceFrom').value;
    const priceTo = document.getElementById('priceTo').value;

    // Получаем активную кнопку состояния
    const activeConditionBtn = document.querySelector('.condition-btn.active');
    const condition = activeConditionBtn ? activeConditionBtn.dataset.value : '';

    // Собираем URL с параметрами
    let url = '/filter?';
    const params = [];

    if (region && region !== '') {
        params.push('region=' + encodeURIComponent(region));
    }
    if (category && category !== '') {
        params.push('category=' + encodeURIComponent(category));
    }
    if (condition && condition !== '') {
        params.push('condition=' + encodeURIComponent(condition));
    }
    if (priceFrom && priceFrom !== '') {
        params.push('priceFrom=' + priceFrom);
    }
    if (priceTo && priceTo !== '') {
        params.push('priceTo=' + priceTo);
    }

    url += params.join('&');

    // Переходим по URL
    window.location.href = url;
});

// Обработка кнопок состояния (Новое/Б/у)
document.querySelectorAll('.condition-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        // Убираем active у всех кнопок
        document.querySelectorAll('.condition-btn').forEach(b => {
            b.classList.remove('active');
        });
        // Добавляем active на нажатую кнопку
        this.classList.add('active');
    });
});

// Сохраняем выбранное состояние при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    const selectedCondition = document.body.getAttribute('data-selected-condition');
    if (selectedCondition) {
        const activeBtn = document.querySelector(`.condition-btn[data-value="${selectedCondition}"]`);
        if (activeBtn) {
            activeBtn.classList.add('active');
        }
    }
});

    // Обработка кнопок состояния (Новое/Б/у)
    document.querySelectorAll('.condition-btn').forEach(btn => {
    btn.addEventListener('click', function(event) {
        event.preventDefault();  // Предотвращаем отправку формы

        // Убираем active у всех кнопок
        document.querySelectorAll('.condition-btn').forEach(b => {
            b.classList.remove('active');
        });

        // Добавляем active на нажатую кнопку
        this.classList.add('active');

        // Записываем выбранное значение в скрытое поле
        const conditionValue = this.getAttribute('data-value');
        document.getElementById('conditionInput').value = conditionValue;

        console.log('Выбрано состояние:', conditionValue);
    });
});

    // При загрузке страницы проверяем, есть ли выбранное состояние
    document.addEventListener('DOMContentLoaded', function() {
    const activeBtn = document.querySelector('.condition-btn.active');
    const conditionInput = document.getElementById('conditionInput');

    if (activeBtn) {
    const conditionValue = activeBtn.getAttribute('data-value');
    conditionInput.value = conditionValue;
    console.log('Загружено состояние:', conditionValue);
} else {
    conditionInput.value = '';
}
});

    // Отладка: перед отправкой формы показываем какие данные уходят
    document.getElementById('filterForm').addEventListener('submit', function() {
    const condition = document.getElementById('conditionInput').value;
    const region = document.getElementById('regionFilter').value;
    const category = document.getElementById('categoryFilter').value;
    const priceFrom = document.getElementById('priceFrom').value;
    const priceTo = document.getElementById('priceTo').value;

    console.log('Отправка фильтра:');
    console.log('  region:', region);
    console.log('  category:', category);
    console.log('  condition:', condition);
    console.log('  priceFrom:', priceFrom);
    console.log('  priceTo:', priceTo);
});




