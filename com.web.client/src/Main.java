import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

//Сервер
public class Main {
    //Создаем объект логгера
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    static {
        Handler handler = null;
        try {
            handler = new FileHandler("log.log");
            handler.setLevel(Level.ALL);
            LOGGER.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //Запись лога
        LOGGER.info("Server starts job....");

        try {
            //Определяем сервер сокет
            ServerSocket serverSocket = new ServerSocket(9090);
            //Создаем сервис на 10 потоков
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            //Создаем пять задач(тасков) с помощью цикла
            for (int i = 0; i < 5; i++){
                //Прописываем в логер какой поток стартовал
                LOGGER.info("Thread # " + i + " starts");
                //создаем поток
                executorService.submit(new Thread(() -> {
                    Socket clientSocket;

                    try {
                        //Бесконечный цикл для работы сервера
                        while (true) {
                            //сервер начинает прослушивать порт и задаем это сокету
                            clientSocket = serverSocket.accept();

                            //Request Запрос принимаем поток что пришло на сервер
                            InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());
                            Thread.sleep(100);

                            //Создаем StringBuilder сюда будем записывать запрос
                            StringBuilder requestString = new StringBuilder();
                            //Задаем цикл пока на input потоке есть данные мы добавляем в строку и делаем кастинг до char и читаем по байту
                            while (input.ready()) {
                                LOGGER.info("Read request...");
                                requestString.append((char) input.read());
                            }
                            //Выводим запрос который пришёл
                            System.out.println(requestString);

                            LocalDateTime localDateTime = LocalDateTime.now();
                            //Response ответ записываем в поток
                            OutputStreamWriter output = new OutputStreamWriter(clientSocket.getOutputStream());
                            //Записываем строку которую будем отдавать
                            String responseOutput = "HTTP/1.1 200 OK\n" +
                                    "Cache-Control: no cache\n" +
                                    "Sever: \n" +
                                    "Date: " + localDateTime + "\n" +
                                    "Connection: closed\n" +
                                    "Content_Type: application/json\n\n" +
                                    "{\"ok\" : \"" + (requestString.toString().length() > 100 ? "too long".toString() : requestString.toString()) + "\" }";
                            //Симуляция работы
                            Thread.sleep(1000);
                            //Записываем в поток наш ответ
                            output.write(responseOutput);
                            output.flush();
                            LOGGER.info("Sent message...");
                            input.close();
                            output.close();
                            clientSocket.close();


                        }

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }


                }));





            }
            Thread.sleep(1000000);
            serverSocket.close();
            LOGGER.info("Server close...");

        } catch (IOException  | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
