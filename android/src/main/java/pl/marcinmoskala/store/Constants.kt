package pl.marcinmoskala.store

import pl.marcinmoskala.store.model.Basket
import pl.marcinmoskala.store.model.Product
import pl.marcinmoskala.store.model.ProductSize

val basket = Basket()

val API_URL = "https://store-app.robovm.com/test/"
val UNKNOWN_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/en/e/ee/Unknown-person.gif"

val NRISE = Product("nRise", "Zajebisty produkt", 50.0, listOf("http://nootro.pl/wp/wp-content/uploads/2014/11/fd6ddd78-1f2d-4165-aaee-401f07b4e4cd-181x300.png"), listOf(ProductSize("0", "1 Buteleczka")))
val MUG = Product("Kubek nRise", "Kubek specjalnie skomponowany tak by przyjemność picia kawy była za każdym razem makymalna", 25.0, listOf("http://nootro.pl/wp/wp-content/uploads/2014/11/fd6ddd78-1f2d-4165-aaee-401f07b4e4cd-181x300.png"), listOf(ProductSize("0", "1 Buteleczka")))
val TSHIRT = Product("Koszulka Nootro", "Zajebisty produkt", 50.0, listOf("http://nootro.pl/wp/wp-content/uploads/2014/11/fd6ddd78-1f2d-4165-aaee-401f07b4e4cd-181x300.png"), listOf(ProductSize("0", "1 Buteleczka")))
val APP = Product("Aplikacja", "Stworzenie aplikacji", 50.0, listOf("http://nootro.pl/wp/wp-content/uploads/2014/11/fd6ddd78-1f2d-4165-aaee-401f07b4e4cd-181x300.png"), listOf(ProductSize("0", "1 Buteleczka")))
