package lab5;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Класс для запуска программы
 */
public class Laba {

  public static void main(String[] args) {
    UI myUI = new UI();

    myUI.menu();
  }

}


/**
 * Интерфейс, реализующий нумерацию вариантов выбора для switch()
 */
interface menuEnum {

  int PRINT_FURNITURE = 1,
      ADD_VOID_FURNITURE = 2,
      ADD_FILLED_FURNITURE = 3,
      DELETE_FURNITURE = 4,
      EDIT_FURNITURE = 5,
      SORT_FURNITURE = 6,
      DELETE_DUPLICATES = 7,
      FILTER_BY_PRICE = 8,
      SUMM_PRICE = 9,
      MIN_MAX_AVERAGE = 10,
      MAX_PRICE = 11,
      GROUP_BY_COUNTRY = 12,
      EXIT = 13;

  int CHANGE_NAME = 1,
      CHANGE_COUNTRY = 2,
      CHANGE_YEAR = 3,
      CHANGE_PRICE = 4,
      EXIT_TO_MENU = 5;

  int SORT_BY_NAME = 1,
      SORT_BY_COUNTRY = 2,
      SORT_BY_YEAR = 3,
      SORT_BY_PRICE = 4;
}

/**
 * Основной класс интерфейса
 */
class UI implements menuEnum {


  /**
   * Поток вывода, поддерживающий русские символы
   */
  private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

  /**
   * Экземпляр класса содержащего методы ввода разных типов
   */
  private final Inputer inp = new Inputer();

  /**
   * Основной список людей
   */
  private ArrayList<Furniture> furnitureList = new ArrayList<>();


  /**
   * Метод основного меню работы
   */
  public void menu() {
    try {
      this.furnitureList.add(new Furniture());
      this.furnitureList.add(new Furniture());
      this.furnitureList.add(new Furniture());
      furnitureList.add(new Furniture(228, 2003, "Мегашкаф", "Украина"));
      furnitureList.add(new Furniture(1337, 2005, "Ультрашкаф", "Россия"));
      furnitureList.add(new Furniture(3600, 2007, "Ультрашкаф", "Россия"));
      furnitureList.add(new Furniture(5600, 2009, "Гигашкаф", "Канада"));
      furnitureList.add(new Furniture(69.96f, 2010, "Тумбус", "США"));

    } catch (NegativeFloatException | NegativeIntException ignored) {
    }

    int choice;
    do {
      this.out.println("""
          1. Вывести список мебели
          2. Добавить незаполненную мебель к списку
          3. Заполнить мебель
          4. Удалить мебель по индексу
          5. Отредактировать данные мебели по индексу
          6. Отсортировать список мебели
          7. Удалить дубликаты
          8. Фильтрация по максимальной цене
          9. Вывести общую стоимость мебели
          10. Вывести максимальное, минимальное и среднее значение года выпуска
          11. Вывести самую дорогую мебель
          12. Группировка по стране - производителю
          13. Завершить программу
          """);

      choice = inp.getInt();
      try {
        switch (choice) {
          case PRINT_FURNITURE -> printFurnitureList();
          case ADD_VOID_FURNITURE -> addVoidFurniture();
          case ADD_FILLED_FURNITURE -> addParamFurniture();
          case DELETE_FURNITURE -> deleteFurniture();
          case EDIT_FURNITURE -> editFurniture();
          case SORT_FURNITURE -> sortFurnitureList();
          case DELETE_DUPLICATES -> deleteDuplicates();
          case FILTER_BY_PRICE -> filterByPrice();
          case MIN_MAX_AVERAGE -> prodYearStat();
          case SUMM_PRICE -> sumByPrice();
          case MAX_PRICE -> printMaxPrice();
          case GROUP_BY_COUNTRY -> printGrouped();
          default -> {
            if (choice != EXIT) {
              this.out.println("Некорректный ввод");
            }
          }
        }
      } catch (NegativeIntException | NegativeFloatException | FurnitureParamException |
               IndexErrorException exc) {
        out.println("Ошибка ввода: \n" + exc.getMessage());
      } catch (AssertionError exc) {
        out.println(exc.getMessage());
      }

    } while (choice != EXIT);
  }

  /**
   * Метод, реализующий сортировку по выбранному в нем полю
   */
  private void sortFurnitureList() {
    this.out.println("""
        1. Сортировка по году прозводства
        2. Сортировка по цене
        3. Сортировка по стране производителе
        4. Сортировка по названию
        """);
    int choice = inp.getInt();
    switch (choice) {
      case SORT_BY_NAME -> furnitureList.sort(Comparator.comparing(Furniture::getName));
      case SORT_BY_COUNTRY -> furnitureList.sort(Comparator.comparing(Furniture::getCountry));
      case SORT_BY_YEAR -> furnitureList.sort(Comparator.comparing(Furniture::getProdYear));
      case SORT_BY_PRICE -> furnitureList.sort(Comparator.comparing(Furniture::getPrice));

      default -> System.out.println("Неизвестный параметр");
    }

  }

  /**
   * Метод удаления мебели из списка по индексу
   *
   * @throws IndexErrorException - исключение при неверном вводе индекса
   * @throws AssertionError      - исключение при попытке удаления мебели из пустого массива
   */
  private void deleteFurniture() throws IndexErrorException, AssertionError {
    assert !furnitureList.isEmpty() : "Список мебели пуст";
    out.println("Введите индекс мебели в списке:");
    int index = inp.getInt();
    if (index < 1 || index > furnitureList.size()) {
      throw new IndexErrorException("Индекс указан неверно: ", index);
    } else {
      this.out.println(furnitureList.get(index - 1).getName() + " удален(а) из списка");
      furnitureList.remove(index - 1);
    }
  }

  /**
   * Метод добавления мебели с задаваемыми параметрами
   *
   * @throws FurnitureParamException - исключение при неверном вводе полей мебели
   */
  private void addParamFurniture() throws FurnitureParamException {

    String name, country;
    float price;
    int prodYear;

    out.println("Введите название:");
    name = inp.getString();

    out.println("Введите страну:");
    country = inp.getString();

    out.println("Введите год:");
    prodYear = inp.getInt();

    out.println("Введите цену:");
    price = inp.getFloat();

    try {
      furnitureList.add(new Furniture(price,
          prodYear, name, country));
    } catch (NegativeIntException | NegativeFloatException exc) {
      throw new FurnitureParamException(
          "Какое-то из полей заполнено неверно", exc);
    }
  }

  /**
   * Метод добавления человека с незаполненными полями
   */
  private void addVoidFurniture() {
    furnitureList.add(new Furniture());
  }

  /**
   * Метод редактирования любых полей человека
   *
   * @throws NegativeFloatException - Исключение при неверном вводе цены
   * @throws NegativeIntException   -  Исключение при неверном вводе года производства
   * @throws AssertionError         - Исключение при попытке изменения мебели в пустом массиве
   * @throws IndexErrorException    - Исключение при неверном вводе индекса
   */
  private void editFurniture()
      throws NegativeFloatException, NegativeIntException, AssertionError, IndexErrorException {
    assert !furnitureList.isEmpty() : "Список мебели пуст";
    out.println("Введите индекс мебели в списке:");
    int index = inp.getInt();
    if (index < 1 || index > furnitureList.size()) {
      throw new IndexErrorException("Индекс указан неверно: ", index);
    }
    Furniture furnitureToEdit = this.furnitureList.get(index - 1);
    int choice;
    do {
      this.out.println("""
          1. Изменить название
          2. Изменить страну производитель
          3. Изменить год производства
          4. Изменить цену
          5. Выйти в меню
          """);
      choice = this.inp.getInt();
      switch (choice) {

        case CHANGE_NAME -> changeNameHandler(furnitureToEdit);
        case CHANGE_COUNTRY -> changeCountryHandler(furnitureToEdit);
        case CHANGE_YEAR -> changeProdYearHandler(furnitureToEdit);
        case CHANGE_PRICE -> changePriceHandler(furnitureToEdit);
        case EXIT_TO_MENU -> this.out.println("Редактирование завершено");
        default -> this.out.println("Некорректный ввод");
      }

    } while (choice != EXIT_TO_MENU);
  }

  /**
   * Метод изменяющий название выбранной мебели
   *
   * @param furnitureToEdit Изменяемый экземпляр мебели
   */
  private void changeNameHandler(Furniture furnitureToEdit) {
    this.out.println("Введите название:");
    furnitureToEdit.setName(this.inp.getString());
  }

  /**
   * Метод изменяющий страну выбранной мебели
   *
   * @param furnitureToEdit Изменяемый экземпляр мебели
   */
  private void changeCountryHandler(Furniture furnitureToEdit) {
    this.out.println("Введите страну производителя:");
    furnitureToEdit.setCountry(this.inp.getString());
  }


  /**
   * Метод изменяющий год производства выбранной мебели
   *
   * @param furnitureToEdit Изменяемый экземпляр мебели
   * @throws NegativeIntException - Исключение при неверном вводе года произвдства
   */
  private void changeProdYearHandler(Furniture furnitureToEdit)
      throws NegativeIntException {
    this.out.println("Введите год производства:");
    furnitureToEdit.setProdYear(this.inp.getInt());
  }

  /**
   * Метод изменяющий цену выбранной мебели
   *
   * @param furnitureToEdit Изменяемый экземпляр мебели
   * @throws NegativeFloatException - исключение при неверном вводе цены
   */
  private void changePriceHandler(Furniture furnitureToEdit) throws NegativeFloatException {
    this.out.println("Введите цену:");
    furnitureToEdit.setPrice(this.inp.getFloat());
  }

  /**
   * Метод вывода всего списка мебели
   */
  private void printFurnitureList() {
    IntStream.range(0, furnitureList.size()).forEach(x ->
        out.printf("Мебель №%d\n%s\n####################\n\n", x + 1, furnitureList.get(x)));
  }


  /**
   * Группировка мебели по стране - производителю
   */
  private Map<String, List<Furniture>> groupByCountry() {
    return furnitureList.stream()
        .filter(x -> x.getProdYear() != 0).
        collect(Collectors.groupingBy(Furniture::getCountry));
  }


  /**
   * Вывод сгруппированных значений
   */
  private void printGrouped() {
    Map<String, List<Furniture>> grouped = groupByCountry();
    grouped.forEach((key, item) -> {
      out.println("Страна: " + key);
      out.println("Мебель произведенная в этой стране: " + item.size());
      item.forEach((x) -> out.println("\n####################\n\n" + x));
      out.println("════════════════════════════════════════════\n");
    });
  }


  /**
   * Получение мебели с максимальной ценой
   */
  private void printMaxPrice() {
    Optional<Furniture> maxPriceFurniture =
        furnitureList.stream().max(Comparator.comparing(Furniture::getPrice));
    if (maxPriceFurniture.isPresent()) {
      out.println(maxPriceFurniture.get());
    } else {
      out.println("Некорректный список мебели");
    }
  }


  /**
   * Вывод максимального, среднего и минимального года производства
   */
  private void prodYearStat() {
    IntSummaryStatistics stat = furnitureList.stream().
        filter((x) -> x.getProdYear() != 0)
        .mapToInt(Furniture::getProdYear).
        summaryStatistics();
    out.printf("""
            Минимальный год производства: %d
            Средний год производства: %.0f
            Максимальный год производства: %d
            
            """, stat.getMin(), stat.getAverage(),
        stat.getMax());
  }

  /**
   * Суммарная цена всей мебели в списке
   */
  private void sumByPrice() {
    out.println(furnitureList.stream().reduce(0f, (x, y) -> x + y.getPrice(), (x, y) -> null));
  }

  /**
   * Метод удаления дубликатов из списка мебели
   */
  private void deleteDuplicates() {
    furnitureList = furnitureList.stream().distinct().
        collect(Collectors.toCollection(ArrayList<Furniture>::new));
  }

  /**
   * Метод фильтрации мебели по максимальной цене
   */
  private void filterByPrice() {
    out.println("Введите максимальную цену");

    float maxPrice = inp.getFloat();
    if (maxPrice < 0) {
      out.println("Некорректное значение");
    }
    furnitureList = furnitureList.stream().filter(furniture -> furniture.getPrice() < maxPrice)
        .collect(Collectors.toCollection(ArrayList<Furniture>::new));

  }
}