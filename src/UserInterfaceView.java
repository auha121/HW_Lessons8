import java.io.IOException;
import java.util.Scanner;

public class UserInterfaceView {
    private Controller controller = new Controller();

    private DataBaseRepository dataBaseRepository = new DataBaseRepository();

    public void runInterface() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1 - погода на сегодня; 5 - прогноз на 5 дней; 2 - данные из базы; 3 - первая запись из базы по городу; 0 - выход: ");
            String command = scanner.nextLine();
            if (command.equals("0")) {
                System.out.println("Завершение программы...");
                break;
            }
            else if (command.equals("1") || command.equals("5") || command.equals("3")) {
                System.out.println("Введите имя города: ");
                String city = scanner.nextLine();
                /*if (command.equals("3")) {
                    dataBaseRepository.getSavedToDBWeatherOne(city);
                }
                else {*/
                    try {
                        controller.getWeather(command, city);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //}
            }
            else if (command.equals("2")) {
                dataBaseRepository.getSavedToDBWeather();
            }
            else System.out.println("Вы ввели некоректное значение выбора");
        }
    }
}