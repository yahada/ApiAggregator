# API Aggregator

Консольное Java-приложение, которое опрашивает три открытых REST API, приводит полученные данные к единой модели и сохраняет их в файл (JSON или CSV). Приложение поддерживает чтение файла и фильтрацию по источнику.

## Используемые технологии
* **Java 21** (включая `java.time` и `java.net.http.HttpClient`)
* **Gradle** (система сборки)
* **Jackson** (библиотека для парсинга и работы с JSON)
* **Apache Commons CSV** (работа с CSV-файлами)
* **SLF4J** (логирование)
* **Lombok** (сокращение шаблонного кода)
* **Java NIO** (работа с файловой системой: `Files`, `Path`)

## Выбранные API

В соответствии с требованиями задания, каждое API принимает query-параметры, возвращает JSON с более чем 5 полями, содержащими вложенные объекты или массивы, и данные которых изменяются со временем.

### 1. USGS Earthquake Hazards (Землетрясения)
**URL:** `https://earthquake.usgs.gov/fdsnws/event/1/query`

Возвращает данные о землетрясениях в формате GeoJSON (вложенные объекты `geometry`, `properties` с массивами координат).

**Доступные параметры:**

| Параметр | Описание |
|----------|----------|
| `starttime` | Начальная дата/время (ISO8601, например `2026-11-01` или `2026-11-01T00:00:00`) |
| `endtime` | Конечная дата/время (ISO8601) |
| `updatedafter` | Фильтр по времени последнего обновления записи (ISO8601) |
| `minmagnitude` | Минимальная магнитуда (например `4.5`) |
| `maxmagnitude` | Максимальная магнитуда |
| `minlatitude` | Минимальная широта (bounding box) |
| `maxlatitude` | Максимальная широта (bounding box) |
| `minlongitude` | Минимальная долгота (bounding box) |
| `maxlongitude` | Максимальная долгота (bounding box) |
| `latitude` | Широта центра круга (radius search) |
| `longitude` | Долгота центра круга (radius search) |
| `maxradiuskm` | Радиус поиска в км от заданной точки |
| `mindepth` | Минимальная глубина в км |
| `maxdepth` | Максимальная глубина в км |
| `orderby` | Сортировка: `time`, `time-asc`, `magnitude` и др. |
| `limit` | Максимальное количество результатов (по умолчанию 20000) |
| `eventtype` | Тип события: `earthquake`, `quarry blast` и др. |
| `format` | Формат ответа: `geojson` (обязательно, иначе XML/QuakeML) |

**Параметры по умолчанию в реализации:** `limit=5`, `orderby=time`, `format=geojson` (добавляется автоматически).

---

### 2. Hacker News Algolia (Новости)
**URL:** `https://hn.algolia.com/api/v1/search`

Возвращает результаты поиска по статьям Hacker News (массив `hits` с вложенными объектами авторов и метаданными).

**Доступные параметры:**

| Параметр | Описание |
|----------|----------|
| `query` | Полнотекстовый поиск по заголовкам, URL и тексту |
| `tags` | Фильтр по типу контента: `story`, `comment`, `poll`, `show_hn`, `ask_hn`, `job`, `front_page`; также `author_USERNAME`, `story_ID` |
| `numericFilters` | Числовые фильтры: `created_at_i` (временная метка), `points`, `num_comments`. Например `points>100` |
| `page` | Номер страницы (начиная с 0) |
| `hitsPerPage` | Количество результатов на странице (максимум 1000) |

**Параметры по умолчанию в реализации:** `query=java`, `hitsPerPage=5`.

---

### 3. Kitsu Anime (Аниме)
**URL:** `https://kitsu.io/api/edge/anime`

Возвращает каталог аниме в формате JSON API (вложенные объекты `data`, `attributes`, `relationships` с массивами).

**Доступные параметры:**

| Параметр | Описание |
|----------|----------|
| `filter[text]` | Текстовый поиск аниме по названию |
| `filter[season]` | Сезон: `winter`, `spring`, `summer`, `fall` |
| `filter[seasonYear]` | Год сезона |
| `filter[status]` | Статус: `current`, `finished`, `tba`, `unreleased`, `upcoming` |
| `filter[subtype]` | Тип: `TV`, `movie`, `OVA`, `ONA`, `special`, `music` |
| `filter[categories]` | Категории/жанры |
| `sort` | Сортировка по атрибуту; префикс `-` — по убыванию (например `-averageRating`) |
| `page[limit]` | Количество результатов (максимум 20) |
| `page[offset]` | Смещение (пагинация) |

**Параметры по умолчанию в реализации:** `page[limit]=5`.

## Единая модель данных

Все источники приводятся к единой записи `DataRecord`:

| Поле | Тип | Описание |
|------|-----|----------|
| `source` | `String` | Идентификатор источника (`usgs`, `hackernews`, `kitsu`) |
| `entityType` | `EntityType` | Тип сущности (`EARTHQUAKE`, `NEWS`, `ANIME`) |
| `entityId` | `String` | Уникальный идентификатор записи в источнике |
| `observedAt` | `Optional<Instant>` | Время события (если доступно) |
| `metrics` | `Map<String, Object>` | Произвольные метрики (магнитуда, рейтинг, заголовок и т.д.) |

## Архитектура

Проект разделён на слои для соблюдения принципов ООП и разделения ответственности:

* **`cli`** — командная строка: парсинг аргументов (`InitialArgsParser`, `ShowCommandParser`), диспетчеризация команд (`CliApplication`), вывод данных (`RecordPrinter`), подготовка файлов (`OutputFilePreparer`).
* **`core.aggregator`** — бизнес-логика: `Aggregator` координирует запросы и хранение, `AggregateService` обрабатывает ошибки при опросе источников, `AggregatorBuilder` собирает зависимости.
* **`core.source`** — реализации `ApiSource` для каждого API (Стратегия), зарегистрированные через `ServiceLoader`; `AbstractHttpApiSource` обеспечивает шаблонный метод запроса.
* **`core.http`** — HTTP-слой: `HttpResponder` выполняет запросы с retry-политикой, `HttpRequestFactory` создаёт запросы, `RetryPolicy` управляет повторами.
* **`core.storage`** — интерфейс `DataStorage` и реализации `JsonDataStorage`, `CsvDataStorage`; формат определяется по расширению файла.
* **`core.model`** — модель данных `DataRecord`, типы сущностей `EntityType`, параметры запроса `RequestParams`, регистр источников `SourceTypeRegistry`.
* **`core.exception`** — иерархия исключений: `AppException` → `ParsingException`, `ApiException`, `StorageException`.

*Новый API добавляется созданием нового класса, реализующего `ApiSource`, без изменения существующей логики (OCP).*

## Сборка и запуск

### Сборка проекта
```bash
./gradlew clean build
```

### Запуск проекта

#### 1. Опрос всех API и сохранение в JSON (новый файл)
```bash
./gradlew run --args="data sources=usgs,hackernews,kitsu format=json mode=new file=out.json"
```

#### 2. Опрос выбранных API и сохранение в CSV
```bash
./gradlew run --args="data sources=usgs,kitsu format=csv mode=new file=out.csv"
```

#### 3. Дозапись в существующий файл (режим APPEND)
```bash
./gradlew run --args="data sources=usgs format=json mode=append file=out.json"
```

#### 4. Просмотр всего файла
```bash
./gradlew run --args="show out.json"
```

#### 5. Просмотр только определённого источника
```bash
./gradlew run --args="show out.json API=usgs"
```

### Параметры команды `data`

| Параметр | Обязательный | Описание |
|----------|:---:|----------|
| `sources` | ✅ | Комма-разделённый список API: `usgs`, `hackernews`, `kitsu` |
| `format` | ✅ | Формат вывода: `json` или `csv` |
| `mode` | ✅ | Режим записи: `new` (создать/перезаписать) или `append` (дозаписать) |
| `file` | ❌ | Имя выходного файла (по умолчанию `out.<формат>`) |

### Параметры команды `show`

| Параметр | Обязательный | Описание |
|----------|:---:|----------|
| `<file>` | ✅ | Путь к файлу с данными (`.json` или `.csv`) |
| `API` | ❌ | Фильтр по источнику: `usgs`, `hackernews`, `kitsu` |

## Обработка ошибок

При неверных аргументах программа выводит понятное сообщение об ошибке и синтаксис команды. Если внешнее API недоступно, агрегатор логирует ошибку и продолжает опрос остальных источников. Все исключения организованы в иерархию: `AppException` → `ParsingException` (парсинг CLI), `ApiException` (HTTP/API), `StorageException` (файловый ввод-вывод).