# mpm-persistent-ds

Курсовой проект по дисциплине "Современные методы программирования" - "Persistent data structures"

## Задание

Реализовать библиотеку со следующими структурами данных в persistent-вариантах (с единым API для всех структур)

### Базовые требования

- [x] Массив (константное время доступа, переменная длина)
- [x] Двусвязный список
- [x] Ассоциативный массив (на основе Hash-таблицы, либо бинарного дерева)
- [x] Должен быть единый API для всех структур, желательно использовать естественный
  API для выбранной платформы

### Дополнительные требования

- [x] Обеспечить произвольную вложенность данных (по аналогии с динамическими языками), не отказываясь при этом
  полностью от типизации посредством <b>generic/template</b>;
- [x] Реализовать универсальный undo-redo механизм для перечисленных структур с поддержкой каскадности (для вложенных
  структур);
- [x] Реализовать более эффективное по скорости доступа представление структур данных, чем fat-node.
- [ ] Расширить экономичное использование памяти на операцию преобразования одной
  структуры к другой (например, списка в массив)
- [ ] Реализовать поддержку транзакционной памяти (STM)

### Ответственные

- Кондакова Дарья Алексеевна, гр. 22225
- Хорошавин Алексей Константинович, гр. 22222

## Реализация

### Предполагаемый путь решения

1. Найти соответствующие алгоритмы;
2. Подобрать и изучить публикации по теме Persistent Data Structures;
3. Изучив теорию по персистентным структурам данных, реализовать их в проекте с использованием выбранного стека
   технологий.

### Календарный план

<table>
    <tr>
        <th>Сроки</th>
        <th>Этап работы</th>
    </tr>
    <tr>
        <td>до 24.11.2022</td>
        <td>
            <ul><li>Создание каркаса проекта</li><li>Создание репозитория на GitHub</li><li>Настройка минимального CI/CD с помощью Github Actions</li></ul>
        </td>
    </tr>
    <tr>
        <td>до 08.12.2022</td>
        <td>Реализация базовой функциональности:<ul><li>Массив (Persistent Array)</li><li>Двусвязный список (Persistent Linked List)</li><li>Ассоциативный массив (Persistent Map)</li><li>Единый API для всех структур</li></ul></td>
    </tr>
    <tr>
        <td>до 24.12.2022 (что успеем)</td>
        <td>Реализация дополнительной функциональности:<ul><li>Произвольная вложенность данных</li><li>Более эффективное по скорости доступа представление структур данных, чем fat-node</li><li>Универсальный undo-redo механизм для перечисленных структур с поддержкой каскадности</li><li>Более эффективное по скорости доступа представление структур данных, чем fat-node</li></ul></td>
    </tr>
</table>

## Теоретическая часть

### Алгоритм работы персистентных структур данных

Неизменяемые структуры данных сохраняют свои предыдущие версии при изменении и,
следовательно, являются фактически неизменными.

Полностью постоянные структуры данных допускают как обновления, так и запросы к любой версии.
Многие операции вносят лишь небольшие изменения. Поэтому просто копирование предыдущей версии было бы неэффективным.

Чтобы сэкономить время и память, важно определить сходство между двумя версиями и переиспользовать как можно больший
объем данных.
Чаще всего в литературе о реализации неизменяемых структур данных встречаются два алгоритма Fat node и Path copying, а
также их комбинации и улучшения.

#### Fat node

Суть алгоритма состоит в том, чтобы записывать все изменения, внесенные в поля узлов в самих узлах, без удаления старых
значений полей.

Главными проблемами данного алгоритма являются большой объем занимаемой памяти и амортизация времени для сохранения
модификации из-за увеличения размеров узлов.

#### Path copying

При использовании данного алгоритма создаются копии каждого узла встреченного на пути к измененному узлу.
Поэтому для каждого изменения будет создан новый корень, по сути являющийся "новой версией" структуры данных.

В данной статье также описывается комбинация рассмотренных алгоритмов с привязкой
к частному случаю деревьев
поиска: [Making data structures persistent](https://www.sciencedirect.com/science/article/pii/0022000089900342).

### Реализация с использованием B-деревьев

Так как одно из дополнительных требований - применить более эффективное представление
по сравнению с fat-node, то будем использовать path coping с применением структуры
B-деревьев.

Данная реализация описана в
лекции: [1. Persistent Data Structures](https://www.youtube.com/watch?v=T0yzrZL1py0&list=PLUl4u3cNGP61hsJNdULdudlRL493b-XZf&index=1&t=3118s)

### API структур

Для API можно использовать естественный вариант для известных структур в
Java, для этого можно обратиться к официальной документации:

* [List](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)
* [AbstractMap](https://docs.oracle.com/javase/7/docs/api/java/util/AbstractMap.html)

За основу для массива и списка было принято решение взять Java List, для мапы - Java Abstract Map.

Для обеспечения единого API (например, методы undo/redo) было решено добавить
асбтрактный
класс [AbstractPersistentCollection](https://github.com/aleksey-khoroshavin/mpm-persistent-ds/blob/develop/src/main/java/ru/nsu/fit/mpm/persistent_ds/collection/AbstractPersistentCollection.java),
имплементирующий
интерфейс [UndoRedoCollection](https://github.com/aleksey-khoroshavin/mpm-persistent-ds/blob/develop/src/main/java/ru/nsu/fit/mpm/persistent_ds/collection/UndoRedoCollection.java).

## Персистентный вектор

Рассматриваемые структуры связаны между собой, реализовав массив можно использовать похожий
механизм для списка, а для хеш таблицы мапы можно уже использовать двусвязный список.
Поэтому большая часть времени уйдет на реализацию персистентного массива.

В ходе поиска информации в Интернете
был найден набор статей, посвященных <b>персистентному вектору</b>. На примере данной структуры
описаны базовые алгоритмы, которые позволят понять, как их применить для массива, а затем списка и мапы.

Серия статей:<br>

* [Understanding Clojure's Persistent Vectors, pt. 1](https://hypirion.com/musings/understanding-persistent-vector-pt-1)
* [Understanding Clojure's Persistent Vectors, pt. 2](https://hypirion.com/musings/understanding-persistent-vector-pt-2)
* [Understanding Clojure's Persistent Vectors, pt. 3](https://hypirion.com/musings/understanding-persistent-vector-pt-3)
* [Understanding Clojure's Transients](https://hypirion.com/musings/understanding-clojure-transients)
* [Persistent Vector Performance Summarised](https://hypirion.com/musings/persistent-vector-performance-summarised)

В ходе изучения материала статей, были использованы структуры для хранения версий той
или иной коллекции. Описан механизм поиска элемента по индексам с использованием битовых масок и преобразований, что
дает оптимизацию операций над коллекциями.

