import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;

//Клиент
public class Main
{
    public static void main(String[] args) {
        //IP 127.0.0.1 (localhost) : port 8080
        try {
            //Задаем сокет
            Socket clientSocket = new Socket("localhost",9090);
            //Request
            OutputStreamWriter output = new OutputStreamWriter(clientSocket.getOutputStream());
            output.write("Welcome to the Web server ..");
            output.flush();


            //Response
            //Записываем в буффер через тройную обертку поток от клиента
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //считываем буфер через стрим вот так
            bufferedReader.lines().forEach(System.out::println);

            bufferedReader.close();
            output.close();
            clientSocket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

