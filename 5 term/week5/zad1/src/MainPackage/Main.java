package MainPackage;

import MainPackage.KsiazkaDecorators.*;
import MainPackage.Wydawnictwa.Wydawnictwo;

public class Main {

    public static void main(String[] args) throws Exception {
        Publikacja a = new Ksiazka("author", "title", 123);
        Publikacja b = new KsiazkaZOkladkaZwykla(a);
        System.out.println(b);

        Publikacja k1 = new Ksiazka("Adam Mickiewicz", "Pan Tadeusz", 340);
        Publikacja k2 = new Ksiazka("Adam Mickiewicz", "Dziady", 130);
        Publikacja kk1 = new KsiazkaZOkladkaZwykla(k1);
        Publikacja kk2 = new KsiazkaZOkladkaTwarda(k2);
        // Publikacja fakeBook = new KsiazkaZObwoluta(k1);
        // wyjątek! Nie można obłożyć obwolutą książki, która nie posiada okładki
        Publikacja kkk2 = new KsiazkaZObwoluta(kk2); // a tu OK
        System.out.println(kkk2);
        // Publikacja odrzut = new KsiazkaZObwoluta(kkk2);
        // wyjątek! Obwoluta może być jedna

        // Publikacja odrzut = new KsiazkaZObwoluta(kkk2);
        // wyjątek! Obwoluta może być jedna
        Publikacja dziadyZAutografemWieszcza = new KsiazkaZAutografem(kk2, "Drogiej Hani - Adam Mickiewicz");
        System.out.println(dziadyZAutografemWieszcza);

        //Publikacja dziadyZDwomaAutografami = new KsiazkaZAutografem(dziadyZAutografemWieszcza, "Haniu, to nieprawda, Dziady napisałem ja, Julek Słowacki!");
        // wyjątek! Autograf może być tylko jeden

        //zad 2
        Wydawnictwo w = Wydawnictwo.getInstance("Józef Ignacy Kraszewski");
        /* W zależności od autora wybieramy odpowiednie wydawnictwo. Wpisać kilka wariantów.
        Tu powstanie wydawnictwo powieści historycznych */
        Ksiazka k = w.createBook("Masław", 280);
        System.out.println(k);
        /* Tworzy książkę klasy PowiescHistoryczna z podanym tytułem i liczbą
        stron. Autor przekazany będzie z wydawnictwa */

    }
}
