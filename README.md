
## Проект Explore with me

Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. Сложнее всего в таком планировании поиск информации и переговоры. Нужно учесть много деталей: какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться.

Приложение, Explore with me — афиша. В этой афише можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.

Было создано два сервиса
- **основной сервис** будет содержать всё необходимое для работы продукта;
- **сервис статистики** будет хранить количество просмотров и позволит делать различные выборки для анализа работы приложения.

API основного сервиса разделите на три части:

- **публичная** будет доступна без регистрации любому пользователю сети;
- **закрытая** будет доступна только авторизованным пользователям;
- **административная** — для администраторов сервиса.

## Спецификация API

Для обоих сервисов мы разработали подробные спецификации API:

- спецификация основного сервиса: [ewm-main-service-spec.json](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json);
- спецификация сервиса статистики: [ewm-stats-service.json](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json).

Для работы с ними вам понадобится редактор Swagger.

```
docker run -p 9090:8080 -e API_URL=https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json  swaggerapi/swagger-ui
```

```
docker run -p 9090:8080 -e API_URL=https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json  swaggerapi/swagger-ui
```

В качестве дополнительной фичи реализована функциональность "Комментарии" - Возможность оставлять комментарии к событиям и модерировать их.


## Облачная среда

Проект адаптирован для работы в облачной среде. Использованы ключевые компоненты из экосистемы Spring Cloud:

- Spring Cloud Config Server — централизованное управление конфигурацией;
- Spring Cloud Eureka — сервис регистрации и обнаружения;
- Spring Cloud Gateway — API-шлюз для маршрутизации запросов.

Сервис Discovery использует стандартный порт 8761 - http://localhost:8761/
Все остальные сервисы получают динамические настройки сетевого порта.

Сервис Config предоставляет настройки для микро сервисов, размещенные в ресурсных папках
```
searchLocations:  
  - classpath:config/core/{application}  
  - classpath:config/stats/{application}  
  - classpath:config/infra/{application}
```


## Микросервисы

Пользователей стало гораздо больше, и нагрузка на сервис `ExploreWithMe` увеличилась.  Появились следующие проблемы:
- **Ограниченная масштабируемость.** Монолитная архитектура не позволяет эффективно распределять нагрузку. Например, высокий трафик на функциональность поиска событий может замедлить работу всего приложения.
- **Сложность изменений.** Любая доработка кода может затронуть другие части системы, увеличивая вероятность ошибок. Исправление багов или добавление новой функциональности становится долгим и рискованным процессом.
- **Снижение надёжности.** Если одна часть системы перестаёт работать, это может вывести из строя весь монолит. Например, сбой в управлении заявками на участие может заблокировать доступ ко всему сервису.
- **Нагрузка на инфраструктуру.** С ростом числа пользователей и событий однотипные запросы (например, поиск событий) перегружают серверы. Это приводит к увеличению времени ответа и ухудшению пользовательского опыта.

#### Архитектура микросервисов

Основной монолитный проект был преобразован в набор микросервисов, функциональность основного сервиса была разделена  на отдельные логические модули:
- управление мероприятиями `event-service`;
- управление заявками на участие `request-service`;
- администрирование пользователями`user-service`;
- дополнительная функциональность - категории `category-service`, комментарии `commentr-service`, выборки мероприятий `compilation-service`.
Общие классы, связанные с обработкой ошибок, логирования (АОП) и настройка для библиотеки Jakson вынесены в отдельный модуль - библиотеку `interaction-api`

#### Базы данных

Хранения данных в микросервисной среде реализована с использованием СУБД Postgre
```
docker run -d --name postgres-ewm-plus -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=ewm_stats_db postgres:16.1

```

Каждый сервис использует отдельную БД
```
create database ewm_main_db;
create database ewm_stats_db;


CREATE DATABASE ewm_event;
CREATE DATABASE ewm_request;
CREATE DATABASE ewm_user;
CREATE DATABASE ewm_compilation;
CREATE DATABASE ewm_category;
CREATE DATABASE ewm_comment;
CREATE DATABASE ewm_subscription;
CREATE DATABASE ewm_rating;
CREATE DATABASE ewm_location;

```


## Feign-клиент

Для реализации взаимодействия микросервисов срезе HTTP использован механизм экосистемы Spring Cloud.

Для внедрения feign клиентов был выбран подход без общих интерфейсов и с дублированием  dto.

#### Взаимодействие микросервисов:


`event-service`
- `category-service` 
- `user-service`
- `request-service`
- `Stat-server`

`request-service`
- `event-service`
- `user-service`

`category-service`
- `event-service`

`compilation-service`
- `event-service`

`comment-service`
- `event-service`
- `user-service`




