# mpm-persistent-ds

Курсовой проект по дисциплине "Современные методы программирования" - "Persistent data structures"

## Задание

Реализовать библиотеку со следующими структурами данных в persistent-вариантах (с единым API для всех структур)

### Базовые требования

- [ ] Массив (константное время доступа, переменная длина)
- [ ] Двусвязный список
- [ ] Ассоциативный массив (на основе Hash-таблицы, либо бинарного дерева)

### Дополнительные требования

- [ ] Обеспечить произвольную вложенность данных (по аналогии с динамическими языками), не отказываясь при этом
  полностью от типизации посредством generic/template;
- [ ] Реализовать универсальный undo-redo механизм для перечисленных структур с поддержкой каскадности (для вложенных
  структур);
- [ ] Реализовать более эффективное по скорости доступа представление структур данных, чем fat-node;
- [ ] Расширить экономичное использование памяти на операцию преобразования одной структуры к другой (например, списка в
  массив);
- [ ] Реализовать поддержку транзакционной памяти (STM).

### Ответственные

- Кондакова Дарья Алексеевна, гр. 22225
- Хорошавин Алексей Константинович, гр. 22222

## Реализация

### Стек технологий

- Java 17.0.4
- Gradle 7.4
- Junit Jupiter - для тестов

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
        <th>Ответственный</th>
    </tr>
    <tr>
        <td>до 24.11.2022</td>
        <td>
            Создание каркаса проекта;<br/> Создание репозитория на GitHub;<br/> Настройка минимального CI/CD с помощью Github Actions.
        </td>
        <td>Хорошавин А.К.</td>
    </tr>
    <tr>
        <td rowspan="2">до 08.12.2022</td>
        <td>Реализация базовой функциональности:<ul><li>массив</li><li>двусвязный список</li><li>unit-тесты</li></ul></td>
        <td>Кондакова Д.А.</td>
    </tr>
    <tr>
        <td>Реализация базовой функциональности:<ul><li>ассоциативный массив</li><li>обеспечение единого api</li><li>unit-тесты</li></ul></td>
        <td>Хорошавин А.К.</td>
    </tr>
    <tr>
        <td rowspan="2">до 24.12.2022</td>
        <td>Реализация дополнительной функциональности:<ul><li>произвольная вложенность данных</li><li>более эффективное по скорости доступа представление структур данных, чем fat-node</li><li>unit-тесты</li></ul></td>
        <td>Кондакова Д.А.</td>
    </tr>
    <tr>
        <td>Реализация дополнительной функциональности:<ul><li>универсальный undo-redo механизм для перечисленных структур с поддержкой каскадности</li><li>более эффективное по скорости доступа представление структур данных, чем fat-node</li><li>unit-тесты</li></ul></td>
        <td>Хорошавин А.К.</td>
    </tr>
</table>