## Android приложение раскраска по номерам

### Описание

Приложение представляет собой игру-раскраску по номерам.
Оно состоит состоит из трех основных функциональных частей: «Галерея», «Мои работы», «Загрузка изображений».
Раздел «Галерея» представляет собой список предустановленных раскрасок на выбор.

Раздел «Мои работы» представляет собой список уже начатых к заполнению раскрасок из общего списка и собственно загруженных изображений.

Раздел «Загрузка изображений» позволяет загружать изображения и выбирать соответствующую обработку для раскрасок (деление на шестиугольники или треугольники, наложение фильтра черно-белого изображения или изменения полностью цветовой палитры относительно выбранного целевого цвета)

Пользователь выбирает картинку, которую хочет раскрасить, и красит ее по номерам согласно выбранной палитре и целевому цвету. 



### Структура и особенности реализации проекта

Проект реализован с использованием Single Activity подхода с использованием Jetpack Compose и архитектуры MVVM (Model-View-ViewModel).

`/data` - пакет отвечающий за взаимодействие с данными об изображениях, фигурах (шестиугольниках и треугольниках), которые необходимо раскрашивать.
Слой данных состоит из репозиториев и DAO, ответсвенных непосредственно за взаимодействие с Room db. Так же сущности Triangle и Hexagon для хранение в БД 
преобразуются в TriangleEntity и HexagonEntity соответственно предстваленных в `/model`.

`/service` -- хранит сервисы для обработки изображений

* _HexgonService_ -- делит изображение на шестиугольники, алгоритм основан на расчёте примерного радиуса шестиугольника используя ширину и высоту изображения, 
а так же количества шестиугольников для раскраски введенным пользователем.

* _TriangleService_ -- реализует алгоритм триангуляции Боуэра-Ватсона c использованием 
алгоритма нахождения «супер треугольника» через минимальную окружность, заключающийся 
в нахождении минимальной окружности с помощью алгоритма Вельцля

* _FilterService_ -- реализует функционал смены палитры изображения на основе выбранного цвета используя HSV модель.
Алгоритм, основываясь на преобладающем цвете изображения и целевом, меняет пропорционально оттенок, насыщенность и яркость 
каждого пиксель изображения.

`/utils` -- содержит дополнительных функционал для удобного преобразования классов фигур в класс для хранения в БД

###  Примеры:

Пример преобразования изображения в серые и зеленые оттенки:

Искомое изображение: 

![img_6.png](img/img_6.png)

Оттенки:

![img_4.png](img/img_4.png)

![img_5.png](img/img_5.png)


Интрерфейс экрана раскраски:

![new.gif](img/ggg.gif)


### Cтек технологий

- Kotlin
- Jetpack Compose
- Android SDK
- Room
- Coroutines
- Dagger Hilt